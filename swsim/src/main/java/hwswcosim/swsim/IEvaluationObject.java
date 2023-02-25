package hwswcosim.swsim;

import java.util.concurrent.Callable;

public interface IEvaluationObject {
    public EvaluationMeasurementCollector getCollector();
    public String getFullMethodName(String methodName);

    public default long getCurrentSystemTime() {
        return System.nanoTime();
    }

    public default <T> T addTimeMeasurement(String methodName, Callable<T> method) throws Exception {
        return this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), method);
    }
    public default void addTimeMeasurement(String methodName, Runnable method) {
        this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), method);
    }
    public default void addTimeMeasurement(String methodName, Number measurement) {
        this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), measurement);
    }
    public default void addTimeMeasurement(String methodName, long start, long end) {
        this.addTimeMeasurement(methodName, Long.valueOf(end - start));
    }
}
