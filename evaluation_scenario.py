import mosaik
import mosaik.util

import fnmatch
import os

import re

import sys

import json

sys.path.append('./hwsim')
sys.path.append('./hwsim/evaluation')
sys.path.append('./evaluation_python')
sys.path.append('./scenario_python')

from scenario_fields import *
import evaluation_object

class EvaluationScenario():
    def __init__(self):
        self.start_time = None
        self.end_time = None

    def run_scenario_script(self, run_number, relative_scenario_resources_path, **file_paths):
        # Get the root directory of the project
        ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
        RESOURCES_FOLDER = ROOT_DIR+relative_scenario_resources_path
        GEM5_PATH = ROOT_DIR+'/git-modules/gem5/build/X86/gem5.opt'
        OUTPUT_DIR = ROOT_DIR+'/eval/out'+str(run_number)
        EVAL_OUTPUT_FILE_PATH = OUTPUT_DIR+'/evalOutput.txt'
        
        SWSIM_OUTPUT_DIR=OUTPUT_DIR+'/swsimOut'
        SWSIM_OUTPUT_FILE_NAME='swsimOutput.txt'
        SWSIM_OUTPUT_FILE_PATH = SWSIM_OUTPUT_DIR+'/'+SWSIM_OUTPUT_FILE_NAME
        SWSIM_EVAL_OUTPUT_FILE_PATH = OUTPUT_DIR+'/swsimEvalOutput.txt'
        
        HWSIM_EVAL_OUTPUT_FILE_PATH = OUTPUT_DIR+'/hwsimEvalOutput.txt'
        
        AGENT_OUTPUT_DIR = OUTPUT_DIR+'/agentOut'
        AGENT_OUTPUT_FILE_NAME = 'agentOutput.txt'
        AGENT_EVAL_OUTPUT_FILE_PATH = OUTPUT_DIR+'/agentEvalOutput.txt'

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
            'EvaluationSoftwareSimulator': {
                'cmd': 'java -cp '+swsim_jar_path+dependencies+' hwswcosim.swsim.evaluation.EvaluationSoftwareSimulatorMosaikAPI %(addr)s',
            },
            'EvaluationAgent': {
                'cmd': '%(python)s ./agent/evaluation/evaluation_agent_mosaik_API.py %(addr)s',
            },
            'EvaluationHWSimulator': {
                'cmd': '%(python)s ./hwsim/evaluation/evaluation_hardware_simulator_mosaik_API.py %(addr)s',
            },
        }
        
        
        # End needs a buffer of at least 2 time steps, otherwise the software simulator
        # cannot receive its last input from the hardware simulator.
        # 
        # Receiving input is a part of the step() method and if the time it outputs
        # is >= END, step() will not be called again. Therefore, to ensure that step() is
        # called to receive the last input, one has to give it a buffer of at least 2
        # time steps.
        transition_chain_file = open(RESOURCES_FOLDER+'/transitionChain.json')
        END = int(((json.loads(transition_chain_file.read()))[-1])['time']) + 2
        transition_chain_file.close()

        start_time = evaluation_object.get_current_system_time()

        # Create World
        world = mosaik.World(SIM_CONFIG)


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
        
        software_simulator = world.start('EvaluationSoftwareSimulator',
            **{
                software_simulator_output_dir_field:SWSIM_OUTPUT_DIR,
                software_simulator_output_file_name_field:SWSIM_OUTPUT_FILE_NAME,
                software_simulator_eval_output_file_path_field:SWSIM_EVAL_OUTPUT_FILE_PATH,
                software_simulator_output_desc_field:{
                    simSeconds_field: add_operation,
                    hostSeconds_field: add_operation,
                    simTicks_field: add_operation,
                    finalTick_field: add_operation,
                    hostMemory_field: add_operation,
                    simInsts_field: add_operation,
                    simOps_field: add_operation,
                    simFreq_field: no_operation,
                    hostTickRate_field: average_operation,
                    hostInstRate_field: average_operation,
                    hostOpRate_field: average_operation
                }
            }
        )
        
        agent_API = world.start('EvaluationAgent',
            **{
                agent_output_dir_field:AGENT_OUTPUT_DIR,
                agent_output_file_name_field:AGENT_OUTPUT_FILE_NAME,
                agent_eval_output_file_field:AGENT_EVAL_OUTPUT_FILE_PATH
            }
        )
        
        hardware_simulator = world.start('EvaluationHWSimulator'
            , hardware_simulator_eval_output_file=HWSIM_EVAL_OUTPUT_FILE_PATH)

        dfa_file_name='dfa.json'
        transition_to_binary_map_file_name='binaryMap.json'
        transition_chain_file_name='transitionChain.json'

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
        
        end_time = evaluation_object.get_current_system_time()
        
        eval_output = open(EVAL_OUTPUT_FILE_PATH, 'x')
        swsim_eval_output = open(SWSIM_EVAL_OUTPUT_FILE_PATH)
        hwsim_eval_output = open(HWSIM_EVAL_OUTPUT_FILE_PATH)
        agent_eval_output = open(AGENT_EVAL_OUTPUT_FILE_PATH)

        eval_output.write('swsim evaluation outputs:\n')
        eval_output.writelines(swsim_eval_output.readlines())
        eval_output.write('\n')
        eval_output.write('hwsim evaluation outputs:\n')
        eval_output.writelines(hwsim_eval_output.readlines())
        eval_output.write('\n')
        eval_output.write('agent evaluation outputs:\n')
        eval_output.writelines(agent_eval_output.readlines())
        eval_output.write('\n')
        eval_output.write('Entire simulation took: %.0f' % (end_time - start_time))

        eval_output.close()
        swsim_eval_output.close()
        hwsim_eval_output.close()
        agent_eval_output.close()

        file_paths['resource_file_tuples'].append((RESOURCES_FOLDER,
                                     dfa_file_name,
                                     transition_to_binary_map_file_name,
                                     transition_chain_file_name))

        file_paths['resource_note_and_file_tuples'].append(('DFA used in run='+str(run_number), RESOURCES_FOLDER+'/'+dfa_file_name))
        file_paths['resource_note_and_file_tuples'].append(('Binary map used in run='+str(run_number), RESOURCES_FOLDER+'/'+transition_to_binary_map_file_name))
        file_paths['resource_note_and_file_tuples'].append(('Transition chain used in run='+str(run_number), RESOURCES_FOLDER+'/'+transition_chain_file_name))

        file_paths['eval_output_file_paths'].append(EVAL_OUTPUT_FILE_PATH)
        file_paths['swsim_output_file_paths'].append(SWSIM_OUTPUT_FILE_PATH)