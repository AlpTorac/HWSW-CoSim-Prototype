package hwswcosim.swsim;

import java.util.concurrent.Callable;

public interface IEvaluationObject {
    public EvaluationMeasurementCollector getCollector();
    public String getFullMethodName(String methodName);

    public default <T> T addTimeMeasurement(String methodName, Callable<T> method) throws Exception {
        return this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), method);
    }
    public default void addTimeMeasurement(String methodName, Runnable method) {
        this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), method);
    }
}
