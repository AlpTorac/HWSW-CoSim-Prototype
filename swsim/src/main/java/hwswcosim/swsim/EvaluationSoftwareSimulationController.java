package hwswcosim.swsim;

public class EvaluationSoftwareSimulationController extends SoftwareSimulationController implements IEvaluationObject {
    private EvaluationMeasurementCollector collector;

    public EvaluationSoftwareSimulationController() {
        super();
        this.collector = EvaluationMeasurementCollector.getInstance();
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
