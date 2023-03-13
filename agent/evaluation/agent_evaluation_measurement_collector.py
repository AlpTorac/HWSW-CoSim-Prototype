import sys
sys.path.append('./evaluation_python')
sys.path.append('./agent')
import evaluation_measurement_collector
import agent_evaluation_object

class AgentEvaluationMeasurementCollector(evaluation_measurement_collector.EvaluationMeasurementCollector, agent_evaluation_object.AgentEvaluationObject):
    def __init__(self):
        evaluation_measurement_collector.EvaluationMeasurementCollector.__init__(self)
        
collector = AgentEvaluationMeasurementCollector()