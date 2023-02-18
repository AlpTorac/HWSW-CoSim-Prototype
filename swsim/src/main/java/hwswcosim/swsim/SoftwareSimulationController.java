package hwswcosim.swsim;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.javasim.RestartException;
import org.javasim.Simulation;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;

public class SoftwareSimulationController extends SimulationProcess {
    Collection<ScriptedTransitionEntry> transitionChain;
    private SoftwareSimulator simulator;

    private TransitionChainParser transitionChainParser;

    private WaitingProcess waitingProcess;

    private volatile boolean isSimulationRunning;

    public SoftwareSimulationController() {
        this.isSimulationRunning = true;
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

    protected void scheduleNextTransitionEvent() {
        ScriptedTransitionEntry ste = this.transitionChain.stream().findFirst().get();
        Number activationTime = ste.time;
        TransitionEvent event = new TransitionEvent(ste.input.charValue());
        try {
            event.activateAt(activationTime.doubleValue());
        } catch (SimulationException | RestartException e) {
            e.printStackTrace();
        }
        this.transitionChain.remove(ste);
        System.out.println("Next event scheduled at time: " + activationTime.doubleValue());

        this.waitingProcess = new WaitingProcess();

        try {
            this.waitingProcess.activateAfter(event);
            System.out.println("Wait scheduled: " + this.waitingProcess.evtime());
        } catch (NoSuchElementException | SimulationException | RestartException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Switching to event, controller resumes at: " + this.waitingProcess.evtime());
            this.suspendProcess();
            System.out.println("Switching to controller");
        } catch (RestartException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("SWSimulator started");
        Simulation.start();
        System.out.println("SWSimulation running");

        while (!this.transitionChain.isEmpty()) {
            System.out.println("SWSimulator simulation time = " + this.time());
            this.scheduleNextTransitionEvent();
        }

        Simulation.stop();
        System.out.println("SWSimulator stopped");
        this.exit();
    }

    public void await() {
        this.resumeProcess();
        SimulationProcess.mainSuspend();
    }

    public void exit() {
        System.out.println("Exiting simulation");
        this.isSimulationRunning = false;

        try {
            SimulationProcess.mainResume();
        } catch (SimulationException e) {
            e.printStackTrace();
        }

        System.out.println("Exited simulation");
    }

    public boolean hasBinaryFilePath() {
        return this.simulator.hasBinaryFilePath();
    }

    public String getBinaryFilePath() {
        return this.simulator.getBinaryFilePath();
    }

    public void addBinaryExecutionStats(String binaryExecutionStatsText) {
        this.simulator.addBinaryExecutionStats(binaryExecutionStatsText);
    }

    public void resumeController() {
        System.out.println("Resuming software simulation controller");
        if (this.waitingProcess != null && !this.waitingProcess.terminated()) {
            this.waitingProcess.terminate();
            this.waitingProcess = null;
            System.out.println("Waiting over");
        }
        this.await();
    }

    public boolean isSimulationRunning() {
        return this.isSimulationRunning;
    }

    private void triggerTransitionEvent(char input) {
        this.simulator.performTransition(input);
        System.out.println("Transition event performed");
    }

    protected class WaitingProcess extends SimulationProcess {
        public void run() {
            try {
                SimulationProcess.mainResume();
            } catch (SimulationException e) {
                e.printStackTrace();
            }

            System.out.println("Waiting ...");

            while (isSimulationRunning()) {
                
            }
        }
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
            System.out.println("TransitionEvent terminated");
        }
    }
}
