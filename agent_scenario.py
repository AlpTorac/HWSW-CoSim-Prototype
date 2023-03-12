import mosaik
import mosaik.util

import fnmatch
import os

import re

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
    'SoftwareSimulator': {
        'cmd': 'java -cp '+swsim_jar_path+dependencies+' hwswcosim.swsim.SoftwareSimulatorMosaikAPI %(addr)s',
    },
    'Agent': {
        'cmd': '%(python)s ./agent/agent_mosaik_API.py %(addr)s',
    },
    'HWSimulator': {
        'cmd': '%(python)s ./hwsim/hardware_simulator_mosaik_API.py %(addr)s',
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
RESOURCES_FOLDER = ROOT_DIR+'/'+'scenario-resources/agent-scenario-resources'

# Start simulators
# software_simulator_output_desc specifies how the software simulator should
# summarise the statistics received from the hardware simulator.
#
# Format: 'output_name': 'action'
# Actions:
#         add:  Sums up the values of the same desired global statistics
#               (output names that do not begin with "system.")
#         avg:  Computes the average value of the desired global statistics
#         none: Uses the first value it finds for output_name
#
software_simulator = world.start('SoftwareSimulator',software_simulator_output_dir=OUTPUT_DIR+'/swsimOut'
    , software_simulator_output_file_name='swsimOutput.txt'
    , software_simulator_output_desc={
    'simSeconds': 'add',
    'simFreq': 'none'
})

hardware_simulator = world.start('HWSimulator', variable_info=[{
                                                                'binary_name': 'ackermann2',
                                                                'binary_arg_pos': 0,
                                                                'binary_arg_min': 0,
                                                                'binary_arg_max': 300,
                                                                'binary_arg_shift_magnitude': 10,
                                                                'target_exec_time': 2,
                                                                'tolerance': 0.2,
                                                                'steps': 10,
                                                                },
                                                               {
                                                                'binary_name': 'ackermann3',
                                                                'binary_arg_pos': 0,
                                                                'binary_arg_min': 0,
                                                                'binary_arg_max': 10,
                                                                'binary_arg_shift_magnitude': 1,
                                                                'target_exec_time': 2,
                                                                'tolerance': 0.1,
                                                                'steps': 3,
                                                                }])

# Instantiate models
sw_model = software_simulator.DFAWrapper(
    resource_folder_path=RESOURCES_FOLDER,
    dfa_file_name='dfa.json',
    transition_to_binary_map_file_name='binaryMap.json',
    transition_chain_file_name='transitionChain.json')

hw_model = hardware_simulator.HWModel(
    hardware_simulator_run_command=GEM5_PATH,
    output_path=OUTPUT_DIR+'/hwsimOut',
    hardware_script_run_command=ROOT_DIR+'/hwsim/hardware_script.py')

world.connect(sw_model, hw_model, 'binary_file_path_out', 'binary_file_path_in')
world.connect(sw_model, hw_model, 'binary_file_arguments_out', 'binary_file_arguments_in')
world.connect(hw_model, sw_model, 'binary_execution_stats_out', 'binary_execution_stats_in', weak=True)

# Run simulation
world.run(until=END)