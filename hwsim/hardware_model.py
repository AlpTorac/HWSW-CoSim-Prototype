class HardwareModel():
    def __init__(self):
        self.current_binary_path = ''
    
    def set_current_binary_path(self, binary_path):
        self.current_binary_path = binary_path
        return None
    
    def get_execution_stats(self):
        result = self.current_binary_path+'_stats'
        self.current_binary_path = ''
        return result

    def has_output(self):
        return self.current_binary_path != ''