import mosaik_api
import agent

modelName = 'Agent'

binary_path_input_field = 'binary_file_path_in'
"""_summary_
The absolute path to the binary file as a String, which is received.
"""
binary_path_output_field = 'binary_file_path_out'
"""_summary_
The absolute path to the binary file as a String, which will be sent.
"""

binary_execution_stats_output_field = 'binary_execution_stats_out'
"""_summary_
Binary execution statistics to be sent as json array of json object (one
such json array can contain one or more json object). Each said json object
contains statistics.

stat_name_i have to be strings, stat_value_i can be of any type.

Format:
     json object: {"stat_name_1": stat_value_1, ..., "stat_name_N": stat_value_N}
     json array: [json_object_1, ..., json_object_M]
"""
binary_execution_stats_input_field = 'binary_execution_stats_in'
"""_summary_
Binary execution statistics received in json object format.

stat_name_i have to be strings, stat_value_i can be of any type.

Format:
     json object: {"stat_name_1": stat_value_1, ..., "stat_name_N": stat_value_N}
"""

binary_arguments_input_field = "binary_file_arguments_in"
"""_summary_
Binary arguments as a list that the agent receives.

Format: [arg1, arg2, ..., arg3]
"""
binary_arguments_output_field = "binary_file_arguments_out"
"""_summary_
Binary arguments as a list that the agent sends.

Format: [arg1, arg2, ..., arg3]
"""

agent_parameters_field = "agent_parameters"
"""_summary_
A json object filled with parameters that will be used by
the agent. The list of variables as of writing this comment is:

binary_name: Name of the binary file (with extention, if present) as String

binary_arg_pos: The position of the binary argument as integer (starting with 0),
which will be changed (the value of the corresponding binary argument must be a number)

binary_arg_min: The minimum allowed value of the said binary argument as a number

binary_arg_max: The maximum allowed value of the said binary argument as a number

binary_arg_shift_magnitude: How much the value of the said argument will change as a number.
It will change positively, if the current execution time is too short, and negatively, if
the current execution time is too long. This behaviour can be inverted by providing a negative
value for this parameter

target_exec_time: The desired run time (in seconds) as a number

tolerance: The largest allowed number equal to abs(target_exec_time - actual execution time)

max_runs: The maximum amount of times the said binary will be run with its argument
at position binary_arg_pos being adjusted
"""
class AgentMosaikAPI(mosaik_api.Simulator):
    """_summary_
    This is an agent that implements mosaik_api.Simulator and manipulates a
    given binary argument from a binary_path, binary_arguments pair with the
    goal of finding the best value for the said argument, using the parameters
    given with agent_parameters_field.
    """

    meta = {
        'api_version': mosaik_api.__api_version__,
        'type': 'event-based',
        'models': {
            modelName: {
                'public': True,
                'params': [agent_parameters_field],
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
        """_summary_
        The absolute path of the binary that is currently being run.
        """
        self.binary_arguments = None
        """_summary_
        The current arguments, with which the binary at self.binary_path
        is being run.
        """
        self.binary_stats = None
        """_summary_
        The execution statistics from the most recent binary run.
        """
        self.binary_run_count = 0
        """_summary_
        The amount of times the binary at self.binary_path has been
        run so far.
        """
        
        self.binary_execution_stats = []
        """_summary_
        The list of binary execution statistics to be sent, once
        the binary at self.binary_path has been run enough times.
        """

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
        
        if agent_parameters_field in model_params:
            self.agent = self.init_agent(model_params[agent_parameters_field])
            
        eid = '%s%d' % (self.eid_prefix, 0)
        entities.append({'eid': eid, 'type': model})

        return entities


    def reset_binary_attributes(self):
        """_summary_
        Reset every member of this instance that is
        relevant to binaries.
        """
        self.binary_execution_stats = []
        self.binary_path = None
        self.binary_arguments = None
        self.binary_stats = None
        self.binary_run_count = 0

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
                        self.reset_binary_attributes()

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
            
            # Process the received binary path, arguments and execution statistics
            # if all of them are present and change binary arguments accordingly
            if self.binary_path is not None \
                and self.binary_arguments is not None \
                and self.binary_stats is not None:
                    
                self.binary_arguments = self.agent.process_stats(self.binary_path, self.binary_arguments, self.binary_stats)
        
        return None

    def binary_run_count_reached(self):
        return self.binary_run_count >= self.agent.get_max_repeat_count(self.binary_path)
    
if __name__ == '__main__':
    mosaik_api.start_simulation(AgentMosaikAPI())