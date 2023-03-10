import time

import sys
sys.path.append('./hwsim')

def get_current_system_time():
    """_summary_
    Returns:
        _type_: The current system time in the
        specified way.
    """
    return time.time_ns()

def get_collector():
    """_summary_
    Returns:
        _type_: evaluation_measurement_collector.collector
    """
    import evaluation_measurement_collector
    return evaluation_measurement_collector.collector

class EvaluationObject():
    """_summary_
    An interface meant to be implemented by classes, which are to be
    used to take measurements from classes relevant to hardware simulation.
    """
    
    def __init__(self):
        pass

    def get_full_method_name(self, method_name):
        """_summary_

        Args:
            method_name (_type_): The name of the method, whose
            run time will be measured
        Returns:
            _type_: The name of the said method in a uniquely
            identifiable way
        """
        pass

    def get_current_system_time(self):
        """_summary_
        Returns:
            _type_: get_current_system_time()
        """
        return get_current_system_time()

    def get_collector(self):
        """_summary_
        Returns:
            _type_: evaluation_measurement_collector.collector
        """
        return get_collector()

    def add_time_measurement(self, method_caller, method_name, method, *args, **kwargs):
        """_summary_
        Measures and adds the run time of method(self=method_caller, *args, **kwargs)
        to the only instance of evaluation_measurement_collector.collector

        Args:
            method_caller (_type_): The object, which will call the method
            method_name (_type_): The name of the method being called
            method (_type_): The method itself

        Returns:
            _type_: The output of method(self=method_caller, *args, **kwargs)
        """
        return self.get_collector().add_time_measurement(method_caller, self.get_full_method_name(method_name), method, *args, **kwargs)