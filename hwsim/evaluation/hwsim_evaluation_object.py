import sys
sys.path.append('./evaluation_python')
sys.path.append('./hwsim')
import evaluation_object

class HWSIMEvaluationObject(evaluation_object.EvaluationObject):
    def __init__(self) -> None:
        evaluation_object.EvaluationObject.__init__(self)
        
    def get_collector(self):
        import hwsim_evaluation_measurement_collector
        return hwsim_evaluation_measurement_collector.collector