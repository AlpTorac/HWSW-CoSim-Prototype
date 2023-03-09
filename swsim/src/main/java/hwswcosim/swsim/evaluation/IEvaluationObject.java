package hwswcosim.swsim.evaluation;

import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * An interface meant to be implemented by classes that are to be used
 * to evaluate the actual software simulation classes.
 */
public interface IEvaluationObject {
    /**
     * @return The given method name in a uniquely identifiable way.
     */
    public String getFullMethodName(String methodName);
    
    /**
     * @return The current system time (in nanoseconds as default)
     */
    public default long getCurrentSystemTime() {
        return System.nanoTime();
    }
    
    /**
     * @return The object instance that is responsible for collecting
     * evaluation data from {@link IEvaluationObject} instances.
     */
    public default EvaluationMeasurementCollector getCollector() {
        return EvaluationMeasurementCollector.getInstance();
    }

    /**
     * Applies the given function to the given input and measures how
     * long the function call takes to finish.
     * 
     * @param <I> Type of the input
     * @param <O> Type of the output
     * @param methodName The name of the method, which is used in "functionToApply"
     * @param functionToApply The function that wraps the method, whose runtime will
     * be measured
     * @param input The input, which "functionToApply" takes
     * @return <code>functionToApply.apply(input)</code>
     */
    public default <I,O> O addTimeMeasurement(String methodName, Function<I, O> functionToApply, I input) {
        return this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), functionToApply, input);
    }

    /**
     * The version of {@link #addTimeMeasurement(String, Function, Object)} for methods
     * that take no parameters but still return. The purpose of this method is to avoid
     * unnecessary try-catch blocks, which falsify measured run times.
     */
    public default <I,O> O addTimeMeasurement(String methodName, Function<I, O> functionToApply) {
        return this.addTimeMeasurement(this.getFullMethodName(methodName), functionToApply, null);
    }

    /**
     * The version of {@link #addTimeMeasurement(String, Function, Object)} for methods
     * that take no parameters but still return. Use this method for measuring the run time
     * of methods that have a "throws" declaration.
     */
    public default <T> T addTimeMeasurement(String methodName, Callable<T> method) throws Exception {
        return this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), method);
    }

    /**
     * The version of {@link #addTimeMeasurement(String, Function, Object)} for methods
     * that take no parameters and return nothing.
     */
    public default void addTimeMeasurement(String methodName, Runnable method) {
        this.addTimeMeasurement(this.getFullMethodName(methodName), (i)->{method.run();return i;}, null);
    }

    /**
     * Adds the given "measurement" for the given "methodName".
     */
    public default void addTimeMeasurement(String methodName, Number measurement) {
        this.getCollector().addTimeMeasurement(this.getFullMethodName(methodName), measurement);
    }

    /**
     * Adds the measurement for the given "methodName" implied by the given
     * "start" and "end" parameters.
     */
    public default void addTimeMeasurement(String methodName, long start, long end) {
        this.addTimeMeasurement(methodName, Long.valueOf(end - start));
    }
}
