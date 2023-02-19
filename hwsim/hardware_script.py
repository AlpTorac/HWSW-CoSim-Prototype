#import sys, getopt
# import the m5 (gem5) library created when gem5 is built
import m5
# import all of the SimObjects
from m5.objects import *

import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--binary_path",
                    help="Path to the binary to execute.")
parser.add_argument("--binary_args",
                    help="List of binary arguments")

options = parser.parse_args()

# binary_args is a single string, parse individual arguments, if necessary
binary_args = options.binary_args

binary_path = options.binary_path

# Create the system
system = System()

# Set the clock frequency of the system (and all of its children)
system.clk_domain = SrcClockDomain()
system.clk_domain.clock = "1GHz"
system.clk_domain.voltage_domain = VoltageDomain()

# Set up the system
system.mem_mode = "timing"  # Use timing accesses
system.mem_ranges = [AddrRange("512MB")]  # Create an address range

# Create a simple CPU
# You can use ISA-specific CPU models for different workloads:
# `RiscvTimingSimpleCPU`, `ArmTimingSimpleCPU`.
system.cpu = X86TimingSimpleCPU()

# Create a memory bus, a system crossbar, in this case
system.membus = SystemXBar()

# Hook the CPU ports up to the membus
system.cpu.icache_port = system.membus.cpu_side_ports
system.cpu.dcache_port = system.membus.cpu_side_ports

# create the interrupt controller for the CPU and connect to the membus
system.cpu.createInterruptController()

# For X86 only we make sure the interrupts care connect to memory.
# Note: these are directly connected to the memory bus and are not cached.
# For other ISA you should remove the following three lines.
system.cpu.interrupts[0].pio = system.membus.mem_side_ports
system.cpu.interrupts[0].int_requestor = system.membus.cpu_side_ports
system.cpu.interrupts[0].int_responder = system.membus.mem_side_ports

# Create a DDR3 memory controller and connect it to the membus
system.mem_ctrl = MemCtrl()
system.mem_ctrl.dram = DDR3_1600_8x8()
system.mem_ctrl.dram.range = system.mem_ranges[0]
system.mem_ctrl.port = system.membus.mem_side_ports

# Connect the system up to the membus
system.system_port = system.membus.cpu_side_ports

# Here we set the X86 "hello world" binary. With other ISAs you must specify
# workloads compiled to those ISAs. Other "hello world" binaries for other ISAs
# can be found in "tests/test-progs/hello".
#thispath = os.path.dirname(os.path.realpath(__file__))
#binary = os.path.join(
#    thispath,
#    "../gem5/tests/test-progs/hello/bin/x86/linux/hello",
#)

system.workload = SEWorkload.init_compatible(binary_path)

# Create a process
process = Process()

# cmd is a list which begins with the executable (like argv)
# each element in the list after binary_path is an argument
process.cmd = [binary_path, binary_args]
# Set the cpu to use the process as its workload and create thread contexts
system.cpu.workload = process
system.cpu.createThreads()

# set up the root SimObject and start the simulation
root = Root(full_system=False, system=system)
# instantiate all of the objects we've created above
m5.instantiate()

print("Beginning simulation!")
exit_event = m5.simulate()
print("Exiting @ tick %i because %s" % (m5.curTick(), exit_event.getCause()))