package hwswcosim.swsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.javasim.RestartException;
import org.javasim.Simulation;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This class extends {@link SimulationProcess} from the JavaSim library
 * and manages the software simulation by acting as its controller.
 * 
 * The classes in JavaSim library are coded in a way, which does not
 * allow pausing the simulation (temporarily stopping it without having to
 * reset its time). Therefore, at any given time between the start of the
 * simulation (Simulation.start()) and its end (Simulation.end()), there
 * will always be at least one SimulationProcess running. To pause the
 * simulation without changing its time, one needs to code empty while-loops
 * to force the currently active SimulationProcess (SimulationProcess.current())
 * to wait and let the threads outside (threads that are not relevant to the
 * simulation) do their work without causing race conditions,
 * hence the empty while-loops in this class' implementation.
 */
public class SoftwareSimulationController extends SimulationProcess {
    /**
     * A list of transitions given by an input character and a time point
     * 
     * @see {@link ScriptedTransitionEntry}
     */
    private Collection<ScriptedTransitionEntry> transitionChain;
    private SoftwareSimulator simulator;

    private TransitionChainParser transitionChainParser;

    private volatile boolean isSimulationTerminated;
    private volatile boolean isSimulationRunning;
    private volatile boolean hasSimulationBegun;
    private volatile boolean isTransitionEventRunning;

    public SoftwareSimulationController() {
        this.hasSimulationBegun = false;
        this.isSimulationTerminated = false;
        this.isSimulationRunning = false;
        this.isTransitionEventRunning = false;
        this.transitionChainParser = new TransitionChainParser();
    }

    /**
     * Initialises the software simulation by instantiating the relevant classes.
     * 
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside
     * @param DFAFileName The name of the file that describes the DFA
     * @param binaryMapFileName The name of the file that contains all {@link BinaryMapEntry} information
     * @param transitionChainFileName The name of the file that contains all {@link ScriptedTransitionEntry} information
     */
    public void initSoftwareSimulation(String resourceFolderPath, String DFAFileName, String binaryMapFileName, String transitionChainFileName) {
        this.initSoftwareSimulator();
        this.initModel(resourceFolderPath, DFAFileName, binaryMapFileName);
        this.initTransitionChain(resourceFolderPath, transitionChainFileName);
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
     * See {@link TransitionChainParser#parseTransitionChain(String, String)}
     * 
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside
     * @param transitionChainFileName The name of the file that contains all {@link ScriptedTransitionEntry} information
     */
    protected void initTransitionChain(String resourceFolderPath, String transitionChainFileName) {
        this.transitionChain = this.transitionChainParser.parseTransitionChain(resourceFolderPath, transitionChainFileName);
    }

    /**
     * Creates and schedules the next transition from {@link #transitionChain}.
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
        //System.out.println("Next event scheduled at time: " + time.doubleValue());
        return event;
    }

        /**
     * Creates and schedules the next transition from {@link #transitionChain}.
     * 
     * @return The created {@link TransitionEvent} instance
     */
    protected TransitionEvent scheduleNextTransitionEvent() {
        ScriptedTransitionEntry ste = this.transitionChain.stream().findFirst().get();
        Number activationTime = ste.getTime();
        TransitionEvent event = new TransitionEvent(ste.getInput().charValue());
        try {
            event.activateAt(activationTime.doubleValue());
        } catch (SimulationException | RestartException e) {
            e.printStackTrace();
        }
        this.transitionChain.remove(ste);
        //System.out.println("Next event scheduled at time: " + activationTime.doubleValue());
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
        //System.out.println("SWSimulator started");
        Simulation.start();
        //System.out.println("SWSimulation running");

        this.hasSimulationBegun = true;
        this.isSimulationRunning = true;
        while (this.isSimulationRunning()) {
            
        }

        Simulation.stop();
        //System.out.println("SWSimulator stopped");
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

        /* 
         * Wait for JavaSim to run this SoftwareSimulationController instance
         * by calling its run() method.
         * 
         * Removing this loop causes issues for the cases, where step()
         * is called before simulation starts in run() method
         * (with Simulation.start).
         */
        while (!this.hasSimulationBegun) {

        }
    }

    /**
     * Call to end the software simulation.
     */
    public void exit() {
        //System.out.println("Exiting simulation");
        this.isSimulationTerminated = true;
        //System.out.println("Exited simulation");
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
     * @return Whether {@link #transitionChain} has any elements.
     */
    public boolean hasUnscheduledTransitionEvents() {
        return !this.transitionChain.isEmpty();
    }

    /**
     * Schedules the head element of {@link #transitionChain} with
     * {@link #scheduleNextTransitionEvent()} and runs it.
     */
    public void step() {
        if (!this.isSimulationTerminated()) {
            this.await();

            if (this.hasUnscheduledTransitionEvents()) {
                //System.out.println("SWSimulator simulation time = " + this.time());
                this.executeScheduledEvent(this.scheduleNextTransitionEvent());
            } else {
                this.isSimulationRunning = false;
            }
        }
    }

    protected void executeScheduledEvent(TransitionEvent scheduledEvent) {
        /*
         * If there is currently a TransitionEvent running, wait for
         * it to finish before running another one.
         * 
         * Meant for the cases, where step() method is called before
         * the said TransitionEvent terminates.
         */
        while (this.isTransitionEventRunning) {

        }

        try {
            //System.out.println("Switching to event");
            this.isTransitionEventRunning = true;
            this.reactivateAfter(scheduledEvent);
        } catch (SimulationException | RestartException e) {
            e.printStackTrace();
        }

        /*
         * Wait for the scheduledEvent to terminate.
         */
        while (this.isTransitionEventRunning) {

        }
    }

    public boolean isSimulationRunning() {
        return this.isSimulationRunning;
    }

    public boolean isSimulationTerminated() {
        return this.isSimulationTerminated;
    }

    /**
     * @return The time, at which the current head element of {@link #transitionChain}
     * is to be scheduled, or null if there is no such element.
     */
    public Number getNextEventTime() {
        if (this.hasUnscheduledTransitionEvents()) {
            return this.transitionChain.stream().findFirst().get().getTime();
        }
        return null;
    }

    /**
     * @return A deep copy of {@link #transitionChain}.
     */
    public Collection<ScriptedTransitionEntry> getRemainingTransitionChain() {
        ArrayList<ScriptedTransitionEntry> remainingChain = new ArrayList<ScriptedTransitionEntry>();

        if (this.transitionChain != null) {
            for (ScriptedTransitionEntry ste : this.transitionChain) {
                remainingChain.add(ste.clone());
            }
        }

        return remainingChain;
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
        //System.out.println("Transition event performed");
    }

    /**
     * This class represents an event, which will result in a call to
     * {@link SoftwareSimulationController#triggerTransitionEvent(char)}
     */
    protected class TransitionEvent extends SimulationProcess {
        private char input;

        public TransitionEvent(char input) {
            //System.out.println("Creating transition event");
            this.input = input;
        }

        /**
         * The implementation of {@link SimulationProcess#run().
         * 
         * Calls {@link SoftwareSimulationController#triggerTransitionEvent(char)} using
         * {@link #input}
         */
        public void run() {
            //System.out.println("TransitionEvent running: time=" + this.time() + ", input=" + this.input);
            triggerTransitionEvent(this.input);
            //System.out.println("TransitionEvent terminating");
            this.terminate();
            isTransitionEventRunning = false;
        }
    }
}
