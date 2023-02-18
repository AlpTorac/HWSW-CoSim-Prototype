import mosaik_api
import hardware_model
import hardware_simulator

modelName = 'HWModel'

binaryPathInputName = 'binary_file_path_in'
binaryPathOutputName = 'binary_file_path_out'

binaryExecutionStatsOutputName = 'binary_execution_stats_out'
binaryExecutionStatsInputName = 'binary_execution_stats_in'

META = {
    'api_version': mosaik_api.__api_version__,
    'type': 'event-based',
    'models': {
        modelName: {
            'public': True,
            'params': [],
            'attrs': [binaryPathInputName, binaryPathOutputName, binaryExecutionStatsOutputName, binaryExecutionStatsInputName]
        },
    },
}

class HardwareSimulatorMosaikAPI(mosaik_api.Simulator):
    def __init__(self):
        super().__init__(META)
        self.eid_prefix = ''
        self.simulator = hardware_simulator.HardwareSimulator()

    def init(self, sid, time_resolution, eid_prefix=None):
        if float(time_resolution) != 1.:
            raise ValueError('ExampleSim only supports time_resolution=1., but'
                             ' %s was set.' % time_resolution)
        if eid_prefix is not None:
            self.eid_prefix = eid_prefix
        return self.meta

    def create(self, num, model):
        entities = []

        self.simulator.add_hardware_model()
        eid = '%s%d' % (self.eid_prefix, 0)
        entities.append({'eid': eid, 'type': model})

        return entities

    def get_data(self, outputs):
        data = {}
        for eid, attrs in outputs.items():
            model = self.simulator.model
            data[eid] = {}
            for attr in attrs:
                if attr == binaryExecutionStatsOutputName:
                    data[eid][attr] = model.get_execution_stats()

        return data

    def step(self, time, inputs, max_advance):
        for eid, attrs in inputs.items():
            for attr, values in attrs.items():
                if attr == binaryPathOutputName:
                    new_binary_path = list(values.values())[0]
                    if new_binary_path != None:
                        self.simulator.model.set_current_binary_path(new_binary_path)

        return None

if __name__ == '__main__':
    mosaik_api.start_simulation(HardwareSimulatorMosaikAPI())