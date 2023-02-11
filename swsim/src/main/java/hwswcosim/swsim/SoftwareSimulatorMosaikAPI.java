package hwswcosim.swsim;

import java.util.List;
import java.util.Map;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.offis.mosaik.api.SimProcess;
import de.offis.mosaik.api.Simulator;

public class SoftwareSimulatorMosaikAPI extends Simulator {

    private static final String modelName = "DFAWrapper";

    private static final String DFAFilePathKeyName = "dfa_file_path";
    private static final String binaryMapFilePathKeyName = "transition_to_binary_map_file_path";
    private static final String transitionChainFilePathKeyName = "transition_chain_file_path";

    private static final String binaryPathOutputName = "binary_file_path";

    private static final String binaryExecutionStatsInputName = "binary_execution_stats";

    private static final String stepSizeKeyName = "step_size";

    private static final JSONObject meta = (JSONObject) JSONValue.parse(("{"
            + "    'api_version': '" + Simulator.API_VERSION + "',"
            + "    'type': 'event-based',"
            + "    'models': {"
            + "        "+"'"+modelName+"'"+": {" + "            'public': true,"
            + "            'params': ['"+DFAFilePathKeyName+"', '"+binaryMapFilePathKeyName+"', '"+transitionChainFilePathKeyName+"'],"
            + "            'attrs': ['"+binaryPathOutputName+"', '"+binaryExecutionStatsInputName+"']" + "        }"
            + "    }" + "}").replace("'", "\""));

    public static void main(String[] args) throws Throwable {
        Simulator sim = new SoftwareSimulatorMosaikAPI();
        SimProcess.startSimulation(args, sim);
    }

    private SoftwareSimulator softwareSimulator;

    private int idCounter = 0;
    private int stepSize = 1;

    public SoftwareSimulatorMosaikAPI() {
        super("SoftwareSimulator");
        this.softwareSimulator = new SoftwareSimulator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> create(int num, String model, Map<String, Object> modelParams) throws Exception {
        JSONArray entities = new JSONArray();

        for (int i = 0; i < num; i++) {

            String eid = "EM_" + (this.idCounter + i);

            if (modelParams.containsKey(DFAFilePathKeyName) 
            && modelParams.containsKey(binaryMapFilePathKeyName)
            && modelParams.containsKey(transitionChainFilePathKeyName)) {
                String DFAFilePath = (String) modelParams.get(DFAFilePathKeyName);
                String binaryMapFilePath = (String) modelParams.get(binaryMapFilePathKeyName);
                String transitionChainFilePath = (String) modelParams.get(transitionChainFilePathKeyName);

                this.softwareSimulator.addDFAWrapper(eid, DFAFilePath, binaryMapFilePath, transitionChainFilePath);
            } else {
                continue;
            }

            JSONObject entity = new JSONObject();
            entity.put("eid", eid);
            entity.put("type", model);
            entity.put("rel", new JSONArray());
            entities.add(entity);
        }
        this.idCounter += num;
        return entities;
    }

    @Override
    public Map<String, Object> getData(Map<String, List<String>> outputs) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();

        for (Map.Entry<String, List<String>> entity : outputs.entrySet()) {
            String eid = entity.getKey();
            List<String> attrs = entity.getValue();
            HashMap<String, Object> values = new HashMap<String, Object>();
            DFAWrapper instance = this.softwareSimulator.getDFAWrapper(eid);

            for (String attr : attrs) {
                if (attr.equals(binaryPathOutputName)) {
                	values.put(attr, instance.getCurrentBinaryPath());
                }
            }
            data.put(eid, values);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> init(String sid, Float timeResolution, Map<String, Object> simParams) throws Exception {
        if (simParams.containsKey(stepSizeKeyName)) {
            this.stepSize = ((Number) simParams.get(stepSizeKeyName)).intValue();
        }
        return SoftwareSimulatorMosaikAPI.meta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public long step(long time, Map<String, Object> inputs, long maxAdvance) throws Exception {

        for (Map.Entry<String, Object> entity : inputs.entrySet()) {
            Map<String, Object> attrs = (Map<String, Object>) entity.getValue();

            for (Map.Entry<String, Object> attr : attrs.entrySet()) {
                String attrName = attr.getKey();
                if (attrName.equals(binaryExecutionStatsInputName)) {
                    this.softwareSimulator.addBinaryExecutionStats();
                }
                else {
                	continue;
                }
            }
        }

        this.softwareSimulator.stepAll(time);

        return time + this.stepSize;
    }
}
