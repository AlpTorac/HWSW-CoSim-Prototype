package hwswcosim.swsim;

import java.util.List;
import java.util.Map;

import de.offis.mosaik.api.SimProcess;
import de.offis.mosaik.api.Simulator;

public class EvaluationSoftwareSimulatorMosaikAPI extends SoftwareSimulatorMosaikAPI implements IEvaluationObject {

    private static final String simulatorName = "EvaluationSoftwareSimulator";
    private EvaluationMeasurementCollector collector;

    public static void main(String[] args) throws Throwable {
        Simulator sim = new EvaluationSoftwareSimulatorMosaikAPI(simulatorName);
        SimProcess.startSimulation(args, sim);
    }

    @Override
    public EvaluationMeasurementCollector getCollector() {
        return this.collector;
    }

    @Override
    public String getFullMethodName(String methodName) {
        return this.getSimName() + "." + methodName;
    }

    public EvaluationSoftwareSimulatorMosaikAPI(String simulatorName) {
        super(simulatorName);
        this.collector = EvaluationMeasurementCollector.getInstance();
    }

    @Override
    protected EvaluationSoftwareSimulationController initSoftwareSimulationController() {
        return new EvaluationSoftwareSimulationController();
    }

    @Override
    public List<Map<String, Object>> create(int num, String model, Map<String, Object> modelParams) throws Exception {
        return this.addTimeMeasurement("create", ()->super.create(num, model, modelParams));
    }

    @Override
    public Map<String, Object> getData(Map<String, List<String>> outputs) throws Exception {
        return this.addTimeMeasurement("getData", ()->super.getData(outputs));
    }

    @Override
    public Map<String, Object> init(String sid, Float timeResolution, Map<String, Object> simParams) throws Exception {
        return this.addTimeMeasurement("init", ()->super.init(sid, timeResolution, simParams));
    }

    @Override
    public Long step(long time, Map<String, Object> inputs, long maxAdvance) throws Exception {
        return this.addTimeMeasurement("step", ()->super.step(time, inputs, maxAdvance));
    }

    protected void writeEvaluationResults() {
        if (this.softwareSimulatorOutputManager != null) {
            this.softwareSimulatorOutputManager.writeOutputMapToFile(
                this.collector.reduceTimeMeasurements(), "swsimEvalOutput.txt");
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.writeEvaluationResults();
    }
}
