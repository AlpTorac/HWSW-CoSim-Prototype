import sys
import evaluation_scenario
import evaluation_object
import os

eval_folder_name = 'eval'

if os.path.exists(os.path.dirname(os.path.abspath(__file__))+'/'+eval_folder_name):
    raise Exception('The evaluation cannot run, if an evaluation output folder already exists')

# Argument 1 = How many times the binary will run on WSL
number_of_binary_runs = int(sys.argv[1])
# Argument 2 = How many times the co-simulation evaluation will run
number_of_eval_runs = int(sys.argv[2])

avg_binary_time = 0
binary_with_args = 'ackermann 2 2000'
# Run the binary
for x in range(number_of_binary_runs):
    start_time = evaluation_object.get_current_system_time()
    os.system('./scenario-resources/evaluation-scenario-resources/'+binary_with_args)
    end_time = evaluation_object.get_current_system_time()
    avg_binary_time += end_time - start_time

print('\n Average runtime for '+binary_with_args+' is: ' + str((avg_binary_time/number_of_binary_runs) / 1000000000) + '\n')

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

# Run the co-simulation evaluation
for x in range(number_of_eval_runs):
    eval_scenario = evaluation_scenario.EvaluationScenario()

    eval_scenario.start_evaluation_time()

    eval_scenario.init_root_dir_path()
    eval_scenario.init_gem5_path()

    eval_scenario.init_output_dir_path(eval_folder_name+'/out'+str(x))
    eval_scenario.init_resources_dir_path('scenario-resources/evaluation-scenario-resources')

    eval_scenario.init_eval_output_file_path()

    eval_scenario.init_swsim_output_dir_path()
    eval_scenario.init_dfa_file_path()
    eval_scenario.init_transition_to_binary_map_file_path()
    eval_scenario.init_transition_chain_file_path()

    eval_scenario.init_swsim_eval_output_file_path()
    eval_scenario.init_swsim_output_description()

    eval_scenario.init_hwsim_output_dir_path()
    eval_scenario.init_hardware_script_file_path()

    eval_scenario.init_hwsim_eval_output_file_path()

    eval_scenario.init_sim_config()
    eval_scenario.init_world_end(7)

    eval_scenario.create_mosaik_world()
    eval_scenario.start_software_simulator()
    eval_scenario.start_hardware_simulator()
    eval_scenario.init_software_model()
    eval_scenario.init_hardware_model()
    eval_scenario.connect_models()
    eval_scenario.run_simulation()

    eval_scenario.end_simulation_time()

    eval_scenario.write_evaluation_output()