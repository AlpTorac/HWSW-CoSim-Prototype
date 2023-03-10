import hardware_model
import binary_run_settings

class HardwareSimulator():
    """_summary_
    This class houses the hardware model and grants access to
    it from outside.
    """
    
    def __init__(self):
        self.model = None

    def create_hardware_model(self, **model_params):
        """_summary_

        Returns:
            _type_: The created hardware model instance
        """
        return hardware_model.HardwareModel(self.init_binary_run_settings(), **model_params)

    def init_hardware_model(self, **model_params):
        """_summary_
        Creates a hardware model and sets self.model to it. Does nothing
        if self.model is not None.
        """
        if self.model is None:
            self.model = self.create_hardware_model(**model_params)

    def init_binary_run_settings(self):
        return binary_run_settings.BinaryRunSettings()

    def run_binary(self, binary_path, binary_arguments, **relevant_attributes):
        """_summary_

        Passes the given arguments to self.model and makes it run the given
        binary.

        Args:
            binary_path (_type_): The path to the binary to be executed
            binary_arguments (_type_): An iterable collection of binary arguments
        """
        self.model.run_binary(binary_path, binary_arguments, **relevant_attributes)
    
    def get_execution_stats(self):
        """_summary_

        Gets the execution stats from the binary that has been run last
        and erases them after returning them.

        Returns:
            _type_: The said execution stats
        """
        return self.model.get_execution_stats()

    def has_output(self):
        """_summary_

        Returns:
            _type_: True if there is data to be output from self.model.
        """
        return self.model.has_output()