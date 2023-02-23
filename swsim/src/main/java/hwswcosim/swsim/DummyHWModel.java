package hwswcosim.swsim;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class DummyHWModel {
    private String currentBinaryPath;
    
    @SuppressWarnings("unused")
    private JSONArray currentBinaryArguments;

    public DummyHWModel() {

    }

    public void setCurrentBinaryPath(String binaryPath) {
        this.currentBinaryPath = binaryPath;
        System.out.println("DummyHWModel binaryPath set to: " + this.currentBinaryPath);
    }

    public void setCurrentBinaryArguments(JSONArray binaryArguments) {
        this.currentBinaryArguments = binaryArguments;
        System.out.println("DummyHWModel binaryArguments set to: ");
        for (int i = 0; i < binaryArguments.size(); i++) {
            System.out.println("Argument " + i + " = " + binaryArguments.get(i));
        }
    }

    public JSONObject mockExecutionStats() {
        JSONObject result = (JSONObject) JSONValue.parse("{\""+this.currentBinaryPath+"\": \"stats\""+"}");
        this.currentBinaryPath = "";
        this.currentBinaryArguments = null;
        return result;
    }

    public boolean hasOutput() {
        return this.currentBinaryPath != null && !this.currentBinaryPath.isEmpty();
    }
}