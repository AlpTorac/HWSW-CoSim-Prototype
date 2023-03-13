import time

def get_current_system_time():
    """_summary_
    Returns:
        _type_: The current system time in the
        specified way.
    """
    return time.time_ns()

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
            _type_: The EvaluationMeasurementCollector, which will receive the
            measurements from this instance.
        """
        pass
    
    def write_output(self, output_file_path, file_start_string, file_end_string):
        """_summary_

        Write the measurements collected by self.get_collector() to the file
        specified by the parameters.

        Args:
            output_file_path (_type_): The absolute path to the file as String
            file_start_string (_type_): The text to write at the start of the file as String
            file_end_string (_type_): The text to write at the end of the file as String
        """
        #print('Generating output at: ' + output_file_path)
        file = open(output_file_path, 'x')

        file.write(file_start_string)

        output_data = self.get_collector().reduce_time_measurements()
        for output_name, output_value in output_data.items():
            if output_name is not None and output_value is not None:
                file.write(output_name+': '+str(output_value)+'\n')

        file.write(file_end_string)

        file.close()

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