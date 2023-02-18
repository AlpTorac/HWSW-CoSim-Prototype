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

public class DummyHWSimulator extends Simulator {

    private static final String modelName = "DummyHWModel";

    private static final String binaryPathInputName = "binary_file_path_in";
    private static final String binaryPathOutputName = "binary_file_path_out";

    private static final String binaryExecutionStatsOutputName = "binary_execution_stats_out";
    private static final String binaryExecutionStatsInputName = "binary_execution_stats_in";

    private static final JSONObject meta = (JSONObject) JSONValue.parse(("{"
            + "    'api_version': '" + Simulator.API_VERSION + "',"
            + "    'type': 'event-based',"
            + "    'models': {"
            + "        "+"'"+modelName+"'"+": {" + "            'public': true,"
            + "            'params': '',"
            + "            'attrs': ['"+binaryPathOutputName+"', '"+binaryExecutionStatsInputName+"', '"+binaryPathInputName+"', '"+binaryExecutionStatsOutputName+"']"
//            + "            'trigger': ['"+binaryPathOutputName+"']"
            + "        }"
            + "    }" + "}").replace("'", "\""));

    public static void main(String[] args) throws Throwable {
        Simulator sim = new DummyHWSimulator();
        SimProcess.startSimulation(args, sim);
    }

    private int idCounter = 0;
//    private int stepSize = 1;
    private String simulatorID;

    private final HashMap<String, DummyHWModel> instances;

    public DummyHWSimulator() {
        super("DummyHWSimulator");
        this.instances = new HashMap<String, DummyHWModel>();
    }

    public String getSimulatorID() {
        return this.simulatorID;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> create(int num, String model, Map<String, Object> modelParams) throws Exception {
        JSONArray entities = new JSONArray();

        for (int i = 0; i < num; i++) {

            String eid = "HW_Model_" + (this.idCounter + i);

            this.instances.put(eid, new DummyHWModel());

            System.out.println("Added DummyHWModel");

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
            DummyHWModel instance = this.instances.get(eid);

            if (instance.hasOutput()) {
                for (String attr : attrs) {
                    System.out.println("HWSimulator output attribute: " + attr);
                    if (attr.equals(binaryExecutionStatsOutputName)) {
                        String output = instance.mockExecutionStats();
                        System.out.println("HWSimulator outputting binaryExecutionStats: " + output);
                        values.put(attr, output);
                        System.out.println("HWSimulator output binaryExecutionStats: " + values.get(attr));
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
        /*
        if (simParams.containsKey(stepSizeKeyName)) {
            this.stepSize = ((Number) simParams.get(stepSizeKeyName)).intValue();
        }
        */
        return DummyHWSimulator.meta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Long step(long time, Map<String, Object> inputs, long maxAdvance) throws Exception {

        for (Map.Entry<String, Object> entity : inputs.entrySet()) {
            Map<String, Object> attrs = (Map<String, Object>) entity.getValue();

            for (Map.Entry<String, Object> attr : attrs.entrySet()) {
                System.out.println("HWSimulator input attribute: " + attr);
                String attrName = attr.getKey();
                // Output attribute from the other simulator is the input
                if (attrName.equals(binaryPathOutputName)) {
                    Collection<Object> binaryPaths = ((JSONObject) attr.getValue()).values();
                    if (!binaryPaths.isEmpty()) {
                        String input = (String) (binaryPaths.stream().findFirst().get());
                        System.out.println("HWSimulator receiving binaryPath: " + input);
                        this.instances.values().forEach(model -> model.setCurrentBinaryPath(input));
                    }
                }
                else {
                	continue;
                }
            }
        }

        System.out.println("HWSimulator stepped at time: " + time 
        //+ ", next step at time: " + (time + this.stepSize)
        );

        return null;
    }

    public class DummyHWModel {
        private String currentBinaryPath;

        public DummyHWModel() {

        }

        public void setCurrentBinaryPath(String binaryPath) {
            this.currentBinaryPath = binaryPath;
            System.out.println("DummyHWModel binaryPath set to: " + this.currentBinaryPath);
        }

        public String mockExecutionStats() {
            String result = this.currentBinaryPath + "_stats";
            this.currentBinaryPath = "";
            return result;
        }

        public boolean hasOutput() {
            return this.currentBinaryPath != null && !this.currentBinaryPath.isEmpty();
        }
    }
}
