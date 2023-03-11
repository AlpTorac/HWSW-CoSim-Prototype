package hwswcosim.swsim;

import java.util.Collection;
import java.util.Map;

import org.javasim.RestartException;
import org.javasim.Simulation;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This class manages the software simulation by acting as its controller.
 */
public class SoftwareSimulationController extends SimulationProcess {
    private SoftwareSimulator simulator;

    private volatile boolean isSimulationTerminated;
    private volatile boolean isSimulationRunning;
    private volatile boolean hasSimulationBegun;
    private volatile boolean isTransitionEventRunning;

    public SoftwareSimulationController() {
        this.hasSimulationBegun = false;
        this.isSimulationTerminated = false;
        this.isSimulationRunning = false;
        this.isTransitionEventRunning = false;
    }

    /**
     * Initialises the software simulation by instantiating the relevant classes.
     * 
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside
     * @param DFAFileName The name of the file that describes the DFA
     * @param binaryMapFileName The name of the file that contains all {@link BinaryMapEntry} information
     */
    public void initSoftwareSimulation(String resourceFolderPath, String DFAFileName, String binaryMapFileName) {
        this.initSoftwareSimulator();
        this.initModel(resourceFolderPath, DFAFileName, binaryMapFileName);
    }

    protected void initSoftwareSimulator() {
        this.simulator = new SoftwareSimulator();
    }

    /**
     * See {@link SoftwareSimulator#addDFAWrapper(String, String, String)}
     * 
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside
     * @param DFAFileName The name of the file that describes the DFA
     * @param binaryMapFileName The name of the file that contains all {@link BinaryMapEntry} information
    */
    protected void initModel(String resourceFolderPath, String DFAFileName, String binaryMapFileName) {
        this.simulator.addDFAWrapper(resourceFolderPath, DFAFileName, binaryMapFileName);
    }

    /**
     * Creates and schedules the next transition.
     * 
     * @param input The input of the next transition
     * @param time The time of the next transition
     * 
     * @return The created {@link TransitionEvent} instance
     */
    protected TransitionEvent scheduleNextTransitionEvent(Character input, Number time) {
        TransitionEvent event = new TransitionEvent(input.charValue());
        try {
            event.activateAt(time.doubleValue());
        } catch (SimulationException | RestartException e) {
            e.printStackTrace();
        }
        System.out.println("Next event scheduled at time: " + time.doubleValue());
        return event;
    }

    /**
     * The implementation of {@link SimulationProcess#run().
     * 
     * Starts the simulation {@link Simulation#start()},
     * waits for {@link #isSimulationRunning()} = false and
     * stops the simulation {@link Simulation#stop()}.
     */
    @Override
    public void run() {
        System.out.println("SWSimulator started");
        Simulation.start();
        System.out.println("SWSimulation running");

        this.hasSimulationBegun = true;
        this.isSimulationRunning = true;
        while (this.isSimulationRunning()) {
            
        }

        Simulation.stop();
        System.out.println("SWSimulator stopped");
        this.exit();
    }

    protected SoftwareSimulator getSoftwareSimulator() {
        return this.simulator;
    }

    /**
     * @see {@link SimulationProcess#resumeProcess()}
     */
    public void await() {
        this.resumeProcess();
    }

    /**
     * Call to end the software simulation.
     */
    public void exit() {
        System.out.println("Exiting simulation");
        this.isSimulationTerminated = true;
        System.out.println("Exited simulation");
    }

    /**
     * @see {@link SoftwareSimulator#hasBinaryFilePath()}
     */
    public boolean hasBinaryFilePath() {
        return this.simulator.hasBinaryFilePath();
    }

    /**
     * @see {@link SoftwareSimulator#hasBinaryArguments()}
     */
    public boolean hasBinaryArguments() {
        return this.simulator.hasBinaryArguments();
    }

    /**
     * @see {@link SoftwareSimulator#getBinaryFilePath()}
     */
    public String getBinaryFilePath() {
        return this.simulator.getBinaryFilePath();
    }

    /**
     * @see {@link SoftwareSimulator#getBinaryArguments()}
     */
    public JSONArray getBinaryArguments() {
        return this.simulator.getBinaryArguments();
    }

    /**
     * @see {@link SoftwareSimulator#addBinaryExecutionStats(Number, JSONObject)}
     */
    public void addBinaryExecutionStats(Long time, JSONObject binaryExecutionStats) {
        this.simulator.addBinaryExecutionStats(time, binaryExecutionStats);
    }

    /**
     * @see {@link SoftwareSimulator#getExecutionStats()}
     */
    public Map<Number, Collection<JSONObject>> getExecutionStats() {
        return this.simulator.getExecutionStats();
    }

    /**
     * Schedules the head element of {@link #transitionChain} with
     * {@link #scheduleNextTransitionEvent()} and runs it.
     * 
     * @param input The input of the next transition
     * @param time The time of the next transition
     */
    public void step(Character input, Number time) {
        // ToDo: Try using synchronized keyword and removing the empty while-loops
        while (this.isTransitionEventRunning) {

        }
        if (!this.isSimulationTerminated()) {
            this.await();

            // Wait for the simulation to start, if not
            while (!this.hasSimulationBegun) {

            }

            System.out.println("SWSimulator simulation time = " + this.time());
            TransitionEvent scheduledEvent = this.scheduleNextTransitionEvent(input, time);

            try {
                System.out.println("Switching to event");
                this.isTransitionEventRunning = true;
                this.reactivateAfter(scheduledEvent);

                // Wait for the transition event to terminate
                while (this.isTransitionEventRunning) {

                }
                System.out.println("Switching to controller");
            } catch (SimulationException | RestartException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSimulationRunning() {
        return this.isSimulationRunning;
    }

    public boolean isSimulationTerminated() {
        return this.isSimulationTerminated;
    }

    /**
     * Performs the transition the given input dictates. This method is meant to be called from
     * {@link TransitionEvent} instances. This lets this method to be called concurrently, while
     * {@link #run()} is being executed.
     * 
     * @param input The char, which will be passed to {@link #simulator}
     */
    protected void triggerTransitionEvent(char input) {
        this.simulator.performTransition(input);
        System.out.println("Transition event performed");
    }

    /**
     * This class represents an event, which will result in a call to
     * {@link SoftwareSimulationController#triggerTransitionEvent(char)}
     */
    protected class TransitionEvent extends SimulationProcess {
        private char input;

        public TransitionEvent(char input) {
            System.out.println("Creating transition event");
            this.input = input;
        }

        /**
         * The implementation of {@link SimulationProcess#run().
         * 
         * Calls {@link SoftwareSimulationController#triggerTransitionEvent(char)} using
         * {@link #input}
         */
        public void run() {
            System.out.println("TransitionEvent running: time=" + this.time() + ", input=" + this.input);
            triggerTransitionEvent(this.input);
            System.out.println("TransitionEvent terminating");
            this.terminate();
            isTransitionEventRunning = false;
        }
    }
}
