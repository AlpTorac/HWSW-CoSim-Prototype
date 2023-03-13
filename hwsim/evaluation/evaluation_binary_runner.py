import hwsim_evaluation_object
import binary_runner

class EvaluationBinaryRunner(binary_runner.BinaryRunner, hwsim_evaluation_object.HWSIMEvaluationObject):
    """_summary_
    This class is to be used to measure run times of the methods of
    binary_runner.BinaryRunner.
    """
    def __init__(self, hardware_simulator_run_command, hardware_script_run_command, binary_run_settings):
        binary_runner.BinaryRunner.__init__(self, hardware_simulator_run_command, hardware_script_run_command,
                                                   binary_run_settings)

    def get_full_method_name(self, method_name):
        return 'EvaluationBinaryRunner.'+method_name
    
    def run_binary(self, binary_path, binary_arguments, current_output_dir):
        self.add_time_measurement(self, 'run_binary', binary_runner.BinaryRunner.run_binary,
            binary_path=binary_path, binary_arguments=binary_arguments, current_output_dir=current_output_dir)