import sys
sys.path.append('./agent')
sys.path.append('./evaluation_python')
import evaluation_object

class AgentEvaluationObject(evaluation_object.EvaluationObject):
    def __init__(self) -> None:
        evaluation_object.EvaluationObject.__init__(self)
        
    def get_collector(self):
        import agent_evaluation_measurement_collector
        return agent_evaluation_measurement_collector.collector