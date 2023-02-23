import re

class HardwareSimulatorOutputFormatter:
    def __init__(self):
        # A pattern that matches path names and captures the name of the binary
        self.binary_name_pattern = re.compile('(?:/.*/)*(.*)')
    
    def get_output_dir_name(self, output_path, binary_path, **relevant_attributes):
        result = output_path + '/' + self.get_binary_name(binary_path)

        if relevant_attributes is not None:
            for key, val in relevant_attributes.items():
                result += '-' + str(key) + '=' + str(val)

        return result

    # Get the name of the binary
    def get_binary_name(self, binary_path):
        return self.binary_name_pattern.match(binary_path).group(1)