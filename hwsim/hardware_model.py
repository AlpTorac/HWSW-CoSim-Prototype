import os
import os.path
import shutil
class HardwareModel():
    def __init__(self, gem5_build_path, gem5_output_file_path,
    gem5_options, hardware_script_path, hardware_script_options):
        self.gem5_build_path = gem5_build_path
        self.gem5_output_file_path = gem5_output_file_path
        self.gem5_options = gem5_options
        self.hardware_script_path = hardware_script_path
        self.hardware_script_options = hardware_script_options

        self.binary_execution_stats_path = None
    
    def run_binary(self, binary_path, binary_arguments=''):
        self.current_binary_path = binary_path
        os.system(self.gem5_build_path + ' ' + self.gem5_options + ' '
        + self.hardware_script_path + ' ' + self.hardware_script_options
        + ' --binary_path=' + binary_path + ' ' + binary_arguments)

        # wait for the execution to finish
        while not os.path.isfile('/root/HWSW-CoSim-Prototype/hwsimOut/config.json'):
            if (os.path.isfile('/root/HWSW-CoSim-Prototype/hwsimOut/config.json')):
                break
        
        shutil.rmtree('/root/HWSW-CoSim-Prototype/hwsimOut')

        self.binary_execution_stats_path = self.gem5_output_file_path
    
    def get_execution_stats(self):
        result = self.binary_execution_stats_path
        self.binary_execution_stats_path = None
        return result

    def has_output(self):
        return self.binary_execution_stats_path != None