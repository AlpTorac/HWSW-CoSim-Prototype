import mosaik
import mosaik.util

import fnmatch
import os

# Get the root directory of the project
ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

# Gather all .jar files inside the project
dependencies = ''
for root, dirnames, filenames in os.walk(ROOT_DIR):
    for filename in fnmatch.filter(filenames, '*.jar'):
        dependencies += ':' + os.path.join(root, filename)

# Run the main(...) methods of the mosaik APIs
# with the dependencies gathered above
SIM_CONFIG = {
    'SoftwareSimulator': {
        'cmd': 'java -cp ./swsim/target/swsim-1.jar'+dependencies+' hwswcosim.swsim.SoftwareSimulatorMosaikAPI %(addr)s',
    },
    'DummyHWSimulator': {
        'cmd': 'java -cp ./swsim/target/swsim-1.jar'+dependencies+' hwswcosim.swsim.DummyHWSimulator %(addr)s',
    },
}
END = 7

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