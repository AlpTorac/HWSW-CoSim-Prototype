import binary_runner
import binary_execution_stat_parser
import hardware_simulator_output_formatter
import hardware_simulator_mosaik_API

class HardwareModel():
    """_summary_
    This class encapsulates a hardware model and grants access to
    its methods.
    """
    
    def __init__(self, binary_run_settings, **model_params):
        """_summary_

        Args:
            binary_run_settings (_type_): A BinaryRunSettings instance to be used
            
            model_params (_type_):
                hardware_simulator_run_command (_type_): The command to run the hardware simulation
                output_path (_type_): The path, at which the hardware simulator will generate its output
                hardware_script_run_command (_type_): The command to build the actual hardware
        """
        
        self.output_path = model_params[hardware_simulator_mosaik_API.output_path_field]

        self.binary_run_settings = binary_run_settings

        self.binary_runner = self.init_binary_runner(model_params[hardware_simulator_mosaik_API.hardware_simulator_run_command_field]
                                                     ,model_params[hardware_simulator_mosaik_API.hardware_script_run_command_field])
        self.binary_execution_stat_parser = self.init_binary_execution_stat_parser()
        self.output_formatter = self.init_hardware_simulator_output_formatter()

        self.binary_execution_stats = None
    
    def init_hardware_simulator_output_formatter(self):
        return hardware_simulator_output_formatter.HardwareSimulatorOutputFormatter()

    def init_binary_execution_stat_parser(self):
        return binary_execution_stat_parser.BinaryExecutionStatParser()

    def init_binary_runner(self, hardware_simulator_run_command, hardware_script_run_command):
        return binary_runner.BinaryRunner(hardware_simulator_run_command, hardware_script_run_command,
                                          self.binary_run_settings)

    def run_binary(self, binary_path, binary_arguments, **relevant_attributes):
        """_summary_

        Runs the given binary with the given arguments.

        Args:
            binary_path (_type_): The path to the binary to run
            binary_arguments (_type_): The arguments, with which the binary
            will be run
            relevant_attributes (dict[str, Any]): A dict of additional values,
            which are relevant for the execution of the binary
        """
        current_output_dir = self.output_formatter.get_output_dir_name(self.output_path, binary_path, **relevant_attributes)
        
        self.binary_runner.run_binary(binary_path, binary_arguments, current_output_dir)

        self.binary_execution_stats = self.binary_execution_stat_parser.parse_execution_stats_from_file(current_output_dir)

    def get_execution_stats(self):
        """_summary_

        Gets the execution stats from the binary that has been run last
        and erases them after returning them.

        Returns:
            _type_: The said execution stats
        """
        result = self.binary_execution_stats
        self.binary_execution_stats = None
        return result

    def has_output(self):
        """_summary_

        Returns:
            _type_: True if there are any execution stats to output.
        """
        return self.binary_execution_stats is not None