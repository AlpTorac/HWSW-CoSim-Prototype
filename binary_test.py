"""_summary_
This script can be used to estimate how long a binary will take to run
on the actual machine.

Call command:

python3 binary_test.py binary_run_count binary_call_command binary_arg_1 binary_arg_2 ... binary_arg_N
"""

import sys
import time
import subprocess

total_time = 0
count = int(sys.argv[1])

for i in range(count):
    start_time = time.time_ns()
    subprocess.run(sys.argv[2:])
    end_time = time.time_ns()
    total_time += (end_time - start_time)
    
print(str((total_time/count)/1000000000))