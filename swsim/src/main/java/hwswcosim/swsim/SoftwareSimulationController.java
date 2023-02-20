package hwswcosim.swsim;

import java.util.Collection;

import org.javasim.RestartException;
import org.javasim.Simulation;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.json.simple.JSONArray;

public class SoftwareSimulationController extends SimulationProcess {
    Collection<ScriptedTransitionEntry> transitionChain;
    private SoftwareSimulator simulator;

    private TransitionChainParser transitionChainParser;

    private volatile boolean isSimulationTerminated;
    private volatile boolean isSimulationRunning;

    private volatile boolean transitionEventRunning;

    public SoftwareSimulationController() {
        this.isSimulationTerminated = false;
        this.isSimulationRunning = false;
        this.transitionEventRunning = false;
        this.transitionChainParser = new TransitionChainParser();
    }

    public void initSoftwareSimulation(String DFAFilePath, String binaryMapFilePath, String transitionChainFilePath) {
        this.initSoftwareSimulator();
        this.initModel(DFAFilePath, binaryMapFilePath);
        this.initTransitionChain(transitionChainFilePath);
    }

    protected void initSoftwareSimulator() {
        this.simulator = new SoftwareSimulator();
    }

    protected void initModel(String DFAFilePath, String binaryMapFilePath) {
        this.simulator.addDFAWrapper(DFAFilePath, binaryMapFilePath);
    }

    protected void initTransitionChain(String transitionChainFilePath) {
        this.transitionChain = this.transitionChainParser.parseTransitionChain(transitionChainFilePath);
    }

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
        System.out.println("Next event scheduled at time: " + activationTime.doubleValue());
        return event;
    }

    @Override
    public void run() {
        System.out.println("SWSimulator started");
        Simulation.start();
        System.out.println("SWSimulation running");

        while (this.isSimulationRunning()) {
            
        }

        Simulation.stop();
        System.out.println("SWSimulator stopped");
        this.exit();
    }

    public void await() {
        this.resumeProcess();
    }

    public void exit() {
        System.out.println("Exiting simulation");
        this.isSimulationTerminated = true;
        System.out.println("Exited simulation");
    }

    public boolean hasBinaryFilePath() {
        return this.simulator.hasBinaryFilePath();
    }

    public String getBinaryFilePath() {
        return this.simulator.getBinaryFilePath();
    }

    public boolean hasBinaryArguments() {
        return this.simulator.hasBinaryArguments();
    }

    public JSONArray getBinaryArguments() {
        return this.simulator.getBinaryArguments();
    }

    public void addBinaryExecutionStats(String binaryExecutionStatsText) {
        this.simulator.addBinaryExecutionStats(binaryExecutionStatsText);
    }

    public boolean hasUnscheduledTransitionEvents() {
        return !this.transitionChain.isEmpty();
    }

    public void step() {
        if (!this.isSimulationTerminated()) {
            this.await();

            if (this.hasUnscheduledTransitionEvents()) {
                this.isSimulationRunning = true;
                System.out.println("SWSimulator simulation time = " + this.time());
                TransitionEvent scheduledEvent = this.scheduleNextTransitionEvent();

                try {
                    System.out.println("Switching to event");
                    this.transitionEventRunning = true;
                    this.reactivateAfter(scheduledEvent);
                    while (this.transitionEventRunning) {

                    }
                    System.out.println("Switching to controller");
                } catch (SimulationException | RestartException e) {
                    e.printStackTrace();
                }
            } else {
                this.isSimulationRunning = false;
            }
        }
    }

    public boolean isSimulationRunning() {
        return this.isSimulationRunning;
    }

    public boolean isSimulationTerminated() {
        return this.isSimulationTerminated;
    }

    public Number getNextEventTime() {
        if (this.hasUnscheduledTransitionEvents()) {
            return this.transitionChain.stream().findFirst().get().getTime();
        }
        return null;
    }

    private void triggerTransitionEvent(char input) {
        this.simulator.performTransition(input);
        System.out.println("Transition event performed");
    }

    protected class TransitionEvent extends SimulationProcess {
        private char input;

        public TransitionEvent(char input) {
            System.out.println("Creating transition event");
            this.input = input;
        }

        public void run() {
            System.out.println("TransitionEvent running: time=" + this.time() + ", input=" + this.input);
            triggerTransitionEvent(this.input);
            System.out.println("TransitionEvent terminating");
            this.terminate();
            transitionEventRunning = false;
        }
    }
}
