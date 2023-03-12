package hwswcosim.swsim;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This class represents a dummy hardware model to test the software simulation
 * in conjunction with {@link DummyHWSimulator}.
 */
public class DummyHWModel {
    private String currentBinaryPath;
    
    @SuppressWarnings("unused")
    private JSONArray currentBinaryArguments;

    public DummyHWModel() {

    }

    public void setCurrentBinaryPath(String binaryPath) {
        this.currentBinaryPath = binaryPath;
        //System.out.println("DummyHWModel binaryPath set to: " + this.currentBinaryPath);
    }

    /**
     * Adds arguments provided to {@link #currentBinaryArguments}.
     * 
     * @param binaryArguments A given array of arguments
     */
    public void setCurrentBinaryArguments(JSONArray binaryArguments) {
        this.currentBinaryArguments = binaryArguments;
        //System.out.println("DummyHWModel binaryArguments set to: ");
        for (int i = 0; i < binaryArguments.size(); i++) {
            //System.out.println("Argument " + i + " = " + binaryArguments.get(i));
        }
    }

    /**
     * @return Mocked execution statistics in form of {@link JSONObject}
     */
    public JSONObject mockExecutionStats() {
        JSONObject result = (JSONObject) JSONValue.parse("{\"stats\": \""+this.currentBinaryPath+"\""+"}");
        this.currentBinaryPath = "";
        this.currentBinaryArguments = null;
        return result;
    }

    /**
     * @return True if this method has data to output.
     */
    public boolean hasOutput() {
        return this.currentBinaryPath != null && !this.currentBinaryPath.isEmpty();
    }
}