package hwswcosim.swsim.evaluation;

import java.util.List;
import java.util.Map;

import de.offis.mosaik.api.SimProcess;
import de.offis.mosaik.api.Simulator;
import hwswcosim.swsim.SoftwareSimulatorMosaikAPI;

/**
 * This class is derived from {@link SoftwareSimulatorMosaikAPI} to allow its methods to be evaluated.
 * 
 * @see {@link SoftwareSimulatorMosaikAPI} for more information.
 */
public class EvaluationSoftwareSimulatorMosaikAPI extends SoftwareSimulatorMosaikAPI implements IEvaluationObject {

    private static final String softwareSimulatorEvalOutputFilePathName = "software_simulator_eval_output_file_path";
    private static final String simulatorName = "EvaluationSoftwareSimulator";

    /**
     * The system time at the start of the simulation.
     */
    private long startTime;

    /**
     * The system time at the end of the simulation.
     */
    private long endTime;

    /**
     * The path to the file that will be created in {@link #cleanup()}.
     */
    private String evalOutputFilePath;

    /**
     * @see {@link SoftwareSimulatorMosaikAPI#main(String[])}
     */
    public static void main(String[] args) throws Throwable {
        Simulator sim = new EvaluationSoftwareSimulatorMosaikAPI(simulatorName);
        SimProcess.startSimulation(args, sim);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullMethodName(String methodName) {
        return this.getSimName() + "." + methodName;
    }

    /**
     * {@inheritDoc}
     */
    public EvaluationSoftwareSimulatorMosaikAPI(String simulatorName) {
        super(simulatorName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EvaluationSoftwareSimulationController initSoftwareSimulationController() {
        return new EvaluationSoftwareSimulationController();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> create(int num, String model, Map<String, Object> modelParams) throws Exception {
        return this.addTimeMeasurement("create", ()->super.create(num, model, modelParams));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getData(Map<String, List<String>> outputs) throws Exception {
        return this.addTimeMeasurement("getData", ()->super.getData(outputs));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> init(String sid, Float timeResolution, Map<String, Object> simParams) throws Exception {
        if (simParams.containsKey(softwareSimulatorEvalOutputFilePathName)) {
            this.evalOutputFilePath = (String) simParams.get(softwareSimulatorEvalOutputFilePathName);
        }

        this.startTime = this.getCurrentSystemTime();
        return this.addTimeMeasurement("init", ()->super.init(sid, timeResolution, simParams));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long step(long time, Map<String, Object> inputs, long maxAdvance) throws Exception {
        return this.addTimeMeasurement("step", ()->super.step(time, inputs, maxAdvance));
    }

    /**
     * A subroutine meant to be used by {@link #cleanup()}. Creates a file that contains
     * evaluation results.
     */
    protected void writeEvaluationResults() {
        if (this.softwareSimulatorOutputManager != null && this.evalOutputFilePath != null) {
            this.softwareSimulatorOutputManager.writeOutputMapToFile(
                this.getCollector().reduceTimeMeasurements(), this.evalOutputFilePath,
                "Software simulator time measurements:\n",
                "\nSoftware simulator ran for: "
                + (this.endTime - this.startTime) + "\n");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        this.addTimeMeasurement("cleanup", ()->super.cleanup());
        this.endTime = this.getCurrentSystemTime();
        this.writeEvaluationResults();
    }
}
