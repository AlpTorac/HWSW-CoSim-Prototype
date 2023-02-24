import evaluation_measurement_collector

class EvaluationObject():
    def __init__(self):
        pass

    def get_full_method_name(self, method_name):
        pass

    def get_collector(self) -> evaluation_measurement_collector.EvaluationMeasurementCollector:
        return evaluation_measurement_collector.collector

    def add_time_measurement(self, method_caller, method_name, method, *args, **kwargs):
        return self.get_collector().add_time_measurement(method_caller, self.get_full_method_name(method_name), method, *args, **kwargs)