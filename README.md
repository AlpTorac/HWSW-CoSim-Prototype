# HWSW-CoSim-Prototype
This repository houses a prototypical implementation of discrete-event-based co-simulation for hardware/software-co-simulation that runs on [mosaik](git-modules/mosaik-api-java/README.rst), which consists of a software simulator ([swsim](swsim)) and a hardware simulator ([hwsim](hwsim)). Swsim is implemented in Java and hwsim part is implemented in Python.

## The Components of the Repository
### The Software Simulator Part (swsim)

The software simulator is implemented using [JavaSim](git-modules/JavaSim/README.md) and [Automata](git-modules/Automata/README.md) libraries. JavaSim provides the means to run the software simulation, whereas Automata is used to model the control flow of a software using a Deterministic Finite Automaton (DFA). In this context, DFAs consist of named states and deterministic transitions between them, which use singular characters (char) as input to switch between the said states. These DFAs are supplemented with relatively small program binaries with arguments (if needed) and a transition chain (a string of taken transitions) to imitate control flows in software. This is done by associating each transition in a DFA to a binary to execute along with the arguments it needs and having the simulator run a transition chain on the mentioned DFA. Swsim works with one model at a time and one such model contains one DFA with the specifications above.

### The Hardware Simulator part (hwsim)

For hardware simulation, [gem5](git-modules/gem5/README) is used. The hwsim part only consists of Python files that adapt the input from the co-simulation to gem5 and adapt the output from gem5 to the rest of the co-simulation. To run a hardware simulation on gem5, one needs to provide a Python script, which uses modules provided by gem5 to build a hardware model, such as [hardware_script.py](hwsim/hardware_script.py).

### The Co-Simulation Framework (mosaik)

mosaik enables the co-simulation of swsim and hwsim by using their APIs implemented for mosaik. Co-simulation configurations and metadata can be provided to mosaik via scenario scripts written in python, such as [test_scenario.py](test_scenario.py) and [gem5_scenario.py](gem5_scenario.py). See [mosaik readme file](git-modules/mosaik-api-java/README.rst) for more information on mosaik, as well as its documentation. Note that the version of mosaik API for Java used here is a fork of the original GitLab-repository [mosaik-api-java](https://gitlab.com/mosaik/api/mosaik-api-java). The changes made to the original version can be found in the commits made to that submodule by the author of this project, as well as in the [changelog file](git-modules/mosaik-api-java/CHANGES.txt).

### Provided Resources

This repository comes with 2 mosaik scenario scripts:

- test_scenario.py: Runs the co-simulation with swsim and a dummy hardware simulator, which receives input and mocks output, using the resources found under [swsim test resources](swsim/src/test/resources/). Building gem5 is not necessary for running this scenario script.

- gem5_scenario.py: Runs the co-simulation with swsim and hwsim using the [hardware_script.py](hwsim/hardware_script.py) and the resources found under [gem5-scenario-resources](scenario-resources/gem5-scenario-resources/). Building gem5 with default values is required to run this scenario script.

For evaluation purposes, a script to run a co-simulation and gather detailed run time information is also provided:

- evaluation_script.py: Runs the co-simulation with swsim and hwsim using the [hardware_script.py](hwsim/hardware_script.py) and the resources found under [evaluation-scenario-resources](scenario-resources/evaluation-scenario-resources/). Building gem5 with default values is required to run this scenario script.

## Cloning and Building the Repository

This repository should be cloned with the "--recurse-submodules" option as it contains submodules. Once cloned with all its submodules, using the provided [build script](build.sh) is recommended to build the entire repository, because there are many dependencies that need to be dealt with throughout the building process, which are handled by the said script. The aforementioned script works similar to maven and requires the user to run it with arguments to determine what needs to be done. A list of these arguments as well as their descriptions can be found inside the script file or be printed in the terminal by running the script with the "help" argument.

Note: The gem5 requires the user to build it for a specific target instruction set architecture (ISA) with a specific build option. This has to be repeated for each ISA that is planned on being used. The build script from above is defaulted to build gem5 for the target ISA "X86" with the build option "opt" using (#physical_cores + 1) cores. If the gem5-related argument is used, the user will be asked to provide input regarding the mentioned properties. Leaving the input prompts empty will make the script use the default values that are shown. In order to run the provided scenarios, gem5 needs to be built with the default values given in the script.

**Attention: Building gem5 can take multiple hours depending on what is being built and on the machine that is building it.**

## Running a Scenario

Scenario scripts can be run from the terminal using Python, "python3 test_scenario.py" for instance.

## Implementing and Running a Co-Simulation

For a successful co-simulation,

- Python scripts:
    - A scenario script: Contains the metadata required to run the co-simulation, which includes simulation configuration and various parameters for simulators and models.
    - A hardware script: Has the code to build a hardware model and to assign workload to them using gem5 libraries.
- Software model resources:
    - A DFA description: Describes the DFA through listing its states, transitions, start and end states in JSON format.
    - A binary map and its binaries: Contains the mappings of each transition from a DFA to the binary that will be run upon taking the said transition with the given arguments in JSON format. **Binaries must be built on the same ISA as the gem5 build that will be used to run them**.
    - A transition chain: A list of characters and the timestamps, at which they will fire a transition, in JSON format. Entries of a transition chain are assumed to be sorted and given in the ascending order with respect to their "time" field. **There can only be one input at a time**.

are needed. The scripts that come with this repository contain commentary to help its users understand what is being done easier and these scripts can be used for reference while implementing custom scenarios.