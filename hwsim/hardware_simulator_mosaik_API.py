import mosaik_api
import hardware_simulator

modelName = 'HWModel'

binary_path_field = 'binary_file_path'
binary_execution_stats_field = 'binary_execution_stats'
binary_arguments_field = "binary_file_arguments"

hardware_simulator_run_command_field = 'hardware_simulator_run_command'
output_path_field = 'output_path'
hardware_script_run_command_field = 'hardware_script_run_command'

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
                'params': [hardware_simulator_run_command_field, output_path_field, hardware_script_run_command_field],
                'attrs': [binary_path_field, binary_execution_stats_field, binary_arguments_field]
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
        self.simulator = None
        
        self.past_binary_path = None
        self.past_binary_time = None
        self.binary_repeat_count = 0

    def init(self, sid, time_resolution, **sim_params):
        if 'eid_prefix' in sim_params:
            self.eid_prefix = sim_params['eid_prefix']
        self.simulator = self.init_simulator()
        return self.meta

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
                if attr == binary_execution_stats_field:
                    data[eid][attr] = self.simulator.get_execution_stats()

        return data

    def step(self, time, inputs, max_advance):
        #print('hwsim stepping at time: ' + str(time))
        for eid, attrs in inputs.items():
            new_binary_path = None
            binary_arguments = None
            for attr, values in attrs.items():
                if attr == binary_path_field:
                    new_binary_path = list(values.values())[0]
                if attr == binary_arguments_field:
                    binary_arguments = list(values.values())[0]
            
            if new_binary_path is not None:
                
                if self.past_binary_path == new_binary_path and self.past_binary_time == time:
                    self.binary_repeat_count += 1
                else:
                    self.past_binary_path = new_binary_path
                    self.past_binary_time = time
                    self.binary_repeat_count = 0
                    
                self.run_binary(new_binary_path, binary_arguments, time=time, repeat=self.binary_repeat_count)
    
    def run_binary(self, binary_path, initial_binary_arguments, **relevant_attributes):
        self.simulator.run_binary(binary_path, initial_binary_arguments, **relevant_attributes)
        
        while not self.simulator.has_output():
            continue
                
if __name__ == '__main__':
    mosaik_api.start_simulation(HardwareSimulatorMosaikAPI())