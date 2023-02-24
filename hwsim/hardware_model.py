import binary_runner
import binary_execution_stat_parser
import hardware_simulator_output_formatter

class HardwareModel():
    def __init__(self, hardware_simulator_run_command, output_path,
    hardware_script_run_command, binary_run_settings):
        self.output_path = output_path

        self.binary_run_settings = binary_run_settings

        self.binary_runner = self.init_binary_runner(hardware_simulator_run_command, hardware_script_run_command)
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
        current_output_dir = self.output_formatter.get_output_dir_name(self.output_path, binary_path, **relevant_attributes)

        self.binary_runner.run_binary(binary_path, binary_arguments, current_output_dir)

        self.binary_execution_stats = self.binary_execution_stat_parser.parse_execution_stats_from_file(current_output_dir)

    def get_execution_stats(self):
        result = self.binary_execution_stats
        self.binary_execution_stats = None
        return result

    def has_output(self):
        return self.binary_execution_stats is not None