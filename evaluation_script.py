import re
import sys
import evaluation_scenario
import evaluation_object
import os
import json

import subprocess

eval_folder_name = 'eval'

def format_number(number, prec=9):
    result = '%.{}f'.format(prec) % (float(number))

    # Get rid of trailing zeroes and decimal point, if needed
    if result.find('.') > -1:
        result = result.rstrip('0').rstrip('.')

    return result

def accumulate_outputs(relative_accumulation_file_path, output_file_paths, *additional_lines, **additional_kw_lines):
    accumulated_output_file = open(relative_accumulation_file_path, 'x')
    output_entries = {}

    # Capture group 1: output name
    # Capture group 2: output value (as number)
    output_entry_pattern = re.compile('(.*):\s*((?:\d|\.)+)')

    # Read files, parse output entries and add them to
    # entire_outputs
    for path in output_file_paths:
        file = open(path)
        for line in file.readlines():
            matches = output_entry_pattern.match(line)
            if matches is not None:
                output_name = matches.group(1)
                output_value = float(matches.group(2))

                if output_entries.get(output_name) is not None:
                    output_entries[output_name] += output_value
                else:
                    output_entries[output_name] = output_value
        file.close()
    
    # Take average of every output entry and
    # write them to a file with the given name
    for output_name, output_value in output_entries.items():
        output_entry = output_name+': '+format_number(float(output_value)/len(output_file_paths))
        
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

"""
Runs the given transition chain on the actual machine
run_multiplier time per co-simulation evaluation run

scenario_resources_tuples[0] = resource folder path
scenario_resources_tuples[1] = dfa file name
scenario_resources_tuples[2] = binary map file name
scenario_resources_tuples[3] = transition chain file name

returns the average run time per binary run (in nanoseconds)
"""
def run_transition_chain(scenario_resources_tuples, run_multiplier=1):
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

if os.path.exists(os.path.dirname(os.path.abspath(__file__))+'/'+eval_folder_name):
    raise Exception('The evaluation cannot run, if an evaluation output folder already exists')

# Argument 1 = How many times the co-simulation evaluation will run
number_of_eval_runs = int(sys.argv[1])

# Argument 2 = How many times the binaries simulated in co-simulation
# will run on the actual machine per co-simulation evaluation run
binary_run_multiplier = 1
if len(sys.argv) > 2 :
    binary_run_multiplier = int(sys.argv[2])

# ackermann(2, 2000) ~ 0.02s
# ackermann(2, 4000) ~ 0.076s
# ackermann(2, 4500) ~ 0.093s
# ackermann(2, 5000) ~ 0.114s
# ackermann(2, 10000) ~ 0.45s
# ackermann(2, 11000) ~ 0.54s
# ackermann(2, 12000) ~ 0.64s
# ackermann(2, 14000) ~ 0.88s
# ackermann(2, 15000) ~ 1s
# ackermann(2, 20000) ~ 1.8s
# ackermann(2, 21000) ~ 2s
# ackermann(2, 25000) ~ 2.8s

# co-simulation time ~ 1000 x binary runs on WSL

if number_of_eval_runs > 0:
    eval_output_file_paths = []
    swsim_output_file_paths = []
    resource_note_and_file_tuples = []

    resource_file_tuples = []

    # Run the co-simulation evaluation
    for x in range(number_of_eval_runs):
        eval_scenario = evaluation_scenario.EvaluationScenario()

        eval_scenario.start_evaluation_time()

        eval_scenario.set_root_dir_path()
        eval_scenario.set_gem5_path()

        eval_scenario.set_output_dir_path(eval_folder_name+'/out'+str(x))
        eval_scenario.set_resources_dir_path('scenario-resources/gem5-scenario-resources')
        eval_scenario.set_eval_output_file_path()

        eval_scenario.set_swsim_output_dir_path()
        eval_scenario.set_swsim_output_file_name()
        eval_scenario.set_dfa_file_name()
        eval_scenario.set_transition_to_binary_map_file_name()
        eval_scenario.set_transition_chain_file_name()

        eval_scenario.set_swsim_eval_output_file_path()
        eval_scenario.set_swsim_output_description()

        eval_scenario.set_hwsim_output_dir_path()
        eval_scenario.set_hardware_script_file_path()

        eval_scenario.set_hwsim_eval_output_file_path()

        eval_scenario.set_sim_config()
        eval_scenario.set_world_end(7)

        eval_scenario.create_mosaik_world()
        eval_scenario.start_software_simulator()
        eval_scenario.start_hardware_simulator()
        eval_scenario.init_software_model()
        eval_scenario.init_hardware_model()
        eval_scenario.connect_models()
        eval_scenario.run_simulation()

        eval_scenario.end_simulation_time()

        eval_scenario.write_evaluation_output()

        resource_file_tuples.append((eval_scenario.resources_folder_path,
                                     eval_scenario.dfa_file_name,
                                     eval_scenario.transition_to_binary_map_file_name,
                                     eval_scenario.transition_chain_file_name))

        resource_note_and_file_tuples.append(('DFA used in run='+str(x), eval_scenario.get_dfa_file_path()))
        resource_note_and_file_tuples.append(('Binary map used in run='+str(x), eval_scenario.get_transition_to_binary_map_file_path()))
        resource_note_and_file_tuples.append(('Transition chain used in run='+str(x), eval_scenario.get_transition_chain_file_path()))

        eval_output_file_paths.append(eval_scenario.get_eval_output_file_path())
        swsim_output_file_paths.append(eval_scenario.get_swsim_output_file_path())

    swsim_eval_outputs = eval_folder_name+'/'+'allSwsimOutputs.txt'
    swsim_output_dict = accumulate_outputs(swsim_eval_outputs, swsim_output_file_paths)

    # Take the average of every evalOutput.txt and swsimOutput.txt files
    # and write them into new files
    summarise_resources_used(eval_folder_name+'/'+'usedResources.txt', resource_note_and_file_tuples)

    accumulate_outputs(eval_folder_name+'/'+'allEvalOutputs.txt', eval_output_file_paths,
                    binary_run_on_host_count=str(number_of_eval_runs*binary_run_multiplier),
                    average_binary_run_time_on_host=format_number(run_transition_chain(resource_file_tuples, binary_run_multiplier)/1000000000),
                    simulation_run_count=str(number_of_eval_runs),
                    average_simulation_time=str(swsim_output_dict['simSeconds']))