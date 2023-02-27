import subprocess

class BinaryRunner():
    def __init__(self, hardware_simulator_run_command, hardware_script_run_command,
                 binary_run_settings):
        self.hardware_simulator_run_command = hardware_simulator_run_command
        self.hardware_script_run_command = hardware_script_run_command
        self.binary_run_settings = binary_run_settings

    def run_binary(self, binary_path, binary_arguments, current_output_dir):
        args = self.binary_run_settings.convert_binary_arguments_to_options(binary_arguments)

        self.execute_hardware_script(current_output_dir, binary_path, args)

    # Run the given hardware script from the command line with the given command,
    # binary path and binary arguments
    def execute_hardware_script(self, current_output_dir, binary_path, args):
        subprocess.run([
            self.hardware_simulator_run_command,
            self.binary_run_settings.get_output_dir_option(current_output_dir),
            self.hardware_script_run_command,
            self.binary_run_settings.get_binary_path_option(binary_path)
        ]+args)