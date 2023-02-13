import mosaik
import mosaik.util

SIM_CONFIG = {
    'SoftwareSimulator': {
        'cmd': 'java -cp swsim/target/swsim-1.jar:mosaik-api-java/dist/*:JavaSim/target/javasim-2.3.jar:Automata/target/automata-0.2.2-SNAPSHOT.jar hwswcosim.swsim.SoftwareSimulatorMosaikAPI %(addr)s',
    },
    'DummyHWSimulator': {
        'cmd': 'java -cp swsim/target/swsim-1.jar:mosaik-api-java/dist/*:JavaSim/target/javasim-2.3.jar:Automata/target/automata-0.2.2-SNAPSHOT.jar hwswcosim.swsim.DummyHWSimulator %(addr)s',
    },
}
END = 7

# Create World
world = mosaik.World(SIM_CONFIG)

# Start simulators
software_simulator = world.start('SoftwareSimulator', eid_prefix='SoftwareDFA_')
hardware_simulator = world.start('DummyHWSimulator', eid_prefix='HWModel_')

# Instantiate models
sw_model = software_simulator.DFAWrapper(
    dfa_file_path="/root/HWSW-CoSim-Prototype-SWSim/swsim/src/test/resources/dfa.json",
    transition_to_binary_map_file_path="/root/HWSW-CoSim-Prototype-SWSim/swsim/src/test/resources/binaryMap.json",
    transition_chain_file_path="/root/HWSW-CoSim-Prototype-SWSim/swsim/src/test/resources/transitionChain.json")

hw_model = hardware_simulator.DummyHWModel(init_val=1)

world.connect(sw_model, hw_model, 'binary_file_path_out', 'binary_file_path_in')
world.connect(hw_model, sw_model, 'binary_execution_stats_out', 'binary_execution_stats_in', weak=True)

# Run simulation
world.set_initial_event(software_simulator._sim.sid, time=0)
world.run(until=END)