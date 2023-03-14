import mosaik_api
import evaluation_agent
import agent_mosaik_API

import sys
sys.path.append('./scenario_python')
from scenario_fields import *

import agent_evaluation_object

class EvaluationAgentMosaikAPI(agent_mosaik_API.AgentMosaikAPI, agent_evaluation_object.AgentEvaluationObject):
    """_summary_
    This is an agent that implements mosaik_api.Simulator and manipulates a
    given binary argument from a binary_path, binary_arguments pair with the
    goal of finding the best value for the said argument, using the parameters
    given with agent_parameters_field.
    """
    
    def __init__(self):
        agent_mosaik_API.AgentMosaikAPI.__init__(self)
        self.start_time = None
        self.end_time = None
        
        self.agent_eval_output_file_path = None

    def get_full_method_name(self, method_name):
        return 'EvaluationAgentMosaikAPI.%s' % method_name

    def init(self, sid, time_resolution, **sim_params):
        self.start_time = self.get_current_system_time()
        
        if agent_eval_output_file_field in sim_params:
            self.agent_eval_output_file_path = sim_params[agent_eval_output_file_field]
            print(agent_eval_output_file_field + ' found: ' + self.agent_eval_output_file_path)
        
        return self.add_time_measurement(self, 'init',
            agent_mosaik_API.AgentMosaikAPI.init,
            sid=sid, time_resolution=time_resolution, **sim_params)

    def init_agent(self, params):
        """_summary_

        Returns:
            _type_: The created agent
        """
        return evaluation_agent.EvaluationAgent(params)

    def create(self, num, model, **model_params):
        return self.add_time_measurement(self, 'create',
            agent_mosaik_API.AgentMosaikAPI.create,
            num=num, model=model, **model_params)


    def reset_binary_attributes(self):
        self.add_time_measurement(self, 'reset_binary_attributes',
            agent_mosaik_API.AgentMosaikAPI.reset_binary_attributes)

    def get_data(self, outputs):
        return self.add_time_measurement(self, 'get_data',
            agent_mosaik_API.AgentMosaikAPI.get_data,
            outputs=outputs)

    def step(self, time, inputs, max_advance):
        return self.add_time_measurement(self, 'step',
            agent_mosaik_API.AgentMosaikAPI.step,
            time=time, inputs=inputs, max_advance=max_advance)

    def binary_run_count_reached(self):
        return self.add_time_measurement(self, 'binary_run_count_reached',
            agent_mosaik_API.AgentMosaikAPI.binary_run_count_reached)
    
    def finalize(self):
        agent_mosaik_API.AgentMosaikAPI.finalize(self)
        self.end_time = self.get_current_system_time()
        self.write_output(self.agent_eval_output_file_path,
                    'agent time measurements:\n',
                    '\nagent ran for: %.0f\n' % (self.end_time - self.start_time))
    
if __name__ == '__main__':
    mosaik_api.start_simulation(EvaluationAgentMosaikAPI())