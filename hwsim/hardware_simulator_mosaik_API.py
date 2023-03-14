import mosaik_api
import hardware_simulator

import sys
sys.path.append('./scenario_python')
from scenario_fields import *

modelName = 'HWModel'

class HardwareSimulatorMosaikAPI(mosaik_api.Simulator):
    """_summary_
    This is the concrete implementation of mosaik_api.Simulator for the
    hardware simulator.
    """

    meta = {
        'api_version': mosaik_api.__api_version__,
        type_field: 'event-based',
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
        """_summary_
        The most recently run binary's path.
        """
        self.past_binary_time = None
        """_summary_
        The time, at which the most recent binary was run.
        """
        self.binary_run_count = 0
        """_summary_
        The amount of times the binary at self.past_binary_path
        was run at self.past_binary_time. Should be reset if either
        self.past_binary_path or self.past_binary_time changes.
        """

    def init(self, sid, time_resolution, **sim_params):
        if eid_prefix_field in sim_params:
            self.eid_prefix = sim_params[eid_prefix_field]
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
        entities.append({eid_field: eid, type_field: model})

        return entities

    def get_data(self, outputs):
        data = {}
        for eid, attrs in outputs.items():
            data[eid] = {}
            for attr in attrs:
                if attr == binary_execution_stats_field:
                    data[eid][attr] = self.simulator.get_execution_stats()

        return data

    def adjust_binary_attributes(self, new_binary_path, time):
        """_summary_

        Adjusts self.past_binary_path, self.past_binary_time
        and self.binary_repeat_count.

        Args:
            new_binary_path (_type_): The absolute path of the binary to run
            time (_type_): The time, at which new_binary_path was received
        """
        if self.past_binary_path == new_binary_path and self.past_binary_time == time:
            self.binary_run_count += 1
        else:
            self.past_binary_path = new_binary_path
            self.past_binary_time = time
            self.binary_run_count = 0

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
                self.adjust_binary_attributes(new_binary_path, time)
                self.run_binary(new_binary_path, binary_arguments, time=time, run=self.binary_run_count)
    
    def run_binary(self, binary_path, binary_arguments, **relevant_attributes):
        """_summary_

        Runs the binary at the given path with the given arguments and waits
        for the underlying hardware simulator to generate its runtime statistics
        as output.
        
        Args:
            binary_path (_type_): A given binary path as absolute path
            binary_arguments (_type_): A list of binary arguments
        """
        self.simulator.run_binary(binary_path, binary_arguments, **relevant_attributes)
        
        while not self.simulator.has_output():
            continue
                
if __name__ == '__main__':
    mosaik_api.start_simulation(HardwareSimulatorMosaikAPI())