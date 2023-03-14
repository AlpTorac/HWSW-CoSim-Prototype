# HWSW-CoSim-Prototype
This repository houses a prototypical implementation of discrete-event-based co-simulation for hardware/software-co-simulation that runs on [mosaik](git-modules/mosaik-api-java/README.rst), which consists of a software simulator ([swsim](swsim)), a hardware simulator ([hwsim](hwsim)) and an agent ([agent](agent)). Swsim is implemented in Java, hwsim and agent parts are implemented in Python.

Note: The prototype has only been tested on Ubuntu 22.04 (WSL2). Since gem5, which has been used for the hwsim part, only runs on linux (as of writing this file),
the prototype is likely to not work on any other operating system.

## The Components of the Repository

Each component of the prototype is explained below, in their corresponding subsection.

### The Software Simulator part (swsim)

The software simulator is implemented using [JavaSim](git-modules/JavaSim/README.md) and [Automata](git-modules/Automata/README.md) libraries. JavaSim provides the means to run the software simulation, whereas Automata is used to model the control flow of a software using a Deterministic Finite Automaton (DFA). In this context, DFAs consist of named states and deterministic transitions between them, which use singular characters (char) as input to switch between the said states. These DFAs are supplemented with relatively small program binaries with arguments (if needed) and a transition chain (a string of taken transitions) to imitate control flows in software. This is done by associating each transition in a DFA to a binary to execute along with the arguments it needs and having the simulator run a transition chain on the mentioned DFA. Swsim works with one model at a time and one such model contains one DFA with the specifications above.

### The Hardware Simulator part (hwsim)

For hardware simulation, [gem5](git-modules/gem5/README) is used. The hwsim part only consists of Python files that adapt the input from the co-simulation to gem5 and adapt the output from gem5 to the rest of the co-simulation. To run a hardware simulation on gem5, one needs to provide a Python script, which uses resources provided by gem5 to build a hardware model, such as [hardware_script.py](hwsim/hardware_script.py).

### The Agent part (agent)

The agent component is a tool, which can be used to adjust a single numerical binary argument by a fix amount to fine-tune it to get it closer to a value that meets the predefined execution statistic criterium (see [agent_scenario.py](agent_scenario.py)). To this end, the agent runs the binary with the given arguments, where one of the arguments is being changed after every run. A maximum run limit is given to the agent to make sure it does not make the hwsim run the same binary too long, if the given criterium cannot be reached.

Note that as of now, an agent can only manipulate a single binary argument per binary based on a single criterium numerically and the way this manipulation works (see process_stats() in [agent.py](agent/agent.py)) cannot be changed, one can only change the parameters given to the agent in scenarios.

### The Co-Simulation Framework (mosaik)

mosaik enables the co-simulation of swsim and hwsim by using their APIs implemented for mosaik. Co-simulation configurations and metadata can be provided to mosaik via scenario scripts written in python, such as [test_scenario.py](test_scenario.py) and [gem5_scenario.py](gem5_scenario.py). See [mosaik readme file](git-modules/mosaik-api-java/README.rst) for more information on mosaik, as well as its documentation. Note that the version of mosaik API for Java used here is a fork of the original GitLab-repository [mosaik-api-java](https://gitlab.com/mosaik/api/mosaik-api-java). The changes made to the original version can be found in the commits made to that submodule by the author of this project, as well as in the [changelog file](git-modules/mosaik-api-java/CHANGES.txt).

### Provided Resources

This repository comes with mosaik scenario scripts, along with the [resources](scenario-resources/) they use:

- [test_scenario.py](test_scenario.py): Runs the co-simulation with swsim and a dummy hardware simulator, which receives input and mocks output, using the resources found under [swsim test resources](swsim/src/test/resources/). Building gem5 is not necessary for running this scenario script. It can be run by entering "python3 test_scenario.py" in the terminal.

- [gem5_scenario.py](gem5_scenario.py): Runs the co-simulation with swsim and hwsim using the [hardware_script.py](hwsim/hardware_script.py) and the resources found under [gem5-scenario-resources](scenario-resources/gem5-scenario-resources/). Building gem5 with default values is required to run this scenario script. It can be run by entering "python3 gem5_scenario.py" in the terminal.

- [agent_scenario.py](agent_scenario.py): Runs the co-simulation with swsim, hwsim, [hardware_script.py](hwsim/hardware_script.py) and the agent with the resources found under [agent-sceario-resources](scenario-resources/agent-scenario-resources/). Check the scenario file to find out more about the parameters the agent uses. Building gem5 with default values is required to run this scenario script. It can be run by entering "python3 agent_scenario.py" in the terminal.

For evaluation purposes, a script to run a co-simulation and gather detailed run time information is also provided:

- [evaluation_script.py](evaluation_script.py): Runs the co-simulation described in [evaluation_scenario.py](evaluation_scenario.py) for a given "number_of_eval_runs" times and runs all binaries simulated on the actual machine based on how many times they have been simulated * a given "binary_run_multiplier". Building gem5 with default values is required to run this scenario script. It can be run by entering "python3 evaluation_script.py number_of_eval_runs binary_run_multiplier" in the terminal. **For evaluation purposes, running the evaluation_script multiple time is highly recommended to get more accurate results, if the machine running the evaluation_script uses caches**.

## Cloning and Building the Repository

This repository should be cloned with the "--recurse-submodules" option as it contains submodules. Once cloned with all its submodules, using the provided [build script](build.sh) is recommended to build the entire repository, because there are many dependencies that need to be dealt with throughout the building process, which are handled by the said script. The aforementioned script works similar to maven and requires the user to run it with arguments to determine what needs to be done. A list of these arguments as well as their descriptions can be found inside the script file or be printed in the terminal by running the script with the "help" argument.

Note: The gem5 requires the user to build it for a specific target instruction set architecture (ISA) with a specific build option. This has to be repeated for each ISA that is planned on being used. The build script from above is defaulted to build gem5 for the target ISA "X86" with the build option "opt" using (#physical_cores + 1) cores. If the gem5-related argument is used, the user will be asked to provide input regarding the mentioned properties. Leaving the input prompts empty will make the script use the default values that are shown. In order to run the provided scenarios, gem5 needs to be built with the default values given in the script.

**Attention: Building gem5 can take multiple hours depending on what is being built and on the machine that is building it.**

## Implementing and Running a Co-Simulation

For a successful co-simulation,

- Python scripts:
    - A scenario script: Contains the metadata required to run the co-simulation, which includes simulation configuration and various parameters for simulators and models.
    - A hardware script: Has the code to build a hardware model and to assign workload to them using gem5 libraries.
- Software model resources:
    - A DFA description: Describes the DFA through listing its states, transitions, start and end states in JSON format.
    - A binary map and its binaries: Contains the mappings of each transition from a DFA to the binary that will be run upon taking the said transition with the given arguments in JSON format. **Binaries must be built on the same ISA as the gem5 build that will be used to run them**.
        - The source files used to generate the mentioned binaries are not required.
    - A transition chain: A list of characters and the timestamps, at which they will fire a transition, in JSON format. Entries of a transition chain are assumed to be sorted and given in the ascending order with respect to their "time" field. **There can only be one input at a time**.

are needed. The scripts that come with this repository contain commentary to help its users understand what is being done easier and these scripts can be used for reference while implementing custom scenarios. Any directory under [scenario-resources](scenario-resources/) can be used as reference for making custom resource files.

Descriptions to parameters passed to the partaking simulators/components as well as some utility methods can be found under [scenario_python](scenario_python/). **Note that any change made to swsim's parameters will require changing them in their source files under [swsim](swsim/) manually**