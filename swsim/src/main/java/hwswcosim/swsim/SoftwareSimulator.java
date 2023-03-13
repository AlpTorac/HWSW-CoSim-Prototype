package hwswcosim.swsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This class contains the software model in form of {@link DFAWrapper} and provides
 * access to some of its methods that have to be accessed.
 */
public class SoftwareSimulator {

    private DFAWrapper model;
    /**
     * A map that contains all collected statistics given to this instance at
     * each time point, where a transition found place, via
     * {@link #addBinaryExecutionStats(Number, JSONObject)}. The said statistics
     * are all stored in form of a JSONObject, whose keys are the names of each statistic
     * and values are the values of the statistic with the matching name.
     */
    private Map<Number, Collection<JSONObject>> binaryExecutionStats;

    private DFAWrapperParser dfaWrapperParser;

    public SoftwareSimulator() {
        this.binaryExecutionStats = new HashMap<Number, Collection<JSONObject>>();
        this.dfaWrapperParser = new DFAWrapperParser(new BinaryMapParser(new DFAParser()));
    }

    public DFAWrapperParser getDFAWrapperParser() {
        return this.dfaWrapperParser;
    }

    /**
     * Initialises {@link #model}.
     * 
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside
     * @param DFAFileName The name of the file that describes the DFA
     * @param binaryMapFileName The name of the file that contains all {@link BinaryMapEntry} information
     */
    public void addDFAWrapper(String resourceFolderPath, String DFAFileName, String binaryMapFileName) {
        this.model = this.parseDFAWrapper(resourceFolderPath, DFAFileName, binaryMapFileName);
    }

    public DFAWrapper getDFAWrapper() {
        return this.model;
    }

    /**
     * Passes the given input char to {@link #model}.
     * 
     * @see {@link DFAWrapper#transition(char)}
     */
    public void performTransition(char input) {
        if (this.model != null) {
            this.model.transition(input);
        }
    }

    public Map<Number, Collection<JSONObject>> getExecutionStats() {
        return this.binaryExecutionStats;
    }

    /**
     * Iterates through values of {@link #binaryExecutionStats}, finds
     * all entries with key = statName and gathers their value in a
     * collection instance.
     * 
     * @param statName A given statistic name
     * @return A collection of all values from the statistic "statName"
     * contained in values of {@link #binaryExecutionStats}.
     */
    public Collection<Object> getExecutionStatValues(String statName) {
        ArrayList<Object> result = new ArrayList<Object>();

        for (Collection<JSONObject> binaryExecutionStatsEntry : this.binaryExecutionStats.values()) {
            for (JSONObject executionStatsObject : binaryExecutionStatsEntry) {
                if (executionStatsObject.containsKey(statName)) {
                    result.add(executionStatsObject.get(statName));
                }
            }
        }

        return result;
    }

    /**
     * Inserts the given binaryExecutionStats with key = time
     * into {@link #binaryExecutionStats}.
     * 
     * @param time The time point, when binaryExecutionStats is generated
     * @param binaryExecutionStats Statistics given to this instance
     */
    public void addBinaryExecutionStats(Number time, JSONObject binaryExecutionStats) {
        if (!this.binaryExecutionStats.containsKey(time)) {
            this.binaryExecutionStats.put(time, new ArrayList<JSONObject>());
        }

        Collection<JSONObject> statsCollection = this.binaryExecutionStats.get(time);
        statsCollection.add(binaryExecutionStats);
        
        //System.out.println("SWSimulator received binaryExecutionStats:\n" + binaryExecutionStats);
    }

    /**
     * @see {@link DFAWrapper#hasBinaryFilePath()}
     */
    public boolean hasBinaryFilePath() {
        return this.model.hasBinaryFilePath();
    }

    /**
     * @see {@link DFAWrapper#getCurrentBinaryPath()}
     */
    public String getBinaryFilePath() {
        return this.model.getCurrentBinaryPath();
    }

    /**
     * @see {@link DFAWrapper#hasBinaryArguments()}
     */
    public boolean hasBinaryArguments() {
        return this.model.hasBinaryArguments();
    }

    /**
     * @see {@link DFAWrapper#getCurrentBinaryArguments()}
     */
    public JSONArray getBinaryArguments() {
        return this.model.getCurrentBinaryArguments();
    }

    /**
     * Parses and returns the {@link DFAWrapper} from the given parameters.
     * 
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside
     * @param DFAFileName The name of the file that describes the DFA
     * @param binaryMapFileName The name of the file that contains all {@link BinaryMapEntry} information
     * @return The {@link DFAWrapper} instance parsed by {@link #dfaWrapperParser} from the given parameters.
     */
    protected DFAWrapper parseDFAWrapper(String resourceFolderPath, String DFAFileName, String binaryMapFileName) {
        return this.getDFAWrapperParser().parseDFAWrapper(resourceFolderPath, DFAFileName, binaryMapFileName);
    }
}
