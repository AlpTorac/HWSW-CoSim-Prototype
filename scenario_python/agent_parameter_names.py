"""_summary_
This file contains names of the parameters the model in the agent_mosaik_API.py uses
"""

binary_name_field = 'binary_name'
"""_summary_
Name of the binary file (with extention, if present) as String
"""

binary_arg_pos_field = 'binary_arg_pos'
"""_summary_
The position of the binary argument as integer (starting with 0),
which will be changed (the value of the corresponding binary argument must be a number)
"""

binary_arg_min_field = 'binary_arg_min'
"""_summary_
The minimum allowed value of the said binary argument as a number
"""

binary_arg_max_field = 'binary_arg_max'
"""_summary_
The maximum allowed value of the said binary argument as a number
"""

binary_arg_shift_magnitude_field = 'binary_arg_shift_magnitude'
"""_summary_
How much the value of the said argument will change as a number.
It will change positively, if the current value of the criterium is too small, and negatively, if
the current value of the criterium is too large. This behaviour can be inverted by providing a negative
value for this parameter
"""

binary_stat_criterium_field = 'binary_stat_criterium'
"""_summary_
An entry in the binary execution statistics, which will be used as a
criterium to adjust the value of a binary argument
"""

criterium_target_field = 'criterium_target'
"""_summary_
The desired numerical value for the mentioned criterium
"""

tolerance_field = 'tolerance'
"""_summary_
The largest allowed number equal to abs(criterium_target - actual criterium value)
"""

max_runs_field = 'max_runs'
"""_summary_
The maximum amount of times the said binary will be run with its argument
at position binary_arg_pos being adjusted
"""