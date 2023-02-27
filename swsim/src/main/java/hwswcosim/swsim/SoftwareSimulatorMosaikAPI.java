package hwswcosim.swsim;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.offis.mosaik.api.SimProcess;
import de.offis.mosaik.api.Simulator;

public class SoftwareSimulatorMosaikAPI extends Simulator {

    private static final String simulatorName = "SoftwareSimulator";
    private static final String modelName = "DFAWrapper";

    private static final String DFAFileNameField = "dfa_file_name";
    private static final String binaryMapFileNameField = "transition_to_binary_map_file_name";
    private static final String transitionChainFileNameField = "transition_chain_file_name";
    private static final String resourceFolderPathField = "resource_folder_path";

    private static final String softwareSimulatorOutputDescField = "software_simulator_output_desc";
    private static final String softwareSimulatorOutputDirField = "software_simulator_output_dir";
    private static final String softwareSimulatorOutputFileField = "software_simulator_output_file_name";

    private static final String binaryPathInputField = "binary_file_path_in";
    private static final String binaryPathOutputField = "binary_file_path_out";
    private static final String binaryArgumentsInputField = "binary_file_arguments_in";
    private static final String binaryArgumentsOutputField = "binary_file_arguments_out";

    private static final String binaryExecutionStatsOutputField = "binary_execution_stats_out";
    private static final String binaryExecutionStatsInputField = "binary_execution_stats_in";

    private static final JSONObject meta = (JSONObject) JSONValue.parse(("{"
            + "    'api_version': '" + Simulator.API_VERSION + "',"
            + "    'type': 'time-based',"
            + "    'models': {"
            + "        "+"'"+modelName+"'"+": {" + "            'public': true,"
            + "            'params': ['"+resourceFolderPathField+"', '"+DFAFileNameField+"', '"+binaryMapFileNameField
            + "', '"+transitionChainFileNameField+"'],"
            + "            'attrs': ['"+binaryPathOutputField+"', '"+binaryExecutionStatsInputField
            + "', '"+binaryPathInputField+"', '"+binaryExecutionStatsOutputField
            + "', '"+binaryArgumentsInputField+"', '"+binaryArgumentsOutputField+"']"
            + "        }"
            + "    }" + "}").replace("'", "\""));

    public static void main(String[] args) throws Throwable {
        Simulator sim = new SoftwareSimulatorMosaikAPI(simulatorName);
        SimProcess.startSimulation(args, sim);
    }

    private SoftwareSimulationController softwareSimulationController;
    private String simulatorID;
    protected SoftwareSimulatorOutputManager softwareSimulatorOutputManager;

    public SoftwareSimulatorMosaikAPI(String simulatorName) {
        super(simulatorName);
        this.softwareSimulationController = this.initSoftwareSimulationController();
    }

    protected SoftwareSimulationController initSoftwareSimulationController() {
        return new SoftwareSimulationController();
    }

    public String getSimulatorID() {
        return this.simulatorID;
    }

    /*
     * Checks whether the software simulator currently has a binary
     * that needs to be simulated.
     */
    protected boolean hasOutput() {
        return this.softwareSimulationController.hasBinaryFilePath();
    }

    /*
     * Gets the path of the binary that is to be simulated.
     */
    protected String getBinaryPathOutput() {
        return this.softwareSimulationController.getBinaryFilePath();
    }


    /*
     * Gets the arguments, with which the binary will be run
     * for simulation.
     */
    protected JSONArray getBinaryArgumentsOutput() {
        return this.softwareSimulationController.getBinaryArguments();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> create(int num, String model, Map<String, Object> modelParams) throws Exception {
        JSONArray entities = new JSONArray();

        String eid = "SW_Model";

        if (modelParams.containsKey(resourceFolderPathField) 
        && modelParams.containsKey(DFAFileNameField) 
        && modelParams.containsKey(binaryMapFileNameField)
        && modelParams.containsKey(transitionChainFileNameField)) {
            String resourceFolderPath = (String) modelParams.get(resourceFolderPathField);
            String DFAFilePath = (String) modelParams.get(DFAFileNameField);
            String binaryMapFilePath = (String) modelParams.get(binaryMapFileNameField);
            String transitionChainFilePath = (String) modelParams.get(transitionChainFileNameField);

            this.softwareSimulationController.initSoftwareSimulation(resourceFolderPath, DFAFilePath, binaryMapFilePath, transitionChainFilePath);

            JSONObject entity = new JSONObject();
            entity.put("eid", eid);
            entity.put("type", model);
            entity.put("rel", new JSONArray());
            entities.add(entity);

            System.out.println("sw_model created");
        } else {
            throw new IllegalArgumentException("Creating a model requires all of the folowing parameters: "
            + DFAFileNameField + ", " + binaryMapFileNameField + " and " + transitionChainFileNameField);
        }

        return entities;
    }

    /*
     * Gather the data that will be output by the software simulator and return it.
     */
    protected Map<String, Object> prepareOutputData(List<String> attrs) {
        Map<String, Object> values = new HashMap<String, Object>();

        for (String attr : attrs) {
            System.out.println("SWSimulator output attribute: " + attr);
            if (attr.equals(binaryPathOutputField)) {
                String output = this.getBinaryPathOutput();
                System.out.println("SWSimulator outputting binaryPath: " + output);
                values.put(attr, output);
                System.out.println("SWSimulator output binaryPath: " + values.get(attr));
            }
            else if (attr.equals(binaryArgumentsOutputField)) {
                JSONArray output = this.getBinaryArgumentsOutput();
                System.out.println("SWSimulator outputting binaryArguments: " + output);
                values.put(attr, output);
                System.out.println("SWSimulator output binaryArguments: " + values.get(attr));
            }
        }

        return values;
    }

    @Override
    public Map<String, Object> getData(Map<String, List<String>> outputs) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();

        for (Map.Entry<String, List<String>> entity : outputs.entrySet()) {
            String eid = entity.getKey();
            List<String> attrs = entity.getValue();

            if (this.hasOutput()) {
                data.put(eid, this.prepareOutputData(attrs));
            }
        }
        return data;
    }

