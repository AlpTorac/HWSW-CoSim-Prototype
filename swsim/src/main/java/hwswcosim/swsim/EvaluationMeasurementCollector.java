package hwswcosim.swsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class EvaluationMeasurementCollector {
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

    public void addTimeMeasurement(String methodName, Number measurement) {
        if (!this.timeMeasurementCollector.containsKey(methodName)) {
            Collection<Number> entry = this.initTimeMeasurementCollectorValue();
            entry.add(measurement);
            this.timeMeasurementCollector.put(methodName, entry);
        } else {
            this.timeMeasurementCollector.get(methodName).add(measurement);
        }
    }

    protected <T> T addTimeMeasurement(String methodName, Callable<T> method) throws Exception {
        long start = System.currentTimeMillis();
        T result = method.call();
        this.addTimeMeasurement(methodName, Long.valueOf(System.currentTimeMillis() - start));
        return result;
    }

    protected void addTimeMeasurement(String methodName, Runnable method) {
        long start = System.currentTimeMillis();
        method.run();
        this.addTimeMeasurement(methodName, Long.valueOf(System.currentTimeMillis() - start));
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
}
