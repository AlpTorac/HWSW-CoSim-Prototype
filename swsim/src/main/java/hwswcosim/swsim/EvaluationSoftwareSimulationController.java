package hwswcosim.swsim;

import java.util.Collection;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class EvaluationSoftwareSimulationController extends SoftwareSimulationController implements IEvaluationObject {
    private EvaluationMeasurementCollector collector;

    public EvaluationSoftwareSimulationController() {
        super();
        this.collector = EvaluationMeasurementCollector.getInstance();
    }

    @Override
    public boolean hasBinaryFilePath() {
        long start = this.getCurrentSystemTime();
        boolean result = super.hasBinaryFilePath();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement("hasBinaryFilePath", start, end);
        return result;
    }

    @Override
    public boolean hasBinaryArguments() {
        long start = this.getCurrentSystemTime();
        boolean result = super.hasBinaryArguments();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement("hasBinaryArguments", start, end);
        return result;
    }

    @Override
    public String getBinaryFilePath() {
        long start = this.getCurrentSystemTime();
        String result = super.getBinaryFilePath();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement("getBinaryFilePath", start, end);
        return result;
    }

    @Override
    public JSONArray getBinaryArguments() {
        long start = this.getCurrentSystemTime();
        JSONArray result = super.getBinaryArguments();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement("getBinaryArguments", start, end);
        return result;
    }

    @Override
    public void addBinaryExecutionStats(Long time, JSONObject binaryExecutionStats) {
        this.addTimeMeasurement("addBinaryExecutionStats", ()->super.addBinaryExecutionStats(time, binaryExecutionStats));
    }

    @Override
    public Map<Number, JSONObject> getExecutionStats() {
        long start = this.getCurrentSystemTime();
        Map<Number, JSONObject> result = super.getExecutionStats();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement("getExecutionStats", start, end);
        return result;
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
        long start = this.getCurrentSystemTime();
        TransitionEvent result = super.scheduleNextTransitionEvent();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement("scheduleNextTransitionEvent", start, end);
        return result;
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
        long start = this.getCurrentSystemTime();
        boolean result = super.hasUnscheduledTransitionEvents();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement("hasUnscheduledTransitionEvents", start, end);
        return result;
    }

    @Override
    public Number getNextEventTime() {
        long start = this.getCurrentSystemTime();
        Number result = super.getNextEventTime();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement("getNextEventTime", start, end);
        return result;
    }

    @Override
    public Collection<ScriptedTransitionEntry> getRemainingTransitionChain() {
        long start = this.getCurrentSystemTime();
        Collection<ScriptedTransitionEntry> result = super.getRemainingTransitionChain();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement("getRemainingTransitionChain", start, end);
        return result;
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
    public EvaluationMeasurementCollector getCollector() {
        return this.collector;
    }

    @Override
    public String getFullMethodName(String methodName) {
        return "EvaluationSoftwareSimulationController." + methodName;
    }
}
