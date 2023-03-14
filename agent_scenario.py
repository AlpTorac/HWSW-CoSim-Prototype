import mosaik
import mosaik.util

import os

import sys
sys.path.append('./scenario_python')
from scenario_imports import *

ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
GEM5_PATH = ROOT_DIR+'/git-modules/gem5/build/X86/gem5.opt'
OUTPUT_DIR = ROOT_DIR+'/out'
RESOURCES_FOLDER = ROOT_DIR+'/'+'scenario-resources/agent-scenario-resources'
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
    'Agent': {
        'cmd': '%(python)s ./agent/agent_mosaik_API.py %(addr)s',
    },
    'HWSimulator': {
        'cmd': '%(python)s ./hwsim/hardware_simulator_mosaik_API.py %(addr)s',
    },
}

# Create World
world = mosaik.World(SIM_CONFIG)

# Start the simulators
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

agent_API = world.start('Agent',
    **{
        agent_output_dir_field:OUTPUT_DIR+'/agentOut',
        agent_output_file_name_field:'agentOutput.txt'
    }
)

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

agent = agent_API.Agent(
    **{
        agent_parameters_field:[{
                                                                binary_name_field: 'ackermann2',
                                                                binary_arg_pos_field: 0,
                                                                binary_arg_min_field: 0,
                                                                binary_arg_max_field: 300,
                                                                binary_arg_shift_magnitude_field: 10,
                                                                binary_stat_criterium_field: 'hostSeconds',
                                                                criterium_target_field: 0.5,
                                                                tolerance_field: 0.05,
                                                                max_runs_field: 5,
                                                                },
                                                               {
                                                                binary_name_field: 'ackermann3',
                                                                binary_arg_pos_field: 0,
                                                                binary_arg_min_field: 0,
                                                                binary_arg_max_field: 10,
                                                                binary_arg_shift_magnitude_field: 1,
                                                                binary_stat_criterium_field: 'hostSeconds',
                                                                criterium_target_field: 0.5,
                                                                tolerance_field: 0.01,
                                                                max_runs_field: 3,
                                                                }]
    }
)

# Connect the agent with sw_model bi-directionally
world.connect(sw_model, agent,
              (binary_path_field, binary_path_input_field),
              (binary_arguments_field, binary_arguments_input_field))
world.connect(agent, sw_model,
              (binary_execution_stats_output_field, binary_execution_stats_field), weak=True)

# Connect the agent with hw_model bi-directionally
world.connect(agent, hw_model,
              (binary_path_output_field, binary_path_field),
              (binary_arguments_output_field, binary_arguments_field))
world.connect(hw_model, agent,
              (binary_execution_stats_field, binary_execution_stats_input_field), weak=True)

# Run simulation
world.run(until=END)