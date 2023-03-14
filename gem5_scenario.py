import mosaik
import mosaik.util

import fnmatch
import os

import re

import sys
sys.path.append('./scenario_python')
from scenario_fields import *

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
software_simulator = world.start('SoftwareSimulator',
    **{
        software_simulator_output_dir_field:OUTPUT_DIR+'/swsimOut',
        software_simulator_output_file_name_field:'swsimOutput.txt',
        software_simulator_output_desc_field:{
            simSeconds_field: add_operation,
            simFreq_field: no_operation
        }
    }
)
hardware_simulator = world.start('HWSimulator')

RESOURCES_FOLDER = ROOT_DIR+'/'+'scenario-resources/gem5-scenario-resources'

# Instantiate models
sw_model = software_simulator.DFAWrapper(**{
        resource_folder_path_field:RESOURCES_FOLDER,
        dfa_file_name_field:'dfa.json',
        transition_to_binary_map_file_name_field:'binaryMap.json',
        transition_chain_file_name_field:'transitionChain.json'
    }
)

hw_model = hardware_simulator.HWModel(**{
        hardware_simulator_run_command_field:GEM5_PATH,
        output_path_field:OUTPUT_DIR+'/hwsimOut',
        hardware_script_run_command_field:ROOT_DIR+'/hwsim/hardware_script.py'
    }
)

# Connect the sw_model and the hw_model bi-directionally
world.connect(sw_model, hw_model, binary_path_field, binary_arguments_field)
world.connect(hw_model, sw_model, binary_execution_stats_field, weak=True)

# Run simulation
world.run(until=END)