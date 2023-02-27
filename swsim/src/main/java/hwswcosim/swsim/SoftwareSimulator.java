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

    private DFAWrapperParser dfaWrapperParser;

    public SoftwareSimulator() {
        this.binaryExecutionStats = new HashMap<Number, JSONObject>();
        this.dfaWrapperParser = new DFAWrapperParser(new BinaryMapParser(new DFAParser()));
    }

    public DFAWrapperParser getDFAWrapperParser() {
        return this.dfaWrapperParser;
    }

    public void addDFAWrapper(String resourceFolderPath, String DFAFilePath, String binaryMapFilePath) {
        this.model = this.parseDFAWrapper(resourceFolderPath, DFAFilePath, binaryMapFilePath);
    }

    public DFAWrapper getDFAWrapper() {
        return this.model;
    }

    public void performTransition(char input) {
        if (this.model != null) {
            this.model.transition(input);
        }
    }

    public Map<Number, JSONObject> getExecutionStats() {
        return this.binaryExecutionStats;
    }

    public Collection<Object> getExecutionStatValues(String statName) {
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

    protected DFAWrapper parseDFAWrapper(String resourceFolderPath, String DFAFilePath, String binaryMapFilePath) {
        return this.getDFAWrapperParser().parseDFAWrapper(resourceFolderPath, DFAFilePath, binaryMapFilePath);
    }
}
