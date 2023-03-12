import re

class Agent():
    def __init__(self, params):
        self.params = params
        self.binary_name_pattern = re.compile('(?:/.*/)*(.*)')
    
    def get_binary_name(self, binary_path):
        return self.binary_name_pattern.match(binary_path).group(1)
    
    def process_stats(self, binary_path, binary_args, stats):
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
            
            if abs(target_exec_time - run_time) <= tolerance:
                return None
            
            elif run_time > target_exec_time:
                arg = arg - binary_arg_shift
                
                if arg < binary_min_arg:
                    arg = binary_min_arg
                    return None
                
                new_binary_args[arg_pos] = arg
                return new_binary_args
            
            else:
                arg = arg + binary_arg_shift
                
                if arg > binary_max_arg:
                    arg = binary_max_arg
                    return None
                
                new_binary_args[arg_pos] = arg
                return new_binary_args
        
        return None
    
    def get_param_for(self, binary_path):
        binary_name = self.get_binary_name(binary_path)
        for param in self.params:
            if param['binary_name'] == binary_name:
                return param
            
        return None
    
    def get_max_repeat_count(self, binary_path):
        param = self.get_param_for(binary_path)
        
        if param is not None:
            return int(param['max_repeats'])
        
        return None