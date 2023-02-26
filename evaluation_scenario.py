import mosaik
import mosaik.util

import fnmatch
import os

import re

import sys

sys.path.append('./hwsim')
sys.path.append('./hwsim/evaluation')

import evaluation_object

start_time = evaluation_object.get_current_system_time()

# Get the root directory of the project
ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

# Gather all .jar files inside the project
dependencies = ''
swsim_jar_pattern = re.compile('swsim.*\.jar')
swsim_jar_path = ''
for root, dirnames, filenames in os.walk(ROOT_DIR):
    for filename in fnmatch.filter(filenames, '*.jar'):
        dependencies += ':' + os.path.join(root, filename)
        if swsim_jar_pattern.match(filename) is not None:
            swsim_jar_path = os.path.join(root, filename)

GEM5_PATH = ROOT_DIR+'/git-modules/gem5/build/X86/gem5.opt'

# Run the main(...) methods of the mosaik APIs
# with the dependencies gathered above
SIM_CONFIG = {
    'EvaluationSoftwareSimulator': {
        'cmd': 'java -cp '+swsim_jar_path+dependencies+' hwswcosim.swsim.evaluation.EvaluationSoftwareSimulatorMosaikAPI %(addr)s',
    },
    'EvaluationHWSimulator': {
        'cmd': '%(python)s ./hwsim/evaluation/evaluation_hardware_simulator_mosaik_API.py %(addr)s',
    },
}
# End needs a buffer of at least 2 time steps, otherwise the software simulator
# cannot receive its last input from the hardware simulator.
# 
# Receiving input is a part of the step() method and if the time it outputs
# is >= END, step() will not be called again. Therefore, to ensure that step() is
# called to receive the last input, one has to give it a buffer of at least 2
# time steps.
END = 9

# Create World
world = mosaik.World(SIM_CONFIG)

OUTPUT_DIR = ROOT_DIR+'/out'
SWSIM_OUTPUT_DIR = OUTPUT_DIR+'/swsimOut'
HWSIM_OUTPUT_DIR = OUTPUT_DIR+'/hwsimOut'

SWSIM_EVAL_OUTPUT_FILE = SWSIM_OUTPUT_DIR+'/swsimEvalOutput.txt'
HWSIM_EVAL_OUTPUT_FILE = HWSIM_OUTPUT_DIR+'/hwsimEvalOutput.txt'
EVAL_OUTPUT_FILE = OUTPUT_DIR+'/evalOutput.txt'

# Start simulators
software_simulator = world.start('EvaluationSoftwareSimulator',software_simulator_output_dir=SWSIM_OUTPUT_DIR
        , software_simulator_eval_output_file_path=SWSIM_EVAL_OUTPUT_FILE
        , software_simulator_output_desc={
    'simSeconds': 'add',
    'hostSeconds': 'add',
    'simTicks': 'add',
    'finalTick': 'add',
    'hostMemory': 'add',
    'simInsts': 'add',
    'simOps': 'add',
    'simFreq': 'none',
    'hostTickRate': 'avg',
    'hostInstRate': 'avg',
    'hostOpRate': 'avg'
})
hardware_simulator = world.start('EvaluationHWSimulator'
        , hardware_simulator_eval_output_file=HWSIM_EVAL_OUTPUT_FILE)

RESOURCES_FOLDER = ROOT_DIR+'/cosim-scenario'

# Instantiate models
sw_model = software_simulator.DFAWrapper(
    dfa_file_path=RESOURCES_FOLDER+'/dfa.json',
    transition_to_binary_map_file_path=RESOURCES_FOLDER+'/binaryMap.json',
    transition_chain_file_path=RESOURCES_FOLDER+'/transitionChain.json')

hw_model = hardware_simulator.HWModel(
    gem5_run_command=GEM5_PATH,
    gem5_output_path=HWSIM_OUTPUT_DIR,
    hardware_script_run_command=ROOT_DIR+'/hwsim/hardware_script.py')

world.connect(sw_model, hw_model, 'binary_file_path_out', 'binary_file_path_in')
world.connect(sw_model, hw_model, 'binary_file_arguments_out', 'binary_file_arguments_in')
world.connect(hw_model, sw_model, 'binary_execution_stats_out', 'binary_execution_stats_in', weak=True)

# Run simulation
world.run(until=END)

end_time = evaluation_object.get_current_system_time()

eval_output = open(EVAL_OUTPUT_FILE, 'x')
swsim_eval_output = open(SWSIM_EVAL_OUTPUT_FILE)
hwsim_eval_output = open(HWSIM_EVAL_OUTPUT_FILE)

eval_output.write('swsim evaluation outputs:\n')
eval_output.writelines(swsim_eval_output.readlines())
eval_output.write('\n')
eval_output.write('hwsim evaluation outputs:\n')
eval_output.writelines(hwsim_eval_output.readlines())
eval_output.write('\n')
eval_output.write('Entire simulation took: %.0f' % (end_time - start_time))

eval_output.close()
swsim_eval_output.close()
hwsim_eval_output.close()