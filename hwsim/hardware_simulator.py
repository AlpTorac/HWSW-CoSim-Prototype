import hardware_model

class HardwareSimulator():
    def __init__(self):
        self.model = None

    def add_hardware_model(self):
        self.model = hardware_model.HardwareModel()
        return None