import hardware_model
import evaluation_object
import evaluation_binary_runner

class EvaluationHardwareModel(hardware_model.HardwareModel, evaluation_object.EvaluationObject):
    def __init__(self, hardware_simulator_run_command, output_path, hardware_script_run_command, binary_run_settings):
        hardware_model.HardwareModel.__init__(self, hardware_simulator_run_command, output_path,
            hardware_script_run_command, binary_run_settings)
        
    def init_binary_runner(self, hardware_simulator_run_command, hardware_script_run_command):
        return evaluation_binary_runner.EvaluationBinaryRunner(hardware_simulator_run_command, hardware_script_run_command,
                                                               self.binary_run_settings)

    def get_full_method_name(self, method_name):
        return 'EvaluationHardwareModel.'+method_name
    
    def run_binary(self, binary_path, binary_arguments, **relevant_attributes):
        self.add_time_measurement(self, 'run_binary', hardware_model.HardwareModel.run_binary,
            binary_path=binary_path, binary_arguments=binary_arguments, **relevant_attributes)