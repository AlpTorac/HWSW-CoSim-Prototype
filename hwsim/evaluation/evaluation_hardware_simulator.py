import evaluation_object
import evaluation_hardware_model

import hardware_simulator

class EvaluationHardwareSimulator(hardware_simulator.HardwareSimulator, evaluation_object.EvaluationObject):
    def __init__(self):
        hardware_simulator.HardwareSimulator.__init__(self)
        self.model = None

    def create_hardware_model(self, hardware_simulator_run_command, output_path, hardware_script_run_command) -> evaluation_hardware_model.EvaluationHardwareModel:
        return evaluation_hardware_model.EvaluationHardwareModel(hardware_simulator_run_command, output_path,
    hardware_script_run_command, self.init_binary_run_settings())

    def get_full_method_name(self, method_name):
        return 'EvaluationHardwareSimulator.'+method_name

    def run_binary(self, binary_path, binary_arguments, **relevant_attributes):
        self.add_time_measurement(self, 'run_binary', hardware_simulator.HardwareSimulator.run_binary,
            binary_path=binary_path, binary_arguments=binary_arguments, **relevant_attributes)