    protected SoftwareSimulatorOutputManager initSoftwareSimulatorOutputManager(Map<String, Object> simParams) {
        JSONObject softwareSimulatorOutput = null;
        String softwareSimulatorOutputDir = null;
        String softwareSimulatorOutputFile = null;

        if (simParams.containsKey(softwareSimulatorOutputDescField)) {
            softwareSimulatorOutput = (JSONObject) simParams.get(softwareSimulatorOutputDescField);
        }

        if (simParams.containsKey(softwareSimulatorOutputDirField)) {
            softwareSimulatorOutputDir = (String) simParams.get(softwareSimulatorOutputDirField);
        } else {
            if (softwareSimulatorOutput != null) {
                throw new IllegalArgumentException("Software simulator has output description but no output directory");
            }
        }

        if (simParams.containsKey(softwareSimulatorOutputFileField)) {
            softwareSimulatorOutputFile = (String) simParams.get(softwareSimulatorOutputFileField);
        } else {
            if (softwareSimulatorOutputDir != null) {
                throw new IllegalArgumentException("Software simulator has output file name but no output directory");
            }
        }

        if (softwareSimulatorOutput != null && softwareSimulatorOutputDir != null) {
            return new SoftwareSimulatorOutputManager(softwareSimulatorOutputDir, softwareSimulatorOutputFile, softwareSimulatorOutput);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> init(String sid, Float timeResolution, Map<String, Object> simParams) throws Exception {
        this.simulatorID = sid;
        this.softwareSimulatorOutputManager = this.initSoftwareSimulatorOutputManager(simParams);

        return SoftwareSimulatorMosaikAPI.meta;
    }

    @SuppressWarnings("unchecked")
    protected void getInputs(long time, Map<String, Object> inputs) {
        for (Map.Entry<String, Object> entity : inputs.entrySet()) {
            Map<String, Object> attrs = (Map<String, Object>) entity.getValue();

            for (Map.Entry<String, Object> attr : attrs.entrySet()) {
                System.out.println("SWSimulator input attribute: " + attr);
                String attrName = attr.getKey();
                // Output from other simulator is the input
                if (attrName.equals(binaryExecutionStatsOutputField)) {
                    Collection<Object> binaryExecutionStats = ((JSONObject) attr.getValue()).values();
                    if (!binaryExecutionStats.isEmpty()) {
                        JSONObject input = (JSONObject) (binaryExecutionStats.stream().findFirst().get());
                        System.out.println("SWSimulator receiving binaryExecutionStats: " + input);
                        this.softwareSimulationController.addBinaryExecutionStats(Long.valueOf(time), input);
                    }
                }
                else {
                	continue;
                }
            }
        }
    }

    /**
     * @return The next time step the software simulator needs to step.
     */
    protected Long getNextTimeStep(long time, long maxAdvance) {
        System.out.println("SWSimulator stepped at time: " + time);

        Number nextStepTime = this.softwareSimulationController.getNextEventTime();

        if (nextStepTime == null) {
            nextStepTime = Long.valueOf(time+1);
        }
        
        System.out.println("SWSimulator next step at: " + nextStepTime.longValue());
        return nextStepTime.longValue();
    }

    @Override
    public Long step(long time, Map<String, Object> inputs, long maxAdvance) throws Exception {

        this.getInputs(time, inputs);

        this.softwareSimulationController.step();

        return this.getNextTimeStep(time, maxAdvance);
    }

    @Override
    public void cleanup() {
        if (this.softwareSimulatorOutputManager != null) {
            this.softwareSimulatorOutputManager.writeAccumulatedOutputToFileInOutputDir(
                this.softwareSimulationController.getExecutionStats());
        }
    }
}
