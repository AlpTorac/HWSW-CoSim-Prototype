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

    private static final String DFAFilePathKeyName = "dfa_file_path";
    private static final String binaryMapFilePathKeyName = "transition_to_binary_map_file_path";
    private static final String transitionChainFilePathKeyName = "transition_chain_file_path";
    private static final String softwareSimulatorOutputName = "software_simulator_output";
    private static final String softwareSimulatorOutputDirName = "software_simulator_output_dir";

    private static final String binaryPathInputName = "binary_file_path_in";
    private static final String binaryPathOutputName = "binary_file_path_out";
    private static final String binaryArgumentsInputName = "binary_file_arguments_in";
    private static final String binaryArgumentsOutputName = "binary_file_arguments_out";

    private static final String binaryExecutionStatsOutputName = "binary_execution_stats_out";
    private static final String binaryExecutionStatsInputName = "binary_execution_stats_in";

    private static final JSONObject meta = (JSONObject) JSONValue.parse(("{"
            + "    'api_version': '" + Simulator.API_VERSION + "',"
            + "    'type': 'time-based',"
            + "    'models': {"
            + "        "+"'"+modelName+"'"+": {" + "            'public': true,"
            + "            'params': ['"+DFAFilePathKeyName+"', '"+binaryMapFilePathKeyName
            +"', '"+transitionChainFilePathKeyName+"'],"
            + "            'attrs': ['"+binaryPathOutputName+"', '"+binaryExecutionStatsInputName
            +"', '"+binaryPathInputName+"', '"+binaryExecutionStatsOutputName
            +"', '"+binaryArgumentsInputName+"', '"+binaryArgumentsOutputName+"']"
//            + "            'trigger': ['"+binaryExecutionStatsOutputName+"']"
            + "        }"
            + "    }" + "}").replace("'", "\""));

    public static void main(String[] args) throws Throwable {
        Simulator sim = new SoftwareSimulatorMosaikAPI();
        SimProcess.startSimulation(args, sim);
    }

    private SoftwareSimulationController softwareSimulationController;
    private String simulatorID;
    private SoftwareSimulatorOutputManager softwareSimulatorOutputManager;

    public SoftwareSimulatorMosaikAPI() {
        super(simulatorName);
        this.softwareSimulationController = new SoftwareSimulationController();
    }

    public String getSimulatorID() {
        return this.simulatorID;
    }

    public boolean hasOutput() {
        return this.softwareSimulationController.hasBinaryFilePath();
    }

    public String getBinaryPathOutput() {
        return this.softwareSimulationController.getBinaryFilePath();
    }

    public JSONArray getBinaryArgumentsOutput() {
        return this.softwareSimulationController.getBinaryArguments();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> create(int num, String model, Map<String, Object> modelParams) throws Exception {
        JSONArray entities = new JSONArray();

        String eid = "SW_Model";

        if (modelParams.containsKey(DFAFilePathKeyName) 
        && modelParams.containsKey(binaryMapFilePathKeyName)
        && modelParams.containsKey(transitionChainFilePathKeyName)) {
            String DFAFilePath = (String) modelParams.get(DFAFilePathKeyName);
            String binaryMapFilePath = (String) modelParams.get(binaryMapFilePathKeyName);
            String transitionChainFilePath = (String) modelParams.get(transitionChainFilePathKeyName);

            this.softwareSimulationController.initSoftwareSimulation(DFAFilePath, binaryMapFilePath, transitionChainFilePath);

            JSONObject entity = new JSONObject();
            entity.put("eid", eid);
            entity.put("type", model);
            entity.put("rel", new JSONArray());
            entities.add(entity);

            System.out.println("sw_model created");
        } else {

        }

        return entities;
    }

    protected void prepareOutputData(List<String> attrs, Map<String, Object> values) {
        for (String attr : attrs) {
            System.out.println("SWSimulator output attribute: " + attr);
            if (attr.equals(binaryPathOutputName)) {
                String output = this.getBinaryPathOutput();
                System.out.println("SWSimulator outputting binaryPath: " + output);
                values.put(attr, output);
                System.out.println("SWSimulator output binaryPath: " + values.get(attr));
            }
            else if (attr.equals(binaryArgumentsOutputName)) {
                JSONArray output = this.getBinaryArgumentsOutput();
                System.out.println("SWSimulator outputting binaryArguments: " + output);
                values.put(attr, output);
                System.out.println("SWSimulator output binaryArguments: " + values.get(attr));
            }
        }
    }

    @Override
    public Map<String, Object> getData(Map<String, List<String>> outputs) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();

        for (Map.Entry<String, List<String>> entity : outputs.entrySet()) {
            String eid = entity.getKey();
            List<String> attrs = entity.getValue();
            Map<String, Object> values = new HashMap<String, Object>();

            if (this.hasOutput()) {
                this.prepareOutputData(attrs, values);
                data.put(eid, values);
            }
        }
        return data;
    }

    protected SoftwareSimulatorOutputManager initSoftwareSimulatorOutputManager(Map<String, Object> simParams) {
        JSONObject softwareSimulatorOutput = null;
        String softwareSimulatorOutputDir = null;

        if (simParams.containsKey(softwareSimulatorOutputName)) {
            softwareSimulatorOutput = (JSONObject) simParams.get(softwareSimulatorOutputName);
        }

        if (simParams.containsKey(softwareSimulatorOutputDirName)) {
            softwareSimulatorOutputDir = (String) simParams.get(softwareSimulatorOutputDirName);
        } else {
            if (softwareSimulatorOutput != null) {
                throw new IllegalArgumentException("Software simulator has output but no output directory");
            }
        }

        if (softwareSimulatorOutput != null && softwareSimulatorOutputDir != null) {
            return new SoftwareSimulatorOutputManager(softwareSimulatorOutputDir,
            softwareSimulatorOutput, this.softwareSimulationController);
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
                if (attrName.equals(binaryExecutionStatsOutputName)) {
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

    protected Long getNextTimeStep(long time, long maxAdvance) {
        System.out.println("SWSimulator stepped at time: " + time 
        //+ ", next step at time: " + (time + this.stepSize)
        );

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
            this.softwareSimulatorOutputManager.writeOutput();
        }
    }
}
