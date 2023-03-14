import mosaik_api
import agent
import pathlib

import sys
sys.path.append('./scenario_python')
from scenario_fields import *

modelName = 'Agent'

class AgentMosaikAPI(mosaik_api.Simulator):
    """_summary_
    This is an agent that implements mosaik_api.Simulator and manipulates a
    given binary argument from a binary_path, binary_arguments pair with the
    goal of finding the best value for the said argument, using the parameters
    given with agent_parameters_field.
    """

    meta = {
        'api_version': mosaik_api.__api_version__,
        type_field: 'event-based',
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
        
        self.agent_output_dir = None
        """_summary_
        The absolute path of the output directory, in which the agent will create the file
        self.agent_output_file_name.
        """
        self.agent_output_file_name = None
        """_summary_
        The name and the extension of the file, which the agent will create and fill with its output.
        """
        self.agent_binaries_run = []
        """_summary_
        A list of dicts, each containing information about all runs from the runs of a binary at the same time step.
        
        The said dicts contain entries for self.binary_path (as String), self.current_binary_arguments (as list[Any])
        and all statistics from self.binary_execution_stats (as list[Any]), where values with the same statistic name
        are gathered in lists. Each statistic related entry will look like the following:
        
                                    'stat_name': [val_1, val_2, ..., val_N]
        """
        self.current_binary_arguments = []
        """_summary_
        The list of arguments from past runs of the binary within the same time step at current self.binary_path
        """
        
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
        if agent_output_dir_field in sim_params:
            self.agent_output_dir = sim_params[agent_output_dir_field]
        if agent_output_file_name_field in sim_params:
            self.agent_output_file_name = sim_params[agent_output_file_name_field]
        if eid_prefix_field in sim_params:
            self.eid_prefix = sim_params[eid_prefix_field]
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
        entities.append({eid_field: eid, type_field: model})

        return entities

    def binary_run_count_reached(self):
        """_summary_
        Returns:
            _type_: True, if the current binary has reached its maximum allowed run count
        """
        return self.binary_run_count >= self.agent.get_max_repeat_count(self.binary_path)

    def add_agent_output(self):
        """_summary_
        Generates and stores output from all recorded arguments and execution statistics
        from the current binary (the one at self.binary_path).
        
        See self.agent_binaries_run for more information about its format.
        """
        all_execution_stats = {}
        
        if len(self.binary_execution_stats) > 0:
            for stat in self.binary_execution_stats:
                for key, value in stat.items():
                    if key in all_execution_stats:
                        all_execution_stats[key].append(value)
                    else:
                        all_execution_stats[key] = [value]
                    
        agent_binary_output = {
            binary_path_field: self.binary_path,
            binary_arguments_field: [binary_arguments_list for binary_arguments_list in self.current_binary_arguments],
        }
        
        if all_execution_stats is not None:
            agent_binary_output = {**agent_binary_output, **all_execution_stats}
        
        self.agent_binaries_run.append(agent_binary_output)

    def reset_binary_attributes(self):
        """_summary_
        Reset every member of this instance that is
        relevant to binaries.
        """
        
        self.add_agent_output()
        
        self.current_binary_arguments = []
        self.binary_execution_stats = []
        self.binary_path = None
        self.binary_arguments = None
        self.binary_stats = None
        self.binary_run_count = 0

    def binary_run_parameters_sent(self):
        """_summary_
        Call when the agent requests the execution of a binary.
        
        Increments the run count of the binary and stores the arguments passed along
        with the binary.
        """
        self.binary_run_count += 1
        self.current_binary_arguments.append([arg for arg in self.binary_arguments])

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
                
                self.binary_run_parameters_sent()
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
    
    def finalize(self):
        """_summary_
        Create and write the output of the agent to the file defined by self.agent_output_dir
        and self.agent_output_file_name. Creates any non-existing directories in the process.
        """
        path = pathlib.Path(self.agent_output_dir)
        path.mkdir(parents=True, exist_ok=True)
        
        file = open(self.agent_output_dir+'/'+self.agent_output_file_name, 'w')

        run_binaries = self.agent_binaries_run
        
        for binary_dict in run_binaries:
            file.write('\n')
            for binary_attribute, value in binary_dict.items():
                file.write(binary_attribute+': '+str(value)+'\n')
        
        file.close()

    
if __name__ == '__main__':
    mosaik_api.start_simulation(AgentMosaikAPI())