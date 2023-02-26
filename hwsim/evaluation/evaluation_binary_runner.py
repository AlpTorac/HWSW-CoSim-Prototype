import evaluation_object

import binary_runner

class EvaluationBinaryRunner(binary_runner.BinaryRunner, evaluation_object.EvaluationObject):
    def __init__(self, hardware_simulator_run_command, hardware_script_run_command, binary_run_settings):
        binary_runner.BinaryRunner.__init__(self, hardware_simulator_run_command, hardware_script_run_command,
                                                   binary_run_settings)

    def get_full_method_name(self, method_name):
        return 'EvaluationBinaryRunner.'+method_name
    
    def run_binary(self, binary_path, binary_arguments, current_output_dir):
        self.add_time_measurement(self, 'run_binary', binary_runner.BinaryRunner.run_binary,
            binary_path=binary_path, binary_arguments=binary_arguments, current_output_dir=current_output_dir)

    # Run the given hardware script from the command line with the given command,
    # binary path and binary arguments
    def execute_hardware_script(self, current_output_dir, binary_path, args):
        self.add_time_measurement(self, 'execute_hardware_script', binary_runner.BinaryRunner.execute_hardware_script,
            current_output_dir=current_output_dir, binary_path=binary_path,
            args=args)