import re

import sys
sys.path.append('./evaluation_python')
sys.path.append('./hwsim')
import evaluation_object

import evaluation_scenario
import os
import json

import subprocess

# Can be run with "python3 evaluation_script.py <number_of_eval_runs> <binary_run_multiplier>"
#
# number_of_eval_runs: How many times the co-simulation evaluation will run
#
# binary_run_multiplier (optional, default value=1): How many times the binaries simulated in
# co-simulation will run on the actual machine per co-simulation evaluation run
number_of_eval_runs = int(sys.argv[1])
binary_run_multiplier = 1
if len(sys.argv) > 2 :
    binary_run_multiplier = int(sys.argv[2])

eval_folder_name = 'eval'
# Check if an evaluation folder already exists
if os.path.exists(os.path.dirname(os.path.abspath(__file__))+'/'+eval_folder_name):
    raise Exception('The evaluation cannot run, if an evaluation output folder already exists')

def format_number(number, prec=9):
    result = '%.{}f'.format(prec) % (float(number))

    # Get rid of trailing zeroes and decimal point, if needed
    if result.find('.') > -1:
        result = result.rstrip('0').rstrip('.')

    return result

def reduce_outputs(output_file_paths, output_entry_finding_operation, binary_reduce_operation):
    """_summary_

    Reads all files given in output_file_paths, finds each line that matches the pattern
    and performs the given binary operation to reduce all output_values found for the
    same output_name. 

    Args:
        output_file_paths (_type_): List of absolute paths to output files
        output_entry_finding_operation (_type_): An operation, which takes a String as
        input and returns the tuple (output_name, output_value) or None, if no such tuple
        exists in the String given
        binary_reduce_operation (_type_): A binary operation, which will be used to reduce
        output values
    
    Returns:
        _type_: A dict of outputs reduced using the way described above
    """
    output_entries = {}

    # Read files, parse output entries and add them to
    # entire_outputs
    for path in output_file_paths:
        file = open(path)
        for line in file.readlines():
            output_entry = output_entry_finding_operation(line)
            if output_entry is not None:
                (output_name, output_value) = output_entry
                if output_entries.get(output_name) is not None:
                    output_entries[output_name] = binary_reduce_operation(output_entries[output_name], output_value)
                else:
                    output_entries[output_name] = output_value
        file.close()
    
    return output_entries

def get_output_entry_dict_item_format(line):
    """_summary_
    An operation, which takes a String as input and returns the tuple
    (output_name, output_value) or None, if no such tuple exists in
    the String given.
    
    Uses a similar format to dict items: "key: value"
    
    Args:
        line (_type_): The String that potentially contains an output entry

    Returns:
        _type_: (output_name, output_value) tuple or None, if no such tuple
        was found
    """
    # Capture group 1: output name
    # Capture group 2: output value (as number)
    output_entry_pattern = re.compile('(.*):\s*((?:\d|\.)+)')
    matches = output_entry_pattern.match(line)
    if matches is not None:
        output_name = matches.group(1)
        output_value = float(matches.group(2))
        return (output_name, output_value)
    return None

def sum(x, y):
    return x + y

def average_final_output_entry(**kw_args):
    """_summary_
    An operation that only takes **kw_args as input to transform a given preliminary
    output entry to the String of the final version of the said output entry.
    
    Returns:
        _type_: The output entry in String: "output_name: output_value/output_file_count"
    """
    return kw_args['output_name']+': '+format_number(float(kw_args['output_value'])/len(kw_args['output_file_paths']))

def accumulate_outputs(relative_accumulation_file_path, output_file_paths
                       ,output_entry_finding_operation, binary_reduce_operation
                       ,compute_final_output_entry
                       ,*additional_lines, **additional_kw_lines):
    """_summary_

    Reads all files given in output_file_paths, acquires each output_entry (output_name, output_value)
    using the given output_entry_finding_operation, reduces all output_entries that have the same
    output_name with binary_reduce_operation then performs compute_final_output_entry to get the final
    version of the output_entry as String and finally writes all such output entries to the file at the
    absolute path relative_accumulation_file_path

    Args:
        relative_accumulation_file_path (_type_): The absolute path to the file that contains
        the accumulated outputs
        output_file_paths (_type_): Absolute paths to all output files that will be accumulated
        output_entry_finding_operation (_type_): An operation, which takes a String as
        input and returns the tuple (output_name, output_value) or None, if no such tuple
        exists in the String given
        binary_reduce_operation (_type_): A binary operation, which will be used to reduce
        output values
        compute_final_output_entry (_type_): An operation that only takes **kw_args as input
        to transform a given preliminary output entry to the String of the final version of
        the said output entry
        additional_lines (_type_): List of Strings that will be written after all output entries
        additional_kw_lines (_type_): Dict of Strings that will be written after additional_lines
        in the form "key: value"

    Returns:
        _type_: The accumulated output file described above
    """
    accumulated_output_file = open(relative_accumulation_file_path, 'x')
    
    output_entries = reduce_outputs(output_file_paths,
                                    get_output_entry_dict_item_format,
                                    sum)
    
    # Take average of every output entry and
    # write them to a file with the given name
    for output_name, output_value in output_entries.items():
        output_entry = compute_final_output_entry(output_name=output_name, output_value=output_value, output_file_paths=output_file_paths)
        
        accumulated_output_file.write(output_entry+'\n')

    if additional_lines is not None:
        accumulated_output_file.write('\n')

        for line in additional_lines:
            accumulated_output_file.write(line+'\n')

    if additional_kw_lines is not None:
        accumulated_output_file.write('\n')

        for line_desc, line in additional_kw_lines.items():
            accumulated_output_file.write(line_desc+': '+line+'\n')

    accumulated_output_file.close()
    return output_entries

