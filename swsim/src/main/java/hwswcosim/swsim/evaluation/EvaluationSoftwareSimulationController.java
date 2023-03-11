package hwswcosim.swsim.evaluation;

import java.util.Collection;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import hwswcosim.swsim.ScriptedTransitionEntry;
import hwswcosim.swsim.SoftwareSimulationController;

public class EvaluationSoftwareSimulationController extends SoftwareSimulationController implements IEvaluationObject {
    /**
     * {@inheritDoc}
     */
    public EvaluationSoftwareSimulationController() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBinaryFilePath() {
        return this.addTimeMeasurement("hasBinaryFilePath",
        (r)->super.hasBinaryFilePath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBinaryArguments() {
        return this.addTimeMeasurement("hasBinaryArguments",
        (r)->super.hasBinaryArguments());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBinaryFilePath() {
        return this.addTimeMeasurement("getBinaryFilePath",
        (r)->super.getBinaryFilePath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONArray getBinaryArguments() {
        return this.addTimeMeasurement("getBinaryArguments",
        (r)->super.getBinaryArguments());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBinaryExecutionStats(Long time, JSONObject binaryExecutionStats) {
        this.addTimeMeasurement("addBinaryExecutionStats", ()->super.addBinaryExecutionStats(time, binaryExecutionStats));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Number, Collection<JSONObject>> getExecutionStats() {
        return this.addTimeMeasurement("getExecutionStats",
        (r)->super.getExecutionStats());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initSoftwareSimulation(String resourceFolderPath, 
        String DFAFilePath, String binaryMapFilePath, String transitionChainFilePath) {
        this.addTimeMeasurement("initSoftwareSimulation", 
        ()->super.initSoftwareSimulation(resourceFolderPath, DFAFilePath, binaryMapFilePath, transitionChainFilePath));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSoftwareSimulator() {
        this.addTimeMeasurement("initSoftwareSimulator", ()->super.initSoftwareSimulator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initModel(String resourceFolderPath, String DFAFilePath, String binaryMapFilePath) {
        this.addTimeMeasurement("initModel", ()->super.initModel(resourceFolderPath, DFAFilePath, binaryMapFilePath));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initTransitionChain(String resourceFolderPath, String transitionChainFilePath) {
        this.addTimeMeasurement("initTransitionChain", ()->super.initTransitionChain(resourceFolderPath, transitionChainFilePath));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TransitionEvent scheduleNextTransitionEvent() {
        return this.addTimeMeasurement("scheduleNextTransitionEvent",
        (r)->super.scheduleNextTransitionEvent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void await() {
        this.addTimeMeasurement("await", ()->super.await());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exit() {
        this.addTimeMeasurement("exit", ()->super.exit());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUnscheduledTransitionEvents() {
        return this.addTimeMeasurement("hasUnscheduledTransitionEvents",
        (r)->super.hasUnscheduledTransitionEvents());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number getNextEventTime() {
        return this.addTimeMeasurement("getNextEventTime",
        (r)->super.getNextEventTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ScriptedTransitionEntry> getRemainingTransitionChain() {
        return this.addTimeMeasurement("getRemainingTransitionChain",
        (r)->super.getRemainingTransitionChain());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void triggerTransitionEvent(char input) {
        this.addTimeMeasurement("triggerTransitionEvent", ()->super.triggerTransitionEvent(input)); 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        this.addTimeMeasurement("run", ()->super.run());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void step(Character input, Number time) {
        this.addTimeMeasurement("step", ()->super.step(input, time));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void step() {
        this.addTimeMeasurement("step", ()->super.step());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullMethodName(String methodName) {
        return "EvaluationSoftwareSimulationController." + methodName;
    }
}
