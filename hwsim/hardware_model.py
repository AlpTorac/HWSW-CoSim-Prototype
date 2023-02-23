import binary_runner
import binary_execution_stat_parser
import hardware_simulator_output_formatter

class HardwareModel():
    def __init__(self, hardware_simulator_run_command, output_path,
    hardware_script_run_command):
        self.output_path = output_path

        self.binary_runner = binary_runner.BinaryRunner(hardware_simulator_run_command, hardware_script_run_command)
        self.binary_execution_stat_parser = binary_execution_stat_parser.BinaryExecutionStatParser()
        self.output_formatter = hardware_simulator_output_formatter.HardwareSimulatorOutputFormatter()

        self.binary_execution_stats = None
    
    def run_binary(self, binary_path, binary_arguments, **relevant_attributes):
        current_output_dir = self.output_formatter.get_output_dir_name(self.output_path, binary_path, **relevant_attributes)

        self.binary_runner.run_binary(binary_path, binary_arguments, current_output_dir)

        self.binary_execution_stats = self.binary_execution_stat_parser.parse_execution_stats_from_file(current_output_dir)

    def get_execution_stats(self):
        result = self.binary_execution_stats
        self.binary_execution_stats = None
        return result

    def has_output(self):
        return self.binary_execution_stats is not None