import agent_evaluation_object
import agent

class EvaluationAgent(agent.Agent, agent_evaluation_object.AgentEvaluationObject):
    def __init__(self, params):
        agent.Agent.__init__(self, params)
        
    def get_full_method_name(self, method_name):
        return 'EvaluationAgent.%s' % method_name
    
    def get_binary_name(self, binary_path):
        return self.add_time_measurement(self, 'get_binary_name',
            agent.Agent.get_binary_name,
            binary_path=binary_path)
    
    def process_stats(self, binary_path, binary_args, stats):
        return self.add_time_measurement(self, 'process_stats',
            agent.Agent.process_stats,
            binary_path=binary_path, binary_args=binary_args,
            stats=stats)
    
    def get_param_for(self, binary_path):
        return self.add_time_measurement(self, 'get_param_for',
            agent.Agent.get_param_for,
            binary_path=binary_path)
    
    def get_max_repeat_count(self, binary_path):
        return self.add_time_measurement(self, 'get_max_repeat_count',
            agent.Agent.get_max_repeat_count,
            binary_path=binary_path)