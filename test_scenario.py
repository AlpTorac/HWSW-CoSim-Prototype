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

# Run the main(...) methods of the mosaik APIs
# with the dependencies gathered above
SIM_CONFIG = {
    'SoftwareSimulator': {
        'cmd': 'java -cp '+swsim_jar_path+dependencies+' hwswcosim.swsim.SoftwareSimulatorMosaikAPI %(addr)s',
    },
    'DummyHWSimulator': {
        'cmd': 'java -cp '+swsim_jar_path+dependencies+' hwswcosim.swsim.DummyHWSimulator %(addr)s',
    },
}

# End needs a buffer of at least 2 time steps, otherwise the software simulator
# cannot receive its last input from the hardware simulator.
# 
# Receiving input is a part of the step() method and if the time it outputs
# is >= END, step() will not be called again. Therefore, to ensure that step() is
# called to receive the last input, one has to give it a buffer of at least 2
# time steps.
END = 12

# Create World
world = mosaik.World(SIM_CONFIG)

# Start simulators
software_simulator = world.start('SoftwareSimulator', eid_prefix='SoftwareDFA_')
hardware_simulator = world.start('DummyHWSimulator', eid_prefix='HWModel_')

RESOURCES_FOLDER = ROOT_DIR+'/swsim/src/test/resources'

# Instantiate models
sw_model = software_simulator.DFAWrapper(
    dfa_file_path=RESOURCES_FOLDER+'/dfa.json',
    transition_to_binary_map_file_path=RESOURCES_FOLDER+'/binaryMap.json',
    transition_chain_file_path=RESOURCES_FOLDER+'/transitionChain.json')

hw_model = hardware_simulator.DummyHWModel()

world.connect(sw_model, hw_model, 'binary_file_path_out', 'binary_file_path_in')
world.connect(sw_model, hw_model, 'binary_file_arguments_out', 'binary_file_arguments_in')
world.connect(hw_model, sw_model, 'binary_execution_stats_out', 'binary_execution_stats_in', weak=True)

# Run simulation
world.set_initial_event(software_simulator._sim.sid, time=0)
world.run(until=END)