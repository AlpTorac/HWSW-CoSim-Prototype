import os
import os.path
import shutil
class HardwareModel():
    def __init__(self, gem5_run_command, gem5_output_path,
    hardware_script_run_command):
        self.gem5_run_command = gem5_run_command
        self.gem5_output_path = gem5_output_path
        self.hardware_script_run_command = hardware_script_run_command

        self.number_of_binaries_run = 0

        self.binary_execution_stats_path = None
    
    def run_binary(self, binary_path, binary_arguments):
        current_output_dir = self.gem5_output_path + '/' + str(self.number_of_binaries_run)

        args = ''

        if binary_arguments is not None:
            for arg in binary_arguments:
                args += '--binary_arg=' +'\"'+ arg + '\"' + ' '

        # Run the given hardware script from the command line with the given command,
        # binary path and binary arguments
        os.system(self.gem5_run_command + ' '
        + '--outdir=' + current_output_dir + ' '
        + self.hardware_script_run_command + ' '
        + '--binary_path=' + binary_path + ' '
        + args)

        # Wait for the execution to finish by checking whether the configuration
        # file created at the end is there
        output_config_path = current_output_dir + '/config.json'
        while not os.path.isfile(output_config_path):
            if (os.path.isfile(output_config_path)):
                break

        # After the wait above, the statistics should have been finalised
        # so change the binary_execution_stats_path variable to its path
        self.binary_execution_stats_path = current_output_dir + '/stats.txt'

        # Increment the number of binaries run
        self.number_of_binaries_run += 1
    
    def get_execution_stats(self):
        result = self.binary_execution_stats_path
        self.binary_execution_stats_path = None
        return result

    def has_output(self):
        return self.binary_execution_stats_path is not None