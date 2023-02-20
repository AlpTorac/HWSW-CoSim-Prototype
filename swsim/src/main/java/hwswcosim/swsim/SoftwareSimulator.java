package hwswcosim.swsim;

import org.json.simple.JSONArray;

public class SoftwareSimulator {

    private DFAWrapper model;

    public SoftwareSimulator() {
        
    }

    public void addDFAWrapper(String DFAFilePath, String binaryMapFilePath) {
        this.model = this.parseDFAWrapper(DFAFilePath, binaryMapFilePath);
    }

    public DFAWrapper getDFAWrapper() {
        return this.model;
    }

    public void performTransition(char input) {
        if (this.model != null) {
            this.model.transition(input);
        }
    }

    public void addBinaryExecutionStats(Object binaryExecutionStats) {
        System.out.println("SWSimulator received binaryExecutionStats " + binaryExecutionStats.toString());
    }

    public boolean hasBinaryFilePath() {
        return this.model.hasBinaryFilePath();
    }

    public String getBinaryFilePath() {
        return this.model.getCurrentBinaryPath();
    }

    public boolean hasBinaryArguments() {
        return this.model.hasBinaryArguments();
    }

    public JSONArray getBinaryArguments() {
        return this.model.getCurrentBinaryArguments();
    }

    protected DFAWrapper parseDFAWrapper(String DFAFilePath, String binaryMapFilePath) {
        return new DFAWrapperParser().parseDFAWrapper(DFAFilePath, binaryMapFilePath);
    }
}
