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

    private static final String binaryPathInputName = "binary_file_path_in";
    private static final String binaryPathOutputName = "binary_file_path_out";
    private static final String binaryArgumentsInputName = "binary_file_arguments_in";
    private static final String binaryArgumentsOutputName = "binary_file_arguments_out";

    private static final String binaryExecutionStatsOutputName = "binary_execution_stats_out";
    private static final String binaryExecutionStatsInputName = "binary_execution_stats_in";

    private static final JSONObject meta = (JSONObject) JSONValue.parse(("{"
            + "    'api_version': '" + Simulator.API_VERSION + "',"
            + "    'type': 'event-based',"
            + "    'models': {"
            + "        "+"'"+modelName+"'"+": {" + "            'public': true,"
            + "            'params': ['"+DFAFilePathKeyName+"', '"+binaryMapFilePathKeyName+"', '"+transitionChainFilePathKeyName+"'],"
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

    public String getBinaryArgumentsOutput() {
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

    @Override
    public Map<String, Object> getData(Map<String, List<String>> outputs) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();

        for (Map.Entry<String, List<String>> entity : outputs.entrySet()) {
            String eid = entity.getKey();
            List<String> attrs = entity.getValue();
            HashMap<String, Object> values = new HashMap<String, Object>();

            if (this.hasOutput()) {
                for (String attr : attrs) {
                    System.out.println("SWSimulator output attribute: " + attr);
                    if (attr.equals(binaryPathOutputName)) {
                        String output = this.getBinaryPathOutput();
                        System.out.println("SWSimulator outputting binaryPath: " + output);
                        values.put(attr, output);
                        System.out.println("SWSimulator output binaryPath: " + values.get(attr));
                    }
                    else if (attr.equals(binaryArgumentsOutputName)) {
                        String output = this.getBinaryArgumentsOutput();
                        System.out.println("SWSimulator outputting binaryArguments: " + output);
                        values.put(attr, output);
                        System.out.println("SWSimulator output binaryArguments: " + values.get(attr));
                    }
                }
                data.put(eid, values);
            }
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> init(String sid, Float timeResolution, Map<String, Object> simParams) throws Exception {
        this.simulatorID = sid;
        return SoftwareSimulatorMosaikAPI.meta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Long step(long time, Map<String, Object> inputs, long maxAdvance) throws Exception {

        for (Map.Entry<String, Object> entity : inputs.entrySet()) {
            Map<String, Object> attrs = (Map<String, Object>) entity.getValue();

            for (Map.Entry<String, Object> attr : attrs.entrySet()) {
                System.out.println("SWSimulator input attribute: " + attr);
                String attrName = attr.getKey();
                // Output from other simulator is the input
                if (attrName.equals(binaryExecutionStatsOutputName)) {
                    Collection<Object> binaryExecutionStats = ((JSONObject) attr.getValue()).values();
                    if (!binaryExecutionStats.isEmpty()) {
                        String input = (String) (binaryExecutionStats.stream().findFirst().get());
                        System.out.println("SWSimulator receiving binaryExecutionStats: " + input);
                        this.softwareSimulationController.addBinaryExecutionStats(input);
                    }
                }
                else {
                	continue;
                }
            }
        }

        this.softwareSimulationController.step();

        while (!this.hasOutput() && !this.softwareSimulationController.isSimulationTerminated()) {

        }

        System.out.println("SWSimulator stepped at time: " + time 
        //+ ", next step at time: " + (time + this.stepSize)
        );

        return null;
    }
}
