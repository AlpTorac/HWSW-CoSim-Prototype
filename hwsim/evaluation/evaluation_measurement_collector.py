import evaluation_object

class EvaluationMeasurementCollector(evaluation_object.EvaluationObject):
    """
    This class is used to collect measurement data from child classes of
    EvaluationObject.

    It should be treated as a singleton, whose only instance is
    evaluation_measurement_collector.collector (see the end of the file)
    """
    
    def __init__(self):
        self.time_measurement_collector = {}

    def add_time_measurement_entry(self, method_name, time):
        """_summary_
        Adds a time measurement to self.time_measurement_collector
        with key = method_name, value = time.
        
        Args:
            method_name (_type_): The uniquely identifiable name
            from the method
            time (_type_): The taken time measurement
        """
        
        if method_name in self.time_measurement_collector:
            self.time_measurement_collector[method_name].append(time)
        else:
            self.time_measurement_collector[method_name] = [time]

    def add_time_measurement(self, method_caller, method_name, method, *args, **kwargs):
        """_summary_
        Measures and adds the run time of method(self=method_caller, *args, **kwargs)
        to the only instance of evaluation_measurement_collector.collector

        Args:
            method_caller (_type_): The object, which will call the method
            method_name (_type_): The uniquely identifiable name of the method
            being called
            method (_type_): The method itself

        Returns:
            _type_: The output of method(self=method_caller, *args, **kwargs)
        """
        
        start = self.get_current_system_time()
        result = method(self=method_caller, *args, **kwargs)
        end = self.get_current_system_time()

        self.add_time_measurement_entry(method_name, end - start)
        return result

    def reduce_time_measurements(self):
        """_summary_
        Reduces the values that belong to a specific key by
        summing all of them up. Does not change self.time_measurement_collector.
        
        Returns:
            _type_: The described version of self.time_measurement_collector
        """
        
        reduced_measurements = {}

        for method_name, time_list in self.time_measurement_collector.items():
            sum = 0

            for time in time_list:
                sum += time
            
            reduced_measurements[method_name] = '%.0f' % sum
        
        return reduced_measurements
    
collector = EvaluationMeasurementCollector()