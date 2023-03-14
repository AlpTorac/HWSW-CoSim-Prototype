"""_summary_
This file contains helper methods, which can be used while implementing
scenarios.
"""

import os
import re
import fnmatch
import json

def get_all_swsim_dependencies(abs_root_dir):
    """_summary_

    Scans all files and folders inside the repository and returns
    the desired resources.

    Args:
        abs_root_dir (_type_): Absolute path to the root of this
        repository

    Returns:
        _type_: A tuple (dependencies, swsim_jar_path), where:
        
        dependencies: Every jar dependency the swsim needs
        swsim_jar_path: The absolute path to the swsim's jar file
    """
    # Gather all .jar files inside the project
    dependencies = ''
    swsim_jar_pattern = re.compile('swsim.*\.jar')
    swsim_jar_path = ''
    for root, dirnames, filenames in os.walk(abs_root_dir):
        for filename in fnmatch.filter(filenames, '*.jar'):
            dependencies += ':' + os.path.join(root, filename)
            if swsim_jar_pattern.match(filename) is not None:
                swsim_jar_path = os.path.join(root, filename)
                
    return (dependencies, swsim_jar_path)

def get_mosaik_end_value(abs_path_to_transition_chain_file, time_field='time'):
    """_summary_

    Reads the given transition chain file and returns the END value for
    mosaik.

    Args:
        abs_path_to_transition_chain_file (_type_): The absolute path to the
        transition chain file.
        time_field (str, optional): The name of the field for time.
        Defaults to 'time'.

    Returns:
        _type_: The value for END value
    """
    
    transition_chain_file = open(abs_path_to_transition_chain_file)
    transition_chain_json = json.loads(transition_chain_file.read())
    last_entry = (transition_chain_json)[-1]
    
    """
    END needs a buffer of at least 2 time steps, otherwise the simulators cannot
    receive their last input before the co-simulation is terminated.
    
    Receiving input is a part of the step() method and if the time it outputs
    is >= END, step() will not be called again. Therefore, to ensure that step() is
    called to receive the last input, one has to give it a buffer of at least 2
    time steps.
    """
    end_value = int(last_entry[time_field]) + 2
    
    transition_chain_file.close()
    
    return end_value