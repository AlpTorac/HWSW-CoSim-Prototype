import hwsim_evaluation_object

import evaluation_hardware_model

import hardware_simulator

class EvaluationHardwareSimulator(hardware_simulator.HardwareSimulator, hwsim_evaluation_object.HWSIMEvaluationObject):
    """_summary_
    This class is to be used to measure run times of the methods of
    hardware_simulator.HardwareSimulator.
    """
    
    def __init__(self):
        hardware_simulator.HardwareSimulator.__init__(self)

    def create_hardware_model(self, **model_params) -> evaluation_hardware_model.EvaluationHardwareModel:
        return evaluation_hardware_model.EvaluationHardwareModel(self.init_binary_run_settings(), **model_params)

    def get_full_method_name(self, method_name):
        return 'EvaluationHardwareSimulator.'+method_name

    def run_binary(self, binary_path, binary_arguments, **relevant_attributes):
        self.add_time_measurement(self, 'run_binary', hardware_simulator.HardwareSimulator.run_binary,
            binary_path=binary_path, binary_arguments=binary_arguments, **relevant_attributes)