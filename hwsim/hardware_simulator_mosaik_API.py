import mosaik_api
import hardware_simulator

modelName = 'HWModel'

binary_path_field = 'binary_file_path'
"""_summary_
The absolute path to the binary file as a String, which will be run by an outside component.
"""
binary_execution_stats_field = 'binary_execution_stats'
"""_summary_
Binary execution statistics received in either json object format (if there is only a
single statistics object) or json array of json object (if there can be multiple statistics objects. One
such json array can also have a single json object).

For each statistic, there is a name field (has to be String) and a value field (Any).

Format:
     json object: {"stat_name_1": stat_value_1, ..., "stat_name_N": stat_value_N}
     json array: [json_object_1, ..., json_object_M]
"""
binary_arguments_field = "binary_file_arguments"
"""_summary_
Binary arguments that belong with the binary from binary_path_field as a list.
Any type can be given as argument. As of now, it is not possible to define variables
as arguments.

Format: [arg1, arg2, ..., arg3]
"""

hardware_simulator_run_command_field = 'hardware_simulator_run_command'
"""_summary_
The command, with which the hardware will be run from the terminal.
"""
output_path_field = 'output_path'
"""_summary_
The path, at which the hardware simulator will generate its output. Should
be passed as an argument, when hardware_simulator_run_command_field is called
from the terminal.
"""
hardware_script_run_command_field = 'hardware_script_run_command'
"""_summary_
The command, with which the hardware model that will run the simulation
will be built.
"""

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