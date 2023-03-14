"""_summary_
This file contains parameter and attribute names used by mosaik scenarios as well as
their partaking components.
"""

from agent_parameter_names import *
from swsim_desc import *
from hwsim_output_stat_names import *

"""
    -----------------------------------------------------------------------
                                    Params
    -----------------------------------------------------------------------
""" 

"""
    -----------------------------------------------------------------------
                                    swsim
    -----------------------------------------------------------------------
""" 
# swsim sim_params
software_simulator_output_dir_field='software_simulator_output_dir'
software_simulator_output_file_name_field='software_simulator_output_file_name'
software_simulator_output_desc_field='software_simulator_output_desc'
"""_summary_
Specifies how the software simulator should reduce the statistics
received from the hardware simulator.

Format: 'output_name': 'action'
"""

# swsim model_params
resource_folder_path_field='resource_folder_path'
dfa_file_name_field='dfa_file_name'
transition_to_binary_map_file_name_field='transition_to_binary_map_file_name'
transition_chain_file_name_field='transition_chain_file_name'

# swsim eval sim_params
software_simulator_eval_output_file_path_field = "software_simulator_eval_output_file_path"

"""
    -----------------------------------------------------------------------
                                    hwsim
    -----------------------------------------------------------------------
""" 
# hwsim sim_params
hardware_simulator_run_command_field='hardware_simulator_run_command'
output_path_field='output_path'
hardware_script_run_command_field='hardware_script_run_command'

# hwsim eval sim_params
hardware_simulator_eval_output_file_name = 'hardware_simulator_eval_output_file'

"""
    -----------------------------------------------------------------------
                                    agent
    -----------------------------------------------------------------------
""" 
# agent sim_params
agent_parameters_field='agent_parameters'
agent_output_dir_field = "agent_output_dir"
agent_output_file_name_field = "agent_output_file_name"

# agent model_params
binary_path_input_field='binary_file_path_in'
binary_path_output_field='binary_file_path_out'
binary_execution_stats_input_field='binary_execution_stats_in'
binary_execution_stats_output_field='binary_execution_stats_out'
binary_arguments_input_field='binary_file_arguments_in'
binary_arguments_output_field='binary_file_arguments_out'

# agent eval sim_params
agent_eval_output_file_field = 'agent_eval_output_file'

"""
    -----------------------------------------------------------------------
                                    Attributes
    -----------------------------------------------------------------------
""" 
# swsim & hwsim attributes
binary_path_field='binary_file_path'
binary_arguments_field='binary_file_arguments'
binary_execution_stats_field='binary_execution_stats'