import hardware_model

class HardwareSimulator():
    def __init__(self):
        self.model = None

    def init_hardware_model(self, gem5_build_path, gem5_output_dir,
    gem5_options, hardware_script_path, hardware_script_options):
        if self.model == None:
            self.model = hardware_model.HardwareModel(gem5_build_path, gem5_output_dir,
            gem5_options, hardware_script_path, hardware_script_options)

    def run_binary(self, binary_path, binary_arguments=''):
        self.model.run_binary(binary_path, binary_arguments)
    
    def get_execution_stats(self):
        return self.model.get_execution_stats()

    def has_output(self):
        return self.model.has_output()