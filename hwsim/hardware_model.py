import os
import os.path
import shutil
import re
class HardwareModel():
    def __init__(self, gem5_run_command, gem5_output_path,
    hardware_script_run_command):
        self.gem5_run_command = gem5_run_command
        self.gem5_output_path = gem5_output_path
        self.hardware_script_run_command = hardware_script_run_command

        # A pattern that matches path names and captures the name of the binary
        self.binary_name_pattern = re.compile('(?:/.*/)*(.*)')

        # A pattern that matches the lines in the output, which are not related to
        # the system object (first few lines that do not start with "system.")
        #
        # Captures the name and the numerical value of the statistics
        #
        # Excludes commentary
        self.binary_execution_stats_pattern = re.compile('^(\w+)\s+((?:\d|\.)+)')

        self.binary_execution_stats = None
    
    def run_binary(self, binary_path, binary_arguments, time):
        binary_name = self.get_binary_name(binary_path)
        current_output_dir = self.get_output_dir_name(binary_name, time=time)

        args = self.convert_binary_arguments_to_options(binary_arguments)

        self.execute_hardware_script(current_output_dir, binary_path, args)
        self.wait_for_hardware_simulation(current_output_dir)
        self.binary_execution_stats = self.parse_execution_stats(current_output_dir)

    def get_output_dir_name(self, binary_name, **relevant_attributes):
        result = self.gem5_output_path + '/' + binary_name

        print(relevant_attributes)

        if relevant_attributes is not None:
            for key, val in relevant_attributes.items():
                result += '-' + str(val)

        return result

    # Run the given hardware script from the command line with the given command,
    # binary path and binary arguments
    def execute_hardware_script(self, current_output_dir, binary_path, args):
        os.system(self.gem5_run_command + ' '
        + '--outdir=' + current_output_dir + ' '
        + self.hardware_script_run_command + ' '
        + '--binary_path=' + binary_path + ' '
        + args)

    def convert_binary_arguments_to_options(self, binary_arguments):
        args = ''

        if binary_arguments is not None:
            for arg in binary_arguments:
                args += '--binary_arg=' +'\"'+ arg + '\"' + ' '
        
        return args

    # Get the name of the binary
    def get_binary_name(self, binary_path):
        return self.binary_name_pattern.match(binary_path).group(1)

    # Wait for the execution to finish by checking whether the configuration
    # file created at the end is there
    def wait_for_hardware_simulation(self, current_output_dir):
        output_config_path = current_output_dir + '/config.json'
        while not os.path.isfile(output_config_path):
            if (os.path.isfile(output_config_path)):
                break
    
    def parse_execution_stats(self, current_output_dir):
        stats = {}
        stats_file = open(current_output_dir + '/stats.txt', 'r')

        lines = stats_file.readlines()

        for line in lines:
            stat = self.binary_execution_stats_pattern.match(line)

            if stat is not None:
                stat_name = stat.group(1)
                stat_value = stat.group(2)

                stats[stat_name] = stat_value

        return stats

    def get_execution_stats(self):
        result = self.binary_execution_stats
        self.binary_execution_stats = None
        return result

    def has_output(self):
        return self.binary_execution_stats is not None