def summarise_resources_used(relative_file_path, resource_note_and_file_tuples):
    """_summary_
    Reads, concatenates and re-writes all given resources to a new file.
    
    Args:
        relative_file_path (_type_): The absolute path of the file, which will be
        created
        resource_note_and_file_tuples (_type_): A list of (resource_note, resource_path)
        tuples, where resource_note is a String that will be written before the resource
        and the resource_path is the absolute path to the said resource.
    """
    resources_file = open(relative_file_path, 'x')

    # Read files, parse output entries and add them to
    # entire_outputs
    for (resource_note, resource_path) in resource_note_and_file_tuples:
        file = open(resource_path)
        resources_file.write(resource_note+'\n\n')
        resources_file.writelines(file.readlines())
        resources_file.write('\n\n')
        file.close()

    resources_file.close()

def run_transition_chain(scenario_resources_tuples, run_multiplier=1):
    """_summary_
    Runs the given transition chain on the actual machine
    run_multiplier time per co-simulation evaluation run
    
    Args:
        scenario_resources_tuples (_type_): A tuple, where:
            scenario_resources_tuples[0] = resource folder path
            scenario_resources_tuples[1] = dfa file name
            scenario_resources_tuples[2] = binary map file name
            scenario_resources_tuples[3] = transition chain file name
            
        run_multiplier (_type_): The amount of time a binary will be run
        on the actual machine (not co-simulation) per co-simulation evaluation
        run
        
    Returns:
        _type_: The average run time per binary run
    """
    transition_pattern = re.compile('\((\w+),(\w+),(\w)\)')
    total_binary_run_time = 0

    for x in range(run_multiplier):
        for resource_tuple in scenario_resources_tuples:
            resource_folder_path = resource_tuple[0]
            dfa_file_path = resource_folder_path + '/' + resource_tuple[1]
            transition_to_binary_map_file_path = resource_folder_path + '/' + resource_tuple[2]
            transition_chain_file_path = resource_folder_path + '/' + resource_tuple[3]

            print(dfa_file_path+'\n'+transition_to_binary_map_file_path+'\n'+transition_chain_file_path)

            dfa_file = open(dfa_file_path)
            binary_map_file = open(transition_to_binary_map_file_path)
            transition_chain_file = open(transition_chain_file_path)

            dfa = json.loads(dfa_file.read())
            binary_map = json.loads(binary_map_file.read())
            transition_chain = json.loads(transition_chain_file.read())

            state = dfa['start_state']

            for transition in transition_chain:
                input = transition['input']
                binary_path = None
                binary_args = None

                for binary_map_entry in binary_map:
                    transition_field = binary_map_entry['transition']

                    matcher = transition_pattern.match(transition_field)

                    origin_state = matcher.group(1)
                    target_state = matcher.group(2)
                    transition_input = matcher.group(3)

                    if origin_state == state and transition_input == input:
                        state = target_state
                        binary_path = resource_folder_path + '/' + binary_map_entry['binary']

                        if binary_map_entry.get('arguments') is not None:
                            binary_args = binary_map_entry['arguments']
                        
                        break

                command_list = [binary_path]

                if binary_args is not None:
                    command_list = command_list + binary_args

                start_time = evaluation_object.get_current_system_time()
                subprocess.run(command_list)
                end_time = evaluation_object.get_current_system_time()
                total_binary_run_time += end_time - start_time
            
            dfa_file.close()
            binary_map_file.close()
            transition_chain_file.close()

    print('total run time = ' + str(total_binary_run_time))
    print('average run time = ' + str(total_binary_run_time/(len(scenario_resources_tuples)*run_multiplier)))

    # Compute the average run time of the binary
    return (total_binary_run_time/(len(scenario_resources_tuples)*run_multiplier))


# Run the evaluation
if number_of_eval_runs > 0:
    eval_output_file_paths = []
    swsim_output_file_paths = []
    resource_note_and_file_tuples = []

    resource_file_tuples = []

    # Run the co-simulation evaluation
    for x in range(number_of_eval_runs):
        eval_scenario = evaluation_scenario.EvaluationScenario()
        eval_scenario.run_scenario_script(x, '/scenario-resources/agent-scenario-resources',
            eval_output_file_paths=eval_output_file_paths,
            swsim_output_file_paths=swsim_output_file_paths,
            resource_note_and_file_tuples=resource_note_and_file_tuples,
            resource_file_tuples=resource_file_tuples)

    # Summarise all swsimOutput.txt files by computing average values for each field
    swsim_eval_outputs = eval_folder_name+'/'+'allSwsimOutputs.txt'
    swsim_output_dict = accumulate_outputs(swsim_eval_outputs, swsim_output_file_paths
                        ,get_output_entry_dict_item_format, sum
                        ,average_final_output_entry)
    
    # Write down all resources used throughout the evaluation
    summarise_resources_used(eval_folder_name+'/'+'usedResources.txt', resource_note_and_file_tuples)

    # Take the average of every evalOutput.txt file and write them into new a new file
    accumulate_outputs(eval_folder_name+'/'+'allEvalOutputs.txt', eval_output_file_paths,
                    get_output_entry_dict_item_format, sum, average_final_output_entry,
                    binary_run_on_host_count=str(number_of_eval_runs*binary_run_multiplier),
                    average_binary_run_time_on_host=format_number(run_transition_chain(resource_file_tuples, binary_run_multiplier)/1000000000),
                    simulation_run_count=str(number_of_eval_runs),
                    average_simulation_time=str(swsim_output_dict['simSeconds']))