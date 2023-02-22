package hwswcosim.swsim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class SoftwareSimulatorOutputManager {
    private String softwareSimulatorOutputDir;
    private JSONObject softwareSimulatorOutput;
    private SoftwareSimulationController softwareSimulationController;

    public SoftwareSimulatorOutputManager(String softwareSimulatorOutputDir, JSONObject softwareSimulatorOutput, SoftwareSimulationController softwareSimulationController) {
        this.softwareSimulatorOutputDir = softwareSimulatorOutputDir;
        this.softwareSimulatorOutput = softwareSimulatorOutput;
        this.softwareSimulationController = softwareSimulationController;
    }

    protected Object prepareSoftwareSimulatorOutputValue(Collection<Object> stats, String action) {
        switch ((String) action) {
            case "add":
                return stats.stream().reduce((val1, val2) -> {
                    return String.valueOf((Double.valueOf((String) val1)).doubleValue() 
                    + (Double.valueOf((String) val2)).doubleValue());
                }).get();

            case "none":
                return stats.stream().findFirst().get();

            default:
                throw new IllegalArgumentException("Unknown output accumulation action: " + action);
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> prepareSoftwareSimulatorOutput() {
        Map<String, Object> result = new HashMap<String, Object>();

        this.softwareSimulatorOutput.forEach((outputName, action) -> {
            Collection<Object> stats = this.softwareSimulationController.getExecutionStats((String) outputName);
            result.put((String) outputName, this.prepareSoftwareSimulatorOutputValue(stats, (String) action));
        });

        return result;
    }

    public void writeOutput() {
        File dir = new File(this.softwareSimulatorOutputDir);
        File fout = new File(dir.getAbsolutePath()+"/swsimOutput.txt");

        try {
            dir.mkdirs();
            fout.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
     
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
     
        this.prepareSoftwareSimulatorOutput().forEach((outputName, outputValue)->{
            try {
                bw.write(outputName + ": " + outputValue);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
