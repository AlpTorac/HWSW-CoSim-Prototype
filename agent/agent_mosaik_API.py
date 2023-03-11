import mosaik_api
import agent_manager

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
        self.agent_manager = None

    def init(self, sid, time_resolution, **sim_params):
        if 'eid_prefix' in sim_params:
            self.eid_prefix = sim_params['eid_prefix']
        self.agent_manager = self.init_agent_manager()
        return self.meta

    def init_agent_manager(self):
        """_summary_

        Returns:
            _type_: The created agent
        """
        return agent_manager.AgentManager()

    def create(self, num, model, **model_params):
        entities = []

        self.agent_manager.add_agent(**model_params)
        eid = '%s%d' % (self.eid_prefix, 0)
        entities.append({'eid': eid, 'type': model})

        return entities



    def get_data(self, outputs):
        data = {}
        for eid, attrs in outputs.items():
            data[eid] = {}
            processed_inputs = self.agent_manager.get_processed_input()
            print(processed_inputs)
            
            def set_data(field, processed_inputs_key):
                if attr == field and processed_inputs_key in processed_inputs:
                    data[eid][attr] = processed_inputs[processed_inputs_key]
            
            for attr in attrs:
                if processed_inputs is not None:
                    set_data(binary_path_output_field, binary_path_field)
                    set_data(binary_arguments_output_field, binary_args_field)
                    set_data(binary_execution_stats_output_field, binary_stats_field)

        return data

    def step(self, time, inputs, max_advance):
        print('Agent stepping at time: ' + str(time))
        for eid, attrs in inputs.items():
            inputs_to_process = {}
            new_binary_path = None
            binary_arguments = None
            for attr, values in attrs.items():
                if attr == binary_path_input_field:
                    new_binary_path = list(values.values())[0]
                    inputs_to_process[binary_path_field] = new_binary_path
                if attr == binary_arguments_input_field:
                    binary_arguments = list(values.values())[0]
                    inputs_to_process[binary_args_field] = binary_arguments
                if attr == binary_execution_stats_input_field:
                    binary_execution_stats = list(values.values())[0]
                    inputs_to_process[binary_stats_field] = binary_execution_stats
            
            if inputs_to_process is not None:
                self.agent_manager.process_input(**inputs_to_process)

if __name__ == '__main__':
    mosaik_api.start_simulation(AgentMosaikAPI())