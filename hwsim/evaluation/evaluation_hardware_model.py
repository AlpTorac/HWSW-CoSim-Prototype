import evaluation_object
import evaluation_binary_runner

import hardware_model

class EvaluationHardwareModel(hardware_model.HardwareModel, evaluation_object.EvaluationObject):
    """_summary_
    This class is to be used to measure run times of the methods of
    hardware_model.HardwareModel.
    """
    
    def __init__(self, binary_run_settings, **model_params):
        hardware_model.HardwareModel.__init__(self, binary_run_settings, **model_params)
        
    def init_binary_runner(self, hardware_simulator_run_command, hardware_script_run_command):
        return evaluation_binary_runner.EvaluationBinaryRunner(hardware_simulator_run_command, hardware_script_run_command,
                                                               self.binary_run_settings)

    def get_full_method_name(self, method_name):
        return 'EvaluationHardwareModel.'+method_name
    
    def run_binary(self, binary_path, binary_arguments, **relevant_attributes):
        self.add_time_measurement(self, 'run_binary', hardware_model.HardwareModel.run_binary,
            binary_path=binary_path, binary_arguments=binary_arguments, **relevant_attributes)