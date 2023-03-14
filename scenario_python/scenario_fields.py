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
                                    General
                                    
    Note: Fields used by swsim part will only affect python scenarios and leave
    its Java implementation unchanged. The Java implementation will also
    need to be changed manually.
    
    See swsim/src/main/java/hwswcosim/swsim/SoftwareSimulatorMosaikAPI.java
    for how each parameter given below is to be used.
    
    For the evaluation version (eval), see
    swsim/src/main/java/hwswcosim/swsim/evaluation/EvaluationSoftwareSimulatorMosaikAPI.java
    -----------------------------------------------------------------------
""" 

# general sim_params
eid_prefix_field = 'eid_prefix'

# general model_params
eid_field = 'eid'
type_field = 'type'

"""
    -----------------------------------------------------------------------
                                    swsim
                                    
    Note: Changes to swsim part will only affect python scenarios and leave
    its Java implementation unchanged. The Java implementation will also
    need to be changed manually.
    
    See swsim/src/main/java/hwswcosim/swsim/SoftwareSimulatorMosaikAPI.java
    for how each parameter given below is to be used.
    
    For the evaluation version (eval), see
    swsim/src/main/java/hwswcosim/swsim/evaluation/EvaluationSoftwareSimulatorMosaikAPI.java
    -----------------------------------------------------------------------
""" 
# swsim sim_params
software_simulator_output_dir_field='software_simulator_output_dir'
software_simulator_output_file_name_field='software_simulator_output_file_name'
software_simulator_output_desc_field='software_simulator_output_desc'

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
"""_summary_
The command, with which the hardware will be run from the terminal.
"""

output_path_field='output_path'
"""_summary_
The path, at which the hardware simulator will generate its output. Should
be passed as an argument, when hardware_simulator_run_command_field is called
from the terminal.
"""

hardware_script_run_command_field='hardware_script_run_command'
"""_summary_
The command, with which the hardware model that will run the simulation
will be built.
"""

# hwsim eval sim_params
hardware_simulator_eval_output_file_name = 'hardware_simulator_eval_output_file'
"""_summary_
The path, where the file will be created, in which this evaluation class
will generate its output.
"""

"""
    -----------------------------------------------------------------------
                                    agent
    -----------------------------------------------------------------------
""" 
# agent sim_params
agent_parameters_field='agent_parameters'
"""_summary_
A json object filled with parameters that will be used by
the agent.
"""

agent_output_dir_field = "agent_output_dir"
"""_summary_
The absolute path to the output directory as String, where the agent will generate its output.
"""

agent_output_file_name_field = "agent_output_file_name"
"""_summary_
The name and the extension of the output file of the agent.
"""

# agent eval sim_params
agent_eval_output_file_field = 'agent_eval_output_file'
"""_summary_
The path, where the file will be created, in which this evaluation class
will generate its output.
"""

"""
    -----------------------------------------------------------------------
                                    Attributes
    -----------------------------------------------------------------------
""" 
# swsim & hwsim attributes
binary_path_field='binary_file_path'
"""_summary_
The absolute path to the binary file as a String, which will be run by an outside component.
"""

binary_arguments_field='binary_file_arguments'
"""_summary_
Binary arguments that belong with the binary from binary_path_field as a list.
Any type can be given as argument. As of now, it is not possible to define variables
as arguments.

Format: [arg1, arg2, ..., arg3]
"""

binary_execution_stats_field='binary_execution_stats'
"""_summary_
Binary execution statistics received in either json object format (if there is only a
single statistics object) or json array of json object (if there can be multiple statistics objects. One
such json array can also have a single json object).

For each statistic, there is a name field (has to be String) and a value field (Any).

Format:
     json object: {"stat_name_1": stat_value_1, ..., "stat_name_N": stat_value_N}
     json array: [json_object_1, ..., json_object_M]
"""

# agent attributes
binary_path_input_field='binary_file_path_in'
"""_summary_
The absolute path to the binary file as a String, which is received.
"""

binary_path_output_field='binary_file_path_out'
"""_summary_
The absolute path to the binary file as a String, which will be sent.
"""

binary_execution_stats_input_field='binary_execution_stats_in'
"""_summary_
Binary execution statistics received in json object format.

stat_name_i have to be strings, stat_value_i can be of any type.

Format:
     json object: {"stat_name_1": stat_value_1, ..., "stat_name_N": stat_value_N}
"""

binary_execution_stats_output_field='binary_execution_stats_out'
"""_summary_
Binary execution statistics to be sent as json array of json object (one
such json array can contain one or more json object). Each said json object
contains statistics.

stat_name_i have to be strings, stat_value_i can be of any type.

Format:
     json object: {"stat_name_1": stat_value_1, ..., "stat_name_N": stat_value_N}
     json array: [json_object_1, ..., json_object_M]
"""

binary_arguments_input_field='binary_file_arguments_in'
"""_summary_
Binary arguments as a list that the agent receives.

Format: [arg1, arg2, ..., arg3]
"""

binary_arguments_output_field='binary_file_arguments_out'
"""_summary_
Binary arguments as a list that the agent sends.

Format: [arg1, arg2, ..., arg3]
"""