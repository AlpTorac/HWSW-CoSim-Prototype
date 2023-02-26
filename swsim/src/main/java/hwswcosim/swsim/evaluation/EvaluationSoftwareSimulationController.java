package hwswcosim.swsim.evaluation;

import java.util.Collection;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import hwswcosim.swsim.ScriptedTransitionEntry;
import hwswcosim.swsim.SoftwareSimulationController;

public class EvaluationSoftwareSimulationController extends SoftwareSimulationController implements IEvaluationObject {
    public EvaluationSoftwareSimulationController() {
        super();
    }

    @Override
    public boolean hasBinaryFilePath() {
        return this.addTimeMeasurement("hasBinaryFilePath",
        (r)->super.hasBinaryFilePath());
    }

    @Override
    public boolean hasBinaryArguments() {
        return this.addTimeMeasurement("hasBinaryArguments",
        (r)->super.hasBinaryArguments());
    }

    @Override
    public String getBinaryFilePath() {
        return this.addTimeMeasurement("getBinaryFilePath",
        (r)->super.getBinaryFilePath());
    }

    @Override
    public JSONArray getBinaryArguments() {
        return this.addTimeMeasurement("getBinaryArguments",
        (r)->super.getBinaryArguments());
    }

    @Override
    public void addBinaryExecutionStats(Long time, JSONObject binaryExecutionStats) {
        this.addTimeMeasurement("addBinaryExecutionStats", ()->super.addBinaryExecutionStats(time, binaryExecutionStats));
    }

    @Override
    public Map<Number, JSONObject> getExecutionStats() {
        return this.addTimeMeasurement("getExecutionStats",
        (r)->super.getExecutionStats());
    }

    @Override
    public void initSoftwareSimulation(String DFAFilePath, String binaryMapFilePath, String transitionChainFilePath) {
        this.addTimeMeasurement("initSoftwareSimulation", 
        ()->super.initSoftwareSimulation(DFAFilePath, binaryMapFilePath, transitionChainFilePath));
    }

    @Override
    protected void initSoftwareSimulator() {
        this.addTimeMeasurement("initSoftwareSimulator", ()->super.initSoftwareSimulator());
    }

    @Override
    protected void initModel(String DFAFilePath, String binaryMapFilePath) {
        this.addTimeMeasurement("initModel", ()->super.initModel(DFAFilePath, binaryMapFilePath));
    }

    @Override
    protected void initTransitionChain(String transitionChainFilePath) {
        this.addTimeMeasurement("initTransitionChain", ()->super.initTransitionChain(transitionChainFilePath));
    }

    @Override
    protected TransitionEvent scheduleNextTransitionEvent() {
        return this.addTimeMeasurement("scheduleNextTransitionEvent",
        (r)->super.scheduleNextTransitionEvent());
    }

    @Override
    public void await() {
        this.addTimeMeasurement("await", ()->super.await());
    }

    @Override
    public void exit() {
        this.addTimeMeasurement("exit", ()->super.exit());
    }

    @Override
    public boolean hasUnscheduledTransitionEvents() {
        return this.addTimeMeasurement("hasUnscheduledTransitionEvents",
        (r)->super.hasUnscheduledTransitionEvents());
    }

    @Override
    public Number getNextEventTime() {
        return this.addTimeMeasurement("getNextEventTime",
        (r)->super.getNextEventTime());
    }

    @Override
    public Collection<ScriptedTransitionEntry> getRemainingTransitionChain() {
        return this.addTimeMeasurement("getRemainingTransitionChain",
        (r)->super.getRemainingTransitionChain());
    }

    @Override
    protected void triggerTransitionEvent(char input) {
        this.addTimeMeasurement("triggerTransitionEvent", ()->super.triggerTransitionEvent(input)); 
    }

    @Override
    public void run() {
        this.addTimeMeasurement("run", ()->super.run());
    }

    @Override
    public void step() {
        this.addTimeMeasurement("step", ()->super.step());
    }

    @Override
    public String getFullMethodName(String methodName) {
        return "EvaluationSoftwareSimulationController." + methodName;
    }
}
