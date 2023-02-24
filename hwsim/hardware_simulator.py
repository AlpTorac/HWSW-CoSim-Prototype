import hardware_model
import binary_run_settings

class HardwareSimulator():
    def __init__(self):
        self.model = None

    def create_hardware_model(self, hardware_simulator_run_command, output_path, hardware_script_run_command) -> hardware_model.HardwareModel:
        return hardware_model.HardwareModel(hardware_simulator_run_command, output_path,
    hardware_script_run_command, self.init_binary_run_settings())

    def init_hardware_model(self, hardware_simulator_run_command, output_path,
    hardware_script_run_command):
        if self.model is None:
            self.model = self.create_hardware_model(hardware_simulator_run_command,
                output_path, hardware_script_run_command)

    def init_binary_run_settings(self):
        return binary_run_settings.BinaryRunSettings()

    def run_binary(self, binary_path, binary_arguments, **relevant_attributes):
        self.model.run_binary(binary_path, binary_arguments, **relevant_attributes)
    
    def get_execution_stats(self):
        return self.model.get_execution_stats()

    def has_output(self):
        return self.model.has_output()