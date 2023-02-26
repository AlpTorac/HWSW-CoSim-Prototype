package hwswcosim.swsim.evaluation;

import java.util.concurrent.Callable;
import java.util.function.Function;

public interface IEvaluationObject {
    public String getFullMethodName(String methodName);
    
    public default long getCurrentSystemTime() {
        return System.nanoTime();
    }
    
    public default EvaluationMeasurementCollector getCollector() {
        return EvaluationMeasurementCollector.getInstance();
    }
    public default <I,O> O addTimeMeasurement(String methodName, Function<I, O> functionToApply, I input) {
        return this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), functionToApply, input);
    }
    public default <I,O> O addTimeMeasurement(String methodName, Function<I, O> functionToApply) {
        return this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), functionToApply, null);
    }
    /*
     * Only use with methods that declare throwing Exception
     */
    public default <T> T addTimeMeasurement(String methodName, Callable<T> method) throws Exception {
        return this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), method);
    }
    public default void addTimeMeasurement(String methodName, Runnable method) {
        this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), (i)->{method.run();return i;}, null);
    }
    public default void addTimeMeasurement(String methodName, Number measurement) {
        this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), measurement);
    }
    public default void addTimeMeasurement(String methodName, long start, long end) {
        this.addTimeMeasurement(methodName, Long.valueOf(end - start));
    }
}
