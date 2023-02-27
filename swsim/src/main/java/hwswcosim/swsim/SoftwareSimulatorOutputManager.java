package hwswcosim.swsim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
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
    private DecimalFormat decimalFormat;

    private String softwareSimulatorOutputDir;
    private String softwareSimulatorOutputFile;
    private JSONObject softwareSimulatorOutputDesc;

    public SoftwareSimulatorOutputManager(String softwareSimulatorOutputDir, String softwareSimulatorOutputFile, JSONObject softwareSimulatorOutput) {
        this.softwareSimulatorOutputDir = softwareSimulatorOutputDir;
        this.softwareSimulatorOutputFile = softwareSimulatorOutputFile;
        this.softwareSimulatorOutputDesc = softwareSimulatorOutput;
        this.decimalFormat = new DecimalFormat("0.#########");
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
                return this.formatEntryValue(this.addAction(stats));

            case "avg":
                return this.formatEntryValue(this.averageAction(stats));

            case "none":
                return stats.stream().findFirst().get();

            default:
                throw new IllegalArgumentException("Unknown output accumulation action: " + action);
        }
    }

    protected Number addAction(Collection<Object> stats) {
        return (Double) stats.stream().reduce((val1, val2) -> {
            return Double.valueOf(Double.valueOf(val1.toString()).doubleValue() 
            + Double.valueOf(val2.toString()).doubleValue());
        }).get();
    }

    protected Number averageAction(Collection<Object> stats) {
        return Double.valueOf(this.addAction(stats).doubleValue() / Double.valueOf(stats.size()).doubleValue());
    }

    protected String formatEntryValue(Number value) {
        return this.decimalFormat.format(value.doubleValue());
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

    protected String getFullOutputFilePath(String fileName) {
        return this.softwareSimulatorOutputDir+"/"+fileName;
    }

    protected File createOutputFileInOutputDir(String fileName) {
        return this.createOutputFile(this.getFullOutputFilePath(fileName));
    }

    protected File createOutputFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            File parentDir = file.getParentFile();

            if (parentDir != null) {
                parentDir.mkdirs();
            }

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    protected Writer getOutputWriter(File outputFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
     
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        return bw;
    }

    protected void writeOutputEntryToFile(Writer w, String outputName, String outputValue) {
        this.writeToFile(w, outputName + ": " + outputValue + "\n");
    }

    protected void writeToFile(Writer w, String textToWrite) {
        try {
            w.write(textToWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the accumulated output to a file after reducing it.
     */
    public void writeAccumulatedOutputToFileInOutputDir(Map<Number, JSONObject> accumulatedOutput, String fileName) {
        this.writeOutputMapToFileInOutputDir(this.prepareOutput(accumulatedOutput), fileName);
    }

    public void writeAccumulatedOutputToFileInOutputDir(Map<Number, JSONObject> accumulatedOutput) {
        this.writeOutputMapToFileInOutputDir(this.prepareOutput(accumulatedOutput), this.softwareSimulatorOutputFile);
    }

    public <T> void writeOutputMapToFileInOutputDir(Map<String, T> outputMap, String fileName) {
        this.writeOutputMapToFileInOutputDir(outputMap, fileName, null, null);
    }

    public <T> void writeOutputMapToFileInOutputDir(Map<String, T> outputMap, String fileName, String textToWriteAtStart, String textToWriteAtEnd) {
        this.writeOutputMapToFile(outputMap, this.getFullOutputFilePath(fileName), textToWriteAtStart, textToWriteAtEnd);
    }

    public <T> void writeOutputMapToFile(Map<String, T> outputMap, String filePath, String textToWriteAtStart, String textToWriteAtEnd) {
        File outputFile = this.createOutputFile(filePath);
        Writer w = this.getOutputWriter(outputFile);

        if (textToWriteAtStart != null) {
            this.writeToFile(w, textToWriteAtStart);
        }

        outputMap.forEach((outputName, outputValue)->{
            this.writeOutputEntryToFile(w, outputName, outputValue.toString());
        });

        if (textToWriteAtEnd != null) {
            this.writeToFile(w, textToWriteAtEnd);
        }

        try {
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
