import subprocess
import binary_run_settings

class BinaryRunner():
    """_summary_
    This class contains the means to run a binary on hardware that is
    to be built using hardware_script_run_command from terminal.
    """
    
    def __init__(self, hardware_simulator_run_command, hardware_script_run_command,
                 binary_run_settings: binary_run_settings.BinaryRunSettings):
        self.hardware_simulator_run_command = hardware_simulator_run_command
        self.hardware_script_run_command = hardware_script_run_command
        self.binary_run_settings = binary_run_settings

    def run_binary(self, binary_path, binary_arguments, current_output_dir):
        """_summary_

        Runs the given hardware script from the command line with the given command,
        the path and arguments from the binary to run on the hardware built with
        the hardware script.

        Args:
            current_output_dir (_type_): The path, at which the output from the
            hardware simulation will be generated
            binary_path (_type_): The path to the binary to run
            binary_args (_type_): The arguments, with which the binary will be run
        """

        subprocess.run([
            self.hardware_simulator_run_command,
            self.binary_run_settings.convert_to_output_dir_option(current_output_dir),
            self.hardware_script_run_command,
            self.binary_run_settings.convert_to_binary_path_option(binary_path)
        ]+self.binary_run_settings.convert_to_binary_arg_option(binary_arguments))