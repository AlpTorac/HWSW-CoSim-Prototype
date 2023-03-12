package hwswcosim.swsim;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collection;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.offis.mosaik.api.SimProcess;
import de.offis.mosaik.api.Simulator;

/**
 * This class is a concrete implementation of {@link de.offis.mosaik.api.Simulator}
 * and it was made to test the software simulator without
 * using real hardware simulators to spare execution time.
 */
public class DummyHWSimulator extends Simulator {

    private static final String modelName = "DummyHWModel";

    private static final String binaryPathField = "binary_file_path";
    private static final String binaryArgumentsField = "binary_file_arguments";
    private static final String binaryExecutionStatsField = "binary_execution_stats";

    /**
     * The metadata that will be returned to mosaik upon {@link #init(String, Float, Map)}
     */
    private static final JSONObject meta = (JSONObject) JSONValue.parse(("{"
            + "    'api_version': '" + Simulator.API_VERSION + "',"
            + "    'type': 'event-based',"
            + "    'models': {"
            + "        "+"'"+modelName+"'"+": {" + "            'public': true,"
            + "            'params': '',"
            + "            'attrs': ['"+binaryPathField+"', '"+binaryArgumentsField+"', '"+binaryExecutionStatsField+"']"
            + "        }"
            + "    }" + "}").replace("'", "\""));

    public static void main(String[] args) throws Throwable {
        Simulator sim = new DummyHWSimulator();
        SimProcess.startSimulation(args, sim);
    }

    private String simulatorID;

    /**
     * The {@link DummyHWModel} instance to be used by this class.
     */
    private DummyHWModel instance;

    /**
     * Initialise this class.
     * 
     * @see {@link de.offis.mosaik.api.Simulator#Simulator(String)}
     */
    public DummyHWSimulator() {
        super("DummyHWSimulator");
    }

    public String getSimulatorID() {
        return this.simulatorID;
    }

    /**
     * Create <em>num</em> instances of <em>model</em> using the provided
     * <em>model_params</em>.
     *
     * @param num is the number of instances to create.
     * @param model is the name of the model to instantiate. It needs to be
     *              listed in the simulator's meta data and be public.
     * @param modelParams is a map containing additional model parameters.
     * @return a (nested) list of maps describing the created entities (model
     *         instances) (see {@link
     *         https://mosaik.readthedocs.org/en/latest/mosaik-api/low-level.html#create}).
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> create(int num, String model, Map<String, Object> modelParams) throws Exception {
        JSONArray entities = new JSONArray();

        for (int i = 0; i < num; i++) {

            String eid = "HW_Model";

            this.instance = new DummyHWModel();

            System.out.println("Added DummyHWModel");

            JSONObject entity = new JSONObject();
            entity.put("eid", eid);
            entity.put("type", model);
            entity.put("rel", new JSONArray());
            entities.add(entity);
        }

        return entities;
    }

    /**
     * Return the data for the requested attributes in "outputs" parameter
     *
     * @param outputs is a mapping of entity IDs to lists of attribute names.
     * @return a mapping of the same entity IDs to maps with attributes and
     *         their values (see {@link
     *         https://mosaik.readthedocs.org/en/latest/mosaik-api/low-level.html#get-data}).
     * @throws Exception
     */
    @Override
    public Map<String, Object> getData(Map<String, List<String>> outputs) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();

        for (Map.Entry<String, List<String>> entity : outputs.entrySet()) {
            String eid = entity.getKey();
            List<String> attrs = entity.getValue();
            HashMap<String, Object> values = new HashMap<String, Object>();
            DummyHWModel instance = this.instance;

            if (instance.hasOutput()) {
                for (String attr : attrs) {
                    System.out.println("HWSimulator output attribute: " + attr);
                    if (attr.equals(binaryExecutionStatsField)) {
                        Object output = instance.mockExecutionStats();
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

    /**
     * Initialize the simulator with the ID <em>sid</em> and apply additional
     * parameters <em>(simParams)</em> sent by mosaik.
     *
     * @param sid is the ID mosaik has given to this simulator.
     * @param timeResolution a given value to scale the long "time" parameters given throughout
     * this class to make time steps more flexible (normally they would only be equal to 1 second each)
     * @param simParams a map with additional simulation parameters received from mosaik scenario.
     * @return the meta data dictionary (see {@link
     *         <a href="https://mosaik.readthedocs.org/en/latest/mosaik-api/low-level.html#init">https://mosaik.readthedocs.org/en/latest/mosaik-api/low-level.html#init</a>}).
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> init(String sid, Float timeResolution, Map<String, Object> simParams) throws Exception {
        this.simulatorID = sid;
        return DummyHWSimulator.meta;
    }

    /**
     * Processes the given inputs.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Long step(long time, Map<String, Object> inputs, long maxAdvance) throws Exception {

        for (Map.Entry<String, Object> entity : inputs.entrySet()) {
            Map<String, Object> attrs = (Map<String, Object>) entity.getValue();

            for (Map.Entry<String, Object> attr : attrs.entrySet()) {
                System.out.println("HWSimulator input attribute: " + attr);
                String attrName = attr.getKey();
                // Output attribute from the other simulator is the input
                if (attrName.equals(binaryPathField)) {
                    Collection<Object> binaryPaths = ((JSONObject) attr.getValue()).values();
                    if (!binaryPaths.isEmpty()) {
                        String input = (String) (binaryPaths.stream().findFirst().get());
                        System.out.println("HWSimulator receiving binaryPath: " + input);
                        this.instance.setCurrentBinaryPath(input);
                    }
                }
                else if (attrName.equals(binaryArgumentsField)) {
                    Collection<Object> binaryArguments = ((JSONObject) attr.getValue()).values();
                    if (!binaryArguments.isEmpty()) {
                        Optional<Object> bargs = binaryArguments.stream().filter(e -> e != null).findFirst();
                        if (bargs.isPresent()) {
                            JSONArray input = (JSONArray) (bargs.get());
                            System.out.println("HWSimulator receiving binaryArguments: " + input);
                            this.instance.setCurrentBinaryArguments(input);
                        }
                    }
                }
                else {
                	continue;
                }
            }
        }

        System.out.println("HWSimulator stepped at time: " + time);

        return null;
    }
}
