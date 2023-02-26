package hwswcosim.swsim.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class EvaluationMeasurementCollector implements IEvaluationObject {
    private static EvaluationMeasurementCollector instance;

    private Map<String, Collection<Number>> timeMeasurementCollector;

    private EvaluationMeasurementCollector() {
        this.timeMeasurementCollector = this.initTimeMeasurementCollector();
    }

    protected Map<String, Collection<Number>> initTimeMeasurementCollector() {
        return new HashMap<String, Collection<Number>>();
    }

    protected Collection<Number> initTimeMeasurementCollectorValue() {
        return new ArrayList<Number>();
    }

    protected void addTimeMeasurementEntry(String methodName) {
        this.timeMeasurementCollector.put(methodName, this.initTimeMeasurementCollectorValue());
    }

    @Override
    public void addTimeMeasurement(String methodName, Number measurement) {
        if (!this.timeMeasurementCollector.containsKey(methodName)) {
            Collection<Number> entry = this.initTimeMeasurementCollectorValue();
            entry.add(measurement);
            this.timeMeasurementCollector.put(methodName, entry);
        } else {
            this.timeMeasurementCollector.get(methodName).add(measurement);
        }
    }

    @Override
    public <T> T addTimeMeasurement(String methodName, Callable<T> method) throws Exception {
        long start = this.getCurrentSystemTime();
        T result = method.call();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement(methodName, start, end);
        return result;
    }

    @Override
    public <I,O> O addTimeMeasurement(String methodName, Function<I, O> functionToApply, I input) {
        long start = this.getCurrentSystemTime();
        O result = functionToApply.apply(input);
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement(methodName, start, end);
        return result;
    }

    protected Map<String, Number> reduceTimeMeasurements() {
        Map<String, Number> reducedTimeMeasurements = new HashMap<String, Number>();

        this.iterateTimeMeasurementEntries((entry) -> {
            reducedTimeMeasurements.put(entry.getKey(), entry.getValue().stream().reduce(
                (t1, t2) -> Long.valueOf(t1.longValue() + t2.longValue())
            ).get());
        });

        return reducedTimeMeasurements;
    }

    public void iterateTimeMeasurementEntries(Consumer<? super Map.Entry<String, Collection<Number>>> action) {
        this.timeMeasurementCollector.entrySet().stream().forEach(action);
    }

    public static EvaluationMeasurementCollector getInstance() {
        if (instance == null) {
            instance = new EvaluationMeasurementCollector();
        }

        return instance;
    }

    @Override
    public EvaluationMeasurementCollector getCollector() {
        return EvaluationMeasurementCollector.getInstance();
    }

    @Override
    public String getFullMethodName(String methodName) {
        return "EvaluationMeasurementCollector."+methodName;
    }
}
