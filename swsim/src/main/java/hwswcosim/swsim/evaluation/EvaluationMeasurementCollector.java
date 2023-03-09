package hwswcosim.swsim.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This is a singleton class, whose only instance gathers measurements
 * from various methods and stores them in a map ({@link #timeMeasurementCollector}).
 */
public class EvaluationMeasurementCollector implements IEvaluationObject {
    private static EvaluationMeasurementCollector instance;

    /**
     * The map that contains run time measurements of methods
     * (key = method name, value = measurement).
     */
    private Map<String, Collection<Number>> timeMeasurementCollector;

    private EvaluationMeasurementCollector() {
        this.timeMeasurementCollector = this.initTimeMeasurementCollector();
    }

    /**
     * @return A created map instance for {@link #timeMeasurementCollector}
     */
    protected Map<String, Collection<Number>> initTimeMeasurementCollector() {
        return new HashMap<String, Collection<Number>>();
    }

    /**
     * @return A created collection instance to be the value of an entry in
     * {@link #timeMeasurementCollector}. This instance will then be used to
     * collect all measurements of the corresponding key given to this class.
     */
    protected Collection<Number> initTimeMeasurementCollectorValue() {
        return new ArrayList<Number>();
    }

    /**
     * Creates an entry in the {@link #timeMeasurementCollector} with the
     * key "methodName".
     * 
     * @param methodName A given method name
     */
    protected void addTimeMeasurementEntry(String methodName) {
        this.timeMeasurementCollector.put(methodName, this.initTimeMeasurementCollectorValue());
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T addTimeMeasurement(String methodName, Callable<T> method) throws Exception {
        long start = this.getCurrentSystemTime();
        T result = method.call();
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement(methodName, start, end);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <I,O> O addTimeMeasurement(String methodName, Function<I, O> functionToApply, I input) {
        long start = this.getCurrentSystemTime();
        O result = functionToApply.apply(input);
        long end = this.getCurrentSystemTime();
        this.addTimeMeasurement(methodName, start, end);
        return result;
    }

    /**
     * @return A compacted version of {@link #timeMeasurementCollector}, where for
     * each key there is only one value. This value is the sum of all values for the
     * said key in {@link #timeMeasurementCollector}.
     */
    protected Map<String, Number> reduceTimeMeasurements() {
        Map<String, Number> reducedTimeMeasurements = new HashMap<String, Number>();

        this.iterateTimeMeasurementEntries((entry) -> {
            reducedTimeMeasurements.put(entry.getKey(), entry.getValue().stream().reduce(
                (t1, t2) -> Long.valueOf(t1.longValue() + t2.longValue())
            ).get());
        });

        return reducedTimeMeasurements;
    }

    /**
     * Applies the given "action" to each entry in {@link #timeMeasurementCollector}.
     * 
     * @param action A given action that can be applied to the entries of {@link #timeMeasurementCollector}.
     */
    public void iterateTimeMeasurementEntries(Consumer<? super Map.Entry<String, Collection<Number>>> action) {
        this.timeMeasurementCollector.entrySet().stream().forEach(action);
    }

    /**
     * @return The only instance of this class. Instantiate the class,
     * if it has no instances.
     */
    public static EvaluationMeasurementCollector getInstance() {
        if (instance == null) {
            instance = new EvaluationMeasurementCollector();
        }

        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EvaluationMeasurementCollector getCollector() {
        return EvaluationMeasurementCollector.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullMethodName(String methodName) {
        return "EvaluationMeasurementCollector."+methodName;
    }
}
