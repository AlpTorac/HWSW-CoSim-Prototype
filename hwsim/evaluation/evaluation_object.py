import time

import sys
sys.path.append('./hwsim')

def get_current_system_time():
    return time.time_ns()

def get_collector():
    import evaluation_measurement_collector
    return evaluation_measurement_collector.collector

class EvaluationObject():
    def __init__(self):
        pass

    def get_full_method_name(self, method_name):
        pass

    def get_current_system_time(self):
        return get_current_system_time()

    def get_collector(self):
        return get_collector()

    def add_time_measurement(self, method_caller, method_name, method, *args, **kwargs):
        return self.get_collector().add_time_measurement(method_caller, self.get_full_method_name(method_name), method, *args, **kwargs)