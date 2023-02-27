import mosaik
import mosaik.util

import fnmatch
import os

import re

import sys

sys.path.append('./hwsim')
sys.path.append('./hwsim/evaluation')

import evaluation_object

class EvaluationScenario():
    def __init__(self):
        self.start_time = None
        self.end_time = None

        self.sim_config = None
        self.world_end = None
        self.world = None

        self.swsim = None
        self.hwsim = None

        self.sw_model = None
        self.hw_model = None

        self.root_dir_path = None
        self.gem5_path = None
        self.output_dir_path = None
        self.swsim_output_dir_path = None
        self.swsim_output_file_name = None
        self.hwsim_output_dir_path = None
        self.swsim_output_desc = None

        self.swsim_eval_output_file_path = None
        self.hwsim_eval_output_file_path = None
        self.eval_output_file_path = None

        self.resources_folder_path = None

        self.dfa_file_path = None
        self.transition_to_binary_map_file_path = None
        self.transition_chain_file_path = None

        self.hardware_script_file_path = None

    def get_hwsim_eval_output_file_path(self):
        return self.hwsim_eval_output_file_path

    def get_swsim_eval_output_file_path(self):
        return self.swsim_eval_output_file_path

    def get_swsim_output_file_path(self):
        return self.swsim_output_dir_path+'/'+self.swsim_output_file_name

    def get_eval_output_file_path(self):
        return self.eval_output_file_path

    def run_simulation(self):
        self.start_evaluation_time()

        self.create_mosaik_world()
        self.start_software_simulator()
        self.start_hardware_simulator()
        self.init_software_model()
        self.init_hardware_model()
        self.connect_models()
        self.run_simulation()
        
        self.end_simulation_time()

    def start_evaluation_time(self):
        self.start_time = evaluation_object.get_current_system_time()

    def set_root_dir_path(self):
        self.root_dir_path = os.path.dirname(os.path.abspath(__file__))

    def set_gem5_path(self, relative_gem5_path='git-modules/gem5/build/X86/gem5.opt'):
        self.gem5_path = self.root_dir_path+'/'+relative_gem5_path
    
    """
    End needs a buffer of at least 2 time steps, otherwise the software simulator
    cannot receive its last input from the hardware simulator. Hence the
    ending_time_step + 2 in the method.

    Receiving input is a part of the step() method and if the time it outputs
    is >= END, step() will not be called again. Therefore, to ensure that step() is
    called to receive the last input, one has to give it a buffer of at least 2
    time steps.

    last_time_step should be the equal to the time of the last transition in
    the transition chain.
    """
    def set_world_end(self, last_time_step):
        self.world_end = last_time_step + 2

    def set_sim_config(self):
        # Gather all .jar files inside the project
        dependencies = ''
        swsim_jar_pattern = re.compile('swsim.*\.jar')
        swsim_jar_path = ''
        for root, dirnames, filenames in os.walk(self.root_dir_path):
            for filename in fnmatch.filter(filenames, '*.jar'):
                dependencies += ':' + os.path.join(root, filename)
                if swsim_jar_pattern.match(filename) is not None:
                    swsim_jar_path = os.path.join(root, filename)

        self.sim_config = {
            'EvaluationSoftwareSimulator': {
                'cmd': 'java -cp '+swsim_jar_path+dependencies+' hwswcosim.swsim.evaluation.EvaluationSoftwareSimulatorMosaikAPI %(addr)s',
            },
            'EvaluationHWSimulator': {
                'cmd': '%(python)s ./hwsim/evaluation/evaluation_hardware_simulator_mosaik_API.py %(addr)s',
            },
        }
    
    def create_mosaik_world(self):
        self.world = mosaik.World(self.sim_config)

    def set_output_dir_path(self, dir_name='out'):
        self.output_dir_path = self.root_dir_path+'/'+dir_name
    
    def set_swsim_output_dir_path(self, dir_name='swsimOut'):
        self.swsim_output_dir_path = self.output_dir_path+'/'+dir_name
    
    def set_swsim_output_file_name(self, file_name='swsimOutput.txt'):
        self.swsim_output_file_name = file_name

    def set_hwsim_output_dir_path(self, dir_name="hwsimOut"):
        self.hwsim_output_dir_path = self.output_dir_path+'/'+dir_name
    
    def set_swsim_eval_output_file_path(self, file_name='swsimEvalOutput.txt'):
        self.swsim_eval_output_file_path = self.swsim_output_dir_path+'/'+file_name
    
    def set_hwsim_eval_output_file_path(self, file_name='hwsimEvalOutput.txt'):
        self.hwsim_eval_output_file_path = self.hwsim_output_dir_path+'/'+file_name
    
    def set_eval_output_file_path(self, file_name='evalOutput.txt'):
        self.eval_output_file_path = self.output_dir_path+'/'+file_name

    def set_swsim_output_description(self):
        self.swsim_output_desc = {
                'simSeconds': 'add',
                'hostSeconds': 'add',
                'simTicks': 'add',
                'finalTick': 'add',
                'hostMemory': 'add',
                'simInsts': 'add',
                'simOps': 'add',
                'simFreq': 'none',
                'hostTickRate': 'avg',
                'hostInstRate': 'avg',
                'hostOpRate': 'avg'
            }

    def start_software_simulator(self):
        self.swsim = self.world.start('EvaluationSoftwareSimulator',software_simulator_output_dir=self.swsim_output_dir_path
            , software_simulator_output_file_name=self.swsim_output_file_name
            , software_simulator_eval_output_file_path=self.swsim_eval_output_file_path
            , software_simulator_output_desc=self.swsim_output_desc)
    
    def start_hardware_simulator(self):
        self.hwsim = self.world.start('EvaluationHWSimulator'
            , hardware_simulator_eval_output_file=self.hwsim_eval_output_file_path)

    def set_resources_dir_path(self, relative_path):
        self.resources_folder_path = self.root_dir_path+'/'+relative_path
    
    def set_dfa_file_path(self, file_name='dfa.json'):
        self.dfa_file_path = self.resources_folder_path+'/'+file_name

    def set_transition_to_binary_map_file_path(self, file_name='binaryMap.json'):
        self.transition_to_binary_map_file_path = self.resources_folder_path+'/'+file_name
    
    def set_transition_chain_file_path(self, file_name='transitionChain.json'):
        self.transition_chain_file_path = self.resources_folder_path+'/'+file_name

    def init_software_model(self):
        self.sw_model = self.swsim.DFAWrapper(
            dfa_file_path=self.dfa_file_path,
            transition_to_binary_map_file_path=self.transition_to_binary_map_file_path,
            transition_chain_file_path=self.transition_chain_file_path)

    def set_hardware_script_file_path(self, relative_path='hwsim/hardware_script.py'):
        self.hardware_script_file_path = self.root_dir_path+'/'+relative_path

    def init_hardware_model(self):
        self.hw_model = self.hwsim.HWModel(
            gem5_run_command=self.gem5_path,
            gem5_output_path=self.hwsim_output_dir_path,
            hardware_script_run_command=self.hardware_script_file_path)

    def connect_models(self):
        self.world.connect(self.sw_model, self.hw_model, 'binary_file_path_out', 'binary_file_path_in')
        self.world.connect(self.sw_model, self.hw_model, 'binary_file_arguments_out', 'binary_file_arguments_in')
        self.world.connect(self.hw_model, self.sw_model, 'binary_execution_stats_out', 'binary_execution_stats_in', weak=True)

    def run_simulation(self):
        self.world.run(until=self.world_end)

    def end_simulation_time(self):
        self.end_time = evaluation_object.get_current_system_time()

    def write_evaluation_output(self):
        eval_output = open(self.eval_output_file_path, 'x')
        swsim_eval_output = open(self.swsim_eval_output_file_path)
        hwsim_eval_output = open(self.hwsim_eval_output_file_path)

        eval_output.write('swsim evaluation outputs:\n')
        eval_output.writelines(swsim_eval_output.readlines())
        eval_output.write('\n')
        eval_output.write('hwsim evaluation outputs:\n')
        eval_output.writelines(hwsim_eval_output.readlines())
        eval_output.write('\n')
        eval_output.write('Entire simulation took: %.0f' % (self.end_time - self.start_time))

        eval_output.close()
        swsim_eval_output.close()
        hwsim_eval_output.close()