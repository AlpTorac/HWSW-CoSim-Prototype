package hwswcosim.swsim;

import java.util.HashMap;

import org.javasim.SimulationProcess;

public class SoftwareSimulator extends SimulationProcess {

    private final HashMap<String, DFAWrapper> instances;

    public SoftwareSimulator() {
        this.instances = new HashMap<String, DFAWrapper>();
    }

    public void addDFAWrapper(String eid, String DFAFilePath, String binaryMapFilePath, String transitionChainFilePath) {
        this.instances.put(eid, this.parseDFAWrapper(DFAFilePath, binaryMapFilePath, transitionChainFilePath));
    }

    public DFAWrapper getDFAWrapper(String eid) {
        return this.instances.get(eid);
    }

    public void step(String eid, long time) {
        DFAWrapper wrapper = this.getDFAWrapper(eid);
        if (wrapper != null) {
            wrapper.step(time);
        }
    }

    public void stepAll(long time) {
        this.instances.values().forEach(wrapper -> wrapper.step(time));
    }

    public void addBinaryExecutionStats() {

    }

    protected DFAWrapper parseDFAWrapper(String DFAFilePath, String binaryMapFilePath, String transitionChainFilePath) {
        return new DFAWrapperParser().parseDFAWrapper(DFAFilePath, binaryMapFilePath, transitionChainFilePath);
    }
}
