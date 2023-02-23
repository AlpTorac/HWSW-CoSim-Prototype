package hwswcosim.swsim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

/**
 * This class encapsulates what and how the software simulator
 * outputs.
 */
public class SoftwareSimulatorOutputManager {
    private String softwareSimulatorOutputDir;
    private JSONObject softwareSimulatorOutputDesc;

    public SoftwareSimulatorOutputManager(String softwareSimulatorOutputDir, JSONObject softwareSimulatorOutput) {
        this.softwareSimulatorOutputDir = softwareSimulatorOutputDir;
        this.softwareSimulatorOutputDesc = softwareSimulatorOutput;
    }

    /**
     * Computes the value of a single output entry
     * 
     * @param stats A given collection of output values that have been gathered
     * throughout the simulation that belong to a specific output.
     * @param action A given accumulation action that will be used to compute
     * the corresponding output.
     * @return The final output value of the corresponding output entry of the
     * software simulator
     */
    protected Object computeOutputEntryValue(Collection<Object> stats, String action) {
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

    /**
     * @return A map that contains the names of the desired output entries as key
     * and their value as value.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> prepareOutput(Map<Number, JSONObject> accumulatedOutput) {
        Map<String, Object> result = new HashMap<String, Object>();

        this.softwareSimulatorOutputDesc.forEach((outputName, action) -> {
            Collection<Object> stats = new ArrayList<Object>();

            accumulatedOutput.values().forEach(json -> {
                stats.add(json.get((String) outputName));
            });

            result.put((String) outputName, this.computeOutputEntryValue(stats, (String) action));
        });

        return result;
    }

    /**
     * Writes the accumulated output to a file.
     */
    public void writeOutput(Map<Number, JSONObject> accumulatedOutput) {
        File outputDir = new File(this.softwareSimulatorOutputDir);
        File outputFile = new File(outputDir.getAbsolutePath()+"/swsimOutput.txt");

        try {
            outputDir.mkdirs();
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
     
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
     
        this.prepareOutput(accumulatedOutput).forEach((outputName, outputValue)->{
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
