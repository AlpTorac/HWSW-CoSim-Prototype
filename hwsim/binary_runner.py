import os

class BinaryRunner():
    def __init__(self, hardware_simulator_run_command, hardware_script_run_command,
                 binary_run_settings):
        self.hardware_simulator_run_command = hardware_simulator_run_command
        self.hardware_script_run_command = hardware_script_run_command
        self.binary_run_settings = binary_run_settings

    def run_binary(self, binary_path, binary_arguments, current_output_dir):
        args = self.binary_run_settings.convert_binary_arguments_to_options(binary_arguments)

        self.execute_hardware_script(current_output_dir, binary_path, args)
        self.wait_for_hardware_simulation(current_output_dir)

    # Run the given hardware script from the command line with the given command,
    # binary path and binary arguments
    def execute_hardware_script(self, current_output_dir, binary_path, args):
        os.system(self.hardware_simulator_run_command + ' '
        + self.binary_run_settings.get_output_dir_option(current_output_dir) + ' '
        + self.hardware_script_run_command + ' '
        + self.binary_run_settings.get_binary_path_option(binary_path) + ' '
        + args)

    # Wait for the execution to finish by checking whether the configuration
    # file created at the end is there
    def wait_for_hardware_simulation(self, current_output_dir):
        output_config_path = current_output_dir + '/config.json'
        while not os.path.isfile(output_config_path):
            if (os.path.isfile(output_config_path)):
                break