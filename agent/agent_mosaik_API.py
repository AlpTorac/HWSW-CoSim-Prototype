import mosaik_api
import agent

modelName = 'Agent'

binary_path_input_field = 'binary_file_path_in'
binary_path_output_field = 'binary_file_path_out'

binary_execution_stats_output_field = 'binary_execution_stats_out'
binary_execution_stats_input_field = 'binary_execution_stats_in'

binary_arguments_input_field = "binary_file_arguments_in"
binary_arguments_output_field = "binary_file_arguments_out"

variable_info_field = "variable_info"

binary_path_field = 'binary_path'
binary_args_field = 'binary_args'
binary_stats_field = 'binary_stats'

class AgentMosaikAPI(mosaik_api.Simulator):
    """_summary_
    This is an agent that implements mosaik_api.Simulator and manipulates the
    data flow between the software simulation and the hardware simulation.
    """

    meta = {
        'api_version': mosaik_api.__api_version__,
        'type': 'event-based',
        'models': {
            modelName: {
                'public': True,
                'params': [variable_info_field],
                'attrs': [binary_path_input_field, binary_path_output_field,
                binary_execution_stats_output_field, binary_execution_stats_input_field,
                binary_arguments_input_field, binary_arguments_output_field]
            },
        },
    }
    """_summary_
    See :meth:`mosaik_api.Simulator.meta` for more information and the used
    format.
    """
    
    def __init__(self):
        __doc__ = mosaik_api.Simulator.__doc__
        super().__init__(AgentMosaikAPI.meta)
        self.eid_prefix = ''
        self.agent = None
        
        self.binary_path = None
        self.binary_arguments = None
        self.binary_stats = None
        self.binary_run_count = 0
        
        self.binary_execution_stats = []

    def init(self, sid, time_resolution, **sim_params):
        if 'eid_prefix' in sim_params:
            self.eid_prefix = sim_params['eid_prefix']
        return self.meta

    def init_agent(self, params):
        """_summary_

        Returns:
            _type_: The created agent
        """
        #print('Agent params: ')
        #for param in params:
            #print(param)
        
        return agent.Agent(params)

    def create(self, num, model, **model_params):
        entities = []
        
        if variable_info_field in model_params:
            self.agent = self.init_agent(model_params[variable_info_field])
            
        eid = '%s%d' % (self.eid_prefix, 0)
        entities.append({'eid': eid, 'type': model})

        return entities


    def get_data(self, outputs):
        data = {}
        for eid, attrs in outputs.items():
            data[eid] = {}
            if self.binary_arguments is not None \
                and self.binary_path is not None \
                and not self.binary_run_count_reached():
                
                #print('binary arguments = ' + ' '.join([str(arg) for arg in self.binary_arguments]))
                #print('run count = %d, count reached = %s' % (self.binary_run_count, self.binary_run_count_reached()))
                
                for attr in attrs:
                    if attr == binary_path_output_field:
                        data[eid][attr] = self.binary_path
                    if attr == binary_arguments_output_field:
                        data[eid][attr] = self.binary_arguments
                
                self.binary_run_count += 1
            else:
                for attr in attrs:
                    if attr == binary_execution_stats_output_field:
                        data[eid][attr] = self.binary_execution_stats
                        self.binary_execution_stats = []
                        self.binary_path = None
                        self.binary_arguments = None
                        self.binary_stats = None
                        self.binary_run_count = 0

        return data

    def step(self, time, inputs, max_advance):
        #print('Agent stepping at time: ' + str(time))
        for eid, attrs in inputs.items():
            if self.binary_path is None or self.binary_arguments is None:
                for attr, values in attrs.items():
                    if attr == binary_path_input_field:
                        self.binary_path = list(values.values())[0]
                    if attr == binary_arguments_input_field:
                        self.binary_arguments = list(values.values())[0]
            else:
                for attr, values in attrs.items():
                    if attr == binary_execution_stats_input_field:
                        self.binary_stats = list(values.values())[0]
                        self.binary_execution_stats.append(self.binary_stats)
            
            #print('path = %s' % self.binary_path)
            #print('arguments = %s' % self.binary_arguments)
            #print('stats = %s' % self.binary_stats)
            
            if self.binary_path is not None \
                and self.binary_arguments is not None \
                and self.binary_stats is not None:
                    
                self.binary_arguments = self.agent.process_stats(self.binary_path, self.binary_arguments, self.binary_stats)
        
        return None

    def binary_run_count_reached(self):
        return self.binary_run_count >= self.agent.get_max_repeat_count(self.binary_path)
    
if __name__ == '__main__':
    mosaik_api.start_simulation(AgentMosaikAPI())