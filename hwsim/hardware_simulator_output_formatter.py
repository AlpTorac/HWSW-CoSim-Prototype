import re

class HardwareSimulatorOutputFormatter:
    def __init__(self):
        self.binary_name_pattern = re.compile('(?:/.*/)*(.*)')
        """_summary_
        A pattern that matches path names and captures the name of the binary
        (in group(1))
        """
    
    def get_output_dir_name(self, output_path: str, binary_path: str, **relevant_attributes) -> str:
        """_summary_

        Args:
            output_path (str): The path, where the hardware simulation's output
            will be generated
            binary_path (str): The path to the binary to be executed
            relevant_attributes (dict[str, Any]): A dict of values, whose values and
            name will be appended to the output directory's name

        Returns:
            str: The name of the output directory
        """
        result = output_path + '/' + self.get_binary_name(binary_path)

        if relevant_attributes is not None:
            for key, val in relevant_attributes.items():
                result += '-' + str(key) + '=' + str(val)

        return result

    def get_binary_name(self, binary_path: str) -> str:
        """_summary_

        Args:
            binary_path (str): The path to the binary

        Returns:
            str: Name of the binary at binary_path
        """
        return self.binary_name_pattern.match(binary_path).group(1)