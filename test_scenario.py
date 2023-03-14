import mosaik
import mosaik.util

import os

import sys
sys.path.append('./scenario_python')
from scenario_imports import *

ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
OUTPUT_DIR = ROOT_DIR+'/out'
RESOURCES_FOLDER = ROOT_DIR+'/swsim/src/test/resources'
TRANSITION_CHAIN_FILE_NAME = 'transitionChain.json'
END = get_mosaik_end_value(RESOURCES_FOLDER+'/'+TRANSITION_CHAIN_FILE_NAME)

# Gather all .jar files inside the project
(dependencies, swsim_jar_path) = get_all_swsim_dependencies(ROOT_DIR)

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

# Create World
world = mosaik.World(SIM_CONFIG)

# Start simulators
software_simulator = world.start('SoftwareSimulator')
hardware_simulator = world.start('DummyHWSimulator')

# Instantiate models
sw_model = software_simulator.DFAWrapper(**{
        resource_folder_path_field:RESOURCES_FOLDER,
        dfa_file_name_field:'dfa.json',
        transition_to_binary_map_file_name_field:'binaryMap.json',
        transition_chain_file_name_field:TRANSITION_CHAIN_FILE_NAME
    }
)

hw_model = hardware_simulator.DummyHWModel()

# Connect the sw_model and the hw_model bi-directionally
world.connect(sw_model, hw_model, binary_path_field, binary_arguments_field)
world.connect(hw_model, sw_model, binary_execution_stats_field, weak=True)

# Run simulation
world.run(until=END)