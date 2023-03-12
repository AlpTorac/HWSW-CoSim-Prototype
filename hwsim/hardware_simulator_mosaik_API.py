import mosaik_api
import hardware_simulator
import agent

modelName = 'HWModel'

binary_path_input_field = 'binary_file_path_in'
binary_path_output_field = 'binary_file_path_out'

binary_execution_stats_output_field = 'binary_execution_stats_out'
binary_execution_stats_input_field = 'binary_execution_stats_in'

binary_arguments_input_field = "binary_file_arguments_in"
binary_arguments_output_field = "binary_file_arguments_out"

hardware_simulator_run_command_field = 'hardware_simulator_run_command'
output_path_field = 'output_path'
hardware_script_run_command_field = 'hardware_script_run_command'

variable_info_field = "variable_info"

binary_path_field = 'binary_path'
binary_args_field = 'binary_args'
binary_stats_field = 'binary_stats'

class HardwareSimulatorMosaikAPI(mosaik_api.Simulator):
    """_summary_
    This is the concrete implementation of mosaik_api.Simulator for the
    hardware simulator.
    """

    meta = {
        'api_version': mosaik_api.__api_version__,
        'type': 'event-based',
        'models': {
            modelName: {
                'public': True,
                'params': [hardware_simulator_run_command_field, output_path_field, hardware_script_run_command_field,
                           variable_info_field],
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
        super().__init__(HardwareSimulatorMosaikAPI.meta)
        self.eid_prefix = ''
        self.agent = None
        self.simulator = None
        self.binary_execution_stats = []

    def init(self, sid, time_resolution, **sim_params):
        if 'eid_prefix' in sim_params:
            self.eid_prefix = sim_params['eid_prefix']
        if variable_info_field in sim_params:
            self.agent = self.init_agent(sim_params[variable_info_field])
        self.simulator = self.init_simulator()
        return self.meta

    def init_agent(self, params):
        """_summary_

        Returns:
            _type_: The created agent
        """
        print('Agent params: ')
        for param in params:
            print(param)
        
        return agent.Agent(params)

    def init_simulator(self):
        """_summary_

        Returns:
            _type_: The created hardware simulator
        """
        return hardware_simulator.HardwareSimulator()

    def create(self, num, model, **model_params):
        entities = []

        self.simulator.init_hardware_model(**model_params)
        eid = '%s%d' % (self.eid_prefix, 0)
        entities.append({'eid': eid, 'type': model})

        return entities

    def get_data(self, outputs):
        data = {}
        for eid, attrs in outputs.items():
            data[eid] = {}
            for attr in attrs:
                if attr == binary_execution_stats_output_field:
                    data[eid][attr] = self.binary_execution_stats

        self.binary_execution_stats = []

        return data

    def step(self, time, inputs, max_advance):
        print('hwsim stepping at time: ' + str(time))
        for eid, attrs in inputs.items():
            new_binary_path = None
            binary_arguments = None
            for attr, values in attrs.items():
                if attr == binary_path_input_field:
                    new_binary_path = list(values.values())[0]
                if attr == binary_arguments_input_field:
                    binary_arguments = list(values.values())[0]
            
            if new_binary_path is not None:
                self.run_binary(new_binary_path, binary_arguments, time=time)
    
    def run_binary(self, binary_path, initial_binary_arguments, **relevant_attributes):
        if self.agent is None:
            self.simulator.run_binary(binary_path, initial_binary_arguments, **relevant_attributes, step=0)
            
            while not self.simulator.has_output():
                continue
            
            self.binary_execution_stats.append(self.simulator.get_execution_stats())
        else:
            step_limit = self.agent.get_step_count(binary_path)
            binary_arguments = initial_binary_arguments
            
            for i in range(0, step_limit):
                self.simulator.run_binary(binary_path, binary_arguments, **relevant_attributes, step=i)
                
                while not self.simulator.has_output():
                    continue
                
                stats = self.simulator.get_execution_stats()
                binary_arguments = self.agent.process_stats(binary_path, binary_arguments, stats)
                self.binary_execution_stats.append(stats)
                
                if binary_arguments is None:
                    return None
                
if __name__ == '__main__':
    mosaik_api.start_simulation(HardwareSimulatorMosaikAPI())