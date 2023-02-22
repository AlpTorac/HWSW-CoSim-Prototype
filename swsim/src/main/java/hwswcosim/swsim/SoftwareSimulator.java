package hwswcosim.swsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SoftwareSimulator {

    private DFAWrapper model;
    private Map<Number, JSONObject> binaryExecutionStats;

    public SoftwareSimulator() {
        this.binaryExecutionStats = new HashMap<Number, JSONObject>();
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

    public Collection<Object> getExecutionStats(String statName) {
        ArrayList<Object> result = new ArrayList<Object>();

        for (JSONObject binaryExecutionStatsEntry : this.binaryExecutionStats.values()) {
            if (binaryExecutionStatsEntry.containsKey(statName)) {
                result.add(binaryExecutionStatsEntry.get(statName));
            }
        }

        return result;
    }

    public void addBinaryExecutionStats(Number time, JSONObject binaryExecutionStats) {
        this.binaryExecutionStats.put(time, binaryExecutionStats);
        System.out.println("SWSimulator received binaryExecutionStats:\n" + binaryExecutionStats);
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
