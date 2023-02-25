import time

"""
This class is used to collect measurement data from child classes of
EvaluationObject.

It should be treated as a singleton, whose only instance is
evaluation_measurement_collector.collector (see the end of the file)
"""
class EvaluationMeasurementCollector():
    def __init__(self):
        self.time_measurement_collector = {}

    def add_time_measurement_entry(self, method_name, time):
        if method_name in self.time_measurement_collector:
            self.time_measurement_collector[method_name].append(time)
        else:
            self.time_measurement_collector[method_name] = [time]

    def add_time_measurement(self, method_caller, method_name, method, *args, **kwargs):
        start = time.time_ns()
        result = method(self=method_caller, *args, **kwargs)
        end = time.time_ns()

        self.add_time_measurement_entry(method_name, end - start)
        return result

    def reduce_time_measurements(self):
        reduced_measurements = {}

        for method_name, time_list in self.time_measurement_collector.items():
            sum = 0

            for time in time_list:
                sum += time
            
            reduced_measurements[method_name] = '%.0f' % sum
        
        return reduced_measurements
    
collector = EvaluationMeasurementCollector()