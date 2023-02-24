import mosaik_api
import hardware_simulator

modelName = 'HWModel'

binary_path_input_name = 'binary_file_path_in'
binary_path_output_name = 'binary_file_path_out'

binary_execution_stats_output_name = 'binary_execution_stats_out'
binary_execution_stats_input_name = 'binary_execution_stats_in'
binary_arguments_input_name = "binary_file_arguments_in"
binary_arguments_output_name = "binary_file_arguments_out"

gem5_run_command_name = 'gem5_run_command'
gem5_output_path_name = 'gem5_output_path'
hardware_script_run_command_name = 'hardware_script_run_command'

META = {
    'api_version': mosaik_api.__api_version__,
    'type': 'event-based',
    'models': {
        modelName: {
            'public': True,
            'params': [gem5_run_command_name, gem5_output_path_name, hardware_script_run_command_name],
            'attrs': [binary_path_input_name, binary_path_output_name,
            binary_execution_stats_output_name, binary_execution_stats_input_name,
            binary_arguments_input_name, binary_arguments_output_name]
        },
    },
}

class HardwareSimulatorMosaikAPI(mosaik_api.Simulator):
    def __init__(self):
        super().__init__(META)
        self.eid_prefix = ''
        self.simulator = None

    def init(self, sid, time_resolution, **sim_params):
        if 'eid_prefix' in sim_params:
            self.eid_prefix = sim_params['eid_prefix']
        self.simulator = self.init_simulator()
        return self.meta

    def init_simulator(self):
        return hardware_simulator.HardwareSimulator()

    def create(self, num, model, gem5_run_command, gem5_output_path,
    hardware_script_run_command):
        entities = []

        self.simulator.init_hardware_model(gem5_run_command, gem5_output_path,
        hardware_script_run_command)
        eid = '%s%d' % (self.eid_prefix, 0)
        entities.append({'eid': eid, 'type': model})

        return entities

    def get_data(self, outputs):
        data = {}
        for eid, attrs in outputs.items():
            model = self.simulator.model
            data[eid] = {}
            for attr in attrs:
                if attr == binary_execution_stats_output_name:
                    data[eid][attr] = model.get_execution_stats()

        return data

    def step(self, time, inputs, max_advance):
        for eid, attrs in inputs.items():
            new_binary_path = None
            binary_arguments = None
            for attr, values in attrs.items():
                if attr == binary_path_output_name:
                    new_binary_path = list(values.values())[0]
                if attr == binary_arguments_output_name:
                    binary_arguments = list(values.values())[0]
            
            if new_binary_path is not None:
                self.simulator.run_binary(new_binary_path, binary_arguments, time=time)

        return None

if __name__ == '__main__':
    mosaik_api.start_simulation(HardwareSimulatorMosaikAPI())