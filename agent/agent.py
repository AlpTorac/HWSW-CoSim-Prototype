import re

class Agent():
    """_summary_
    This class manipulates binary arguments based on the
    parameters it is given.
    """
    def __init__(self, params):
        self.params = params
        """_summary_
        Parameters given to this Agent instance.
        """
        self.binary_name_pattern = re.compile('(?:/.*/)*(.*)')
        """_summary_
        A pattern that matches path names and captures the name
        of the file / last directory in group(1).
        """
    
    def get_binary_name(self, binary_path):
        """_summary_
        Args:
            binary_path (_type_): A given binary path
        Returns:
            _type_: The name of the binary
        """
        return self.binary_name_pattern.match(binary_path).group(1)
    
    def process_stats(self, binary_path, binary_args, stats):
        """_summary_
        
        Adjusts a binary argument based on the recent execution statistics
        of the binary at binary_path. See agent_mosaik_API.agent_parameter_field
        for more information.

        Args:
            binary_path (_type_): The path to the binary
            binary_args (_type_): The arguments, with which the binary ran
            stats (_type_): The most recent execution statistics from the binary

        Returns:
            _type_:
                    None, if either the execution time from stats was acceptable
                    or the value of the said argument lies out of bounds.
                    
                    New binary arguments as a list of numbers, if the execution time
                    from stats was unacceptable and the value of the said argument
                    is within the boundaries.
        """
        param = self.get_param_for(binary_path)
        
        if param is not None and stats is not None:
            new_binary_args = binary_args
            
            run_time = float(stats['hostSeconds'])
            arg_pos = int(param['binary_arg_pos'])
            arg = float(new_binary_args[arg_pos])
            target_exec_time = float(param['target_exec_time'])
            tolerance = float(param['tolerance'])
            binary_max_arg = float(param['binary_arg_max'])
            binary_min_arg = float(param['binary_arg_min'])
            binary_arg_shift = float(param['binary_arg_shift_magnitude'])
            
            # Run time was tolerable
            if abs(target_exec_time - run_time) <= tolerance:
                return None
            # Run time was too long
            elif run_time > target_exec_time:
                arg = arg - binary_arg_shift
            # Run time was too short
            else:
                arg = arg + binary_arg_shift
                
            # Check the boundaries of the argument
            if arg < binary_min_arg:
                arg = binary_min_arg
                return None
            elif arg > binary_max_arg:
                arg = binary_max_arg
                return None
            else:
                new_binary_args[arg_pos] = arg
                return new_binary_args

        return None
    
    def get_param_for(self, binary_path):
        """_summary_
        Args:
            binary_path (_type_): A given binary path
        Returns:
            _type_: The parameter entry for the matching binary
        """
        binary_name = self.get_binary_name(binary_path)
        for param in self.params:
            if param['binary_name'] == binary_name:
                return param
            
        return None
    
    def get_max_repeat_count(self, binary_path):
        """_summary_
        Args:
            binary_path (_type_): A given binary path
        Returns:
            _type_: The maximum amount of times the said binary
            is to be run.
        """
        param = self.get_param_for(binary_path)
        
        if param is not None:
            return int(param['max_runs'])
        
        return None