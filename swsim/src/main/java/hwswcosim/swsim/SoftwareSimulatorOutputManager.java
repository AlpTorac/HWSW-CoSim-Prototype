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
 * outputs when {@link SoftwareSimulatorMosaikAPI#cleanup()} is called.
 */
public class SoftwareSimulatorOutputManager {
    private DecimalFormat decimalFormat;

    private String softwareSimulatorOutputDir;
    private String softwareSimulatorOutputFileName;
    private JSONObject softwareSimulatorOutputDesc;

    public SoftwareSimulatorOutputManager(String softwareSimulatorOutputDir, String softwareSimulatorOutputFileName, JSONObject softwareSimulatorOutput) {
        this.softwareSimulatorOutputDir = softwareSimulatorOutputDir;
        this.softwareSimulatorOutputFileName = softwareSimulatorOutputFileName;
        this.softwareSimulatorOutputDesc = softwareSimulatorOutput;
        this.decimalFormat = new DecimalFormat("0.#########");
    }

    /**
     * Computes the value of a single output entry.
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

    /**
     * Encapsulates how the given "stats" are to be added.
     * 
     * @param stats A collection of objects that contain elements, whose
     * toString() method returns a string that can be parsed to double
     * @return Sum of all elements in "stats"
     */
    protected Number addAction(Collection<Object> stats) {
        return (Double) stats.stream().reduce((val1, val2) -> {
            return Double.valueOf(Double.valueOf(val1.toString()).doubleValue() 
            + Double.valueOf(val2.toString()).doubleValue());
        }).get();
    }

    /**
     * Encapsulates how to calculate the average value of the given "stats".
     * 
     * @param stats A collection of objects that contain elements, whose
     * toString() method returns a string that can be parsed to double
     * @return Average value of all elements in "stats"
     */
    protected Number averageAction(Collection<Object> stats) {
        return Double.valueOf(this.addAction(stats).doubleValue() / Double.valueOf(stats.size()).doubleValue());
    }

    /**
     * @param value A given {@link Number} instance to format
     * @return Formatted String version of "value" with the format
     * defined with {@link #decimalFormat}.
     */
    protected String formatEntryValue(Number value) {
        return this.decimalFormat.format(value.doubleValue());
    }

    /**
     * @return A map that contains the names of the desired output entries as key
     * and their value as value.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> prepareOutput(Map<Number, Collection<JSONObject>> accumulatedOutput) {
        Map<String, Object> result = new HashMap<String, Object>();

        this.softwareSimulatorOutputDesc.forEach((outputName, action) -> {
            Collection<Object> stats = new ArrayList<Object>();

            accumulatedOutput.values().forEach(col -> {
                col.stream().forEach(json -> {
                    Object output = json.get((String) outputName);
                    if (output != null) {
                        stats.add(json.get((String) outputName));
                    }
                });
            });

            if (!stats.isEmpty()) {
                result.put((String) outputName, this.computeOutputEntryValue(stats, (String) action));
            }
        });
        
        return result;
    }

    /**
     * @return Path to "fileName" in {@link #softwareSimulatorOutputDir}
     */
    protected String getFullOutputFilePath(String fileName) {
        return this.softwareSimulatorOutputDir+"/"+fileName;
    }

    /**
     * @return Creates the file "fileName" in {@link #softwareSimulatorOutputDir}
     */
    protected File createOutputFileInOutputDir(String fileName) {
        return this.createOutputFile(this.getFullOutputFilePath(fileName));
    }

    /**
     * @param filePath The path to the file (including the name and the
     * extension of the file), which will be created.
     * @return Create a file with the given "filePath" and all currently
     * non-existent parent directories
     */
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

    /**
     * @return A {@link Writer} instance that will be used to write to
     * "outputFile".
     */
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

    /**
     * Writes the given "outputName" with the given "outputValue" using the
     * given {@link Writer} instance "w".
     */
    protected void writeOutputEntryToFile(Writer w, String outputName, String outputValue) {
        this.writeToFile(w, outputName + ": " + outputValue + "\n");
    }

    /**
     * Writes the given text "textToWrite" using a given {@link Writer} instance.
     */
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
    public void writeAccumulatedOutputToFileInOutputDir(Map<Number, Collection<JSONObject>> accumulatedOutput, String fileName) {
        this.writeOutputMapToFileInOutputDir(this.prepareOutput(accumulatedOutput), fileName);
    }

    /**
     * Writes the accumulated output to the file with the given name {@link #softwareSimulatorOutputFileName} after reducing it.
     */
    public void writeAccumulatedOutputToFileInOutputDir(Map<Number, Collection<JSONObject>> accumulatedOutput) {
        this.writeOutputMapToFileInOutputDir(this.prepareOutput(accumulatedOutput), this.softwareSimulatorOutputFileName);
    }

    /**
     * Writes the given map "outputMap" to a file after reducing it.
     */
    public <T> void writeOutputMapToFileInOutputDir(Map<String, T> outputMap, String fileName) {
        this.writeOutputMapToFileInOutputDir(outputMap, fileName, null, null);
    }

    /**
     * Writes the given map "outputMap" to a file after reducing it. Also writes "textToWriteAtStart" at the beginning of the file
     * and "textToWriteAtEnd" to the end of the file.
     */
    public <T> void writeOutputMapToFileInOutputDir(Map<String, T> outputMap, String fileName, String textToWriteAtStart, String textToWriteAtEnd) {
        this.writeOutputMapToFile(outputMap, this.getFullOutputFilePath(fileName), textToWriteAtStart, textToWriteAtEnd);
    }

    /**
     * Writes the given map "outputMap" to the file at the path "filePath" after reducing it. Also writes "textToWriteAtStart"
     * at the beginning of the file and "textToWriteAtEnd" to the end of the file.
     */
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
