import sys
sys.path.append('./evaluation_python')
sys.path.append('./hwsim')
import evaluation_measurement_collector
import hwsim_evaluation_object

class HWSIMEvaluationMeasurementCollector(evaluation_measurement_collector.EvaluationMeasurementCollector, hwsim_evaluation_object.HWSIMEvaluationObject):
    def __init__(self):
        evaluation_measurement_collector.EvaluationMeasurementCollector.__init__(self)
        
collector = HWSIMEvaluationMeasurementCollector()