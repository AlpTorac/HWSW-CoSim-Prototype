package hwswcosim.swsim.evaluation;

import java.util.List;
import java.util.Map;

import de.offis.mosaik.api.SimProcess;
import de.offis.mosaik.api.Simulator;
import hwswcosim.swsim.SoftwareSimulatorMosaikAPI;

public class EvaluationSoftwareSimulatorMosaikAPI extends SoftwareSimulatorMosaikAPI implements IEvaluationObject {

    private static final String softwareSimulatorEvalOutputFilePathName = "software_simulator_eval_output_file_path";
    private static final String simulatorName = "EvaluationSoftwareSimulator";

    private long startTime;
    private long endTime;

    private String evalOutputFilePath;

    public static void main(String[] args) throws Throwable {
        Simulator sim = new EvaluationSoftwareSimulatorMosaikAPI(simulatorName);
        SimProcess.startSimulation(args, sim);
    }

    @Override
    public String getFullMethodName(String methodName) {
        return this.getSimName() + "." + methodName;
    }

    public EvaluationSoftwareSimulatorMosaikAPI(String simulatorName) {
        super(simulatorName);
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
        if (simParams.containsKey(softwareSimulatorEvalOutputFilePathName)) {
            this.evalOutputFilePath = (String) simParams.get(softwareSimulatorEvalOutputFilePathName);
        }

        this.startTime = this.getCurrentSystemTime();
        return this.addTimeMeasurement("init", ()->super.init(sid, timeResolution, simParams));
    }

    @Override
    public Long step(long time, Map<String, Object> inputs, long maxAdvance) throws Exception {
        return this.addTimeMeasurement("step", ()->super.step(time, inputs, maxAdvance));
    }

    protected void writeEvaluationResults() {
        if (this.softwareSimulatorOutputManager != null && this.evalOutputFilePath != null) {
            this.softwareSimulatorOutputManager.writeOutputMapToFile(
                this.getCollector().reduceTimeMeasurements(), this.evalOutputFilePath,
                "Software simulator time measurements:\n",
                "\nSoftware simulator ran for: "
                + (this.endTime - this.startTime) + "\n");
        }
    }

    @Override
    public void cleanup() {
        this.addTimeMeasurement("cleanup", ()->super.cleanup());
        this.endTime = this.getCurrentSystemTime();
        this.writeEvaluationResults();
    }
}
