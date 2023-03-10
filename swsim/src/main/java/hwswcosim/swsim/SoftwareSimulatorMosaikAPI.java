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

/**
 * This class is the concrete implementation of {@link de.offis.mosaik.api.Simulator}
 * for the software simulator.
 */
public class SoftwareSimulatorMosaikAPI extends Simulator {

    /*
     * The static final String members given below store the names of various
     * attributes and parameters that will be used by mosaik. What they should
     * be used for and how they could look like is explained below.
     */

    private static final String simulatorName = "SoftwareSimulator";
    private static final String modelName = "DFAWrapper";

    /**
     * The name and the extension of the file that contains DFA information as a String.
     * 
     * @see {@link DFAParser} and {@link DFAWrapper#dfa}
     */
    private static final String DFAFileNameField = "dfa_file_name";
    /**
     * The name and the extension of the file that contains binary map information as a String.
     * 
     * @see {@link BinaryMapParser} and {@link DFAWrapper#binaryMap}
     */
    private static final String binaryMapFileNameField = "transition_to_binary_map_file_name";
    /**
     * The name and the extension of the file that contains binary map information as a String.
     * 
     * @see {@link TransitionChainParser} and {@link SoftwareSimulationController#transitionChain}
     */
    private static final String transitionChainFileNameField = "transition_chain_file_name";
    /**
     * The absolute path of the folder as a String, which contains the files from
     * {@link #DFAFileNameField}, {@link #binaryMapFileNameField} and {@link #transitionChainFileNameField}.
     */
    private static final String resourceFolderPathField = "resource_folder_path";

    /**
     * software_simulator_output_desc specifies how the software simulator should
     * summarise the statistics received.
     *
     * Format: {"output_name_1": "action_1", ..., "output_name_n": "action_n"}
     *          In JSONObject format ({@link JSONObject})
     * Actions: {@link SoftwareSimulatorOutputManager#computeOutputEntryValue(Collection, String)}
     */
    private static final String softwareSimulatorOutputDescField = "software_simulator_output_desc";
    /**
     * The absolute path of the folder as a String, to which {@link #cleanup()} will output.
     */
    private static final String softwareSimulatorOutputDirField = "software_simulator_output_dir";
    /**
     * The name of the output file as a String, which {@link #cleanup()} will fill before this simulator finishes.
     */
    private static final String softwareSimulatorOutputFileNameField = "software_simulator_output_file_name";

    /**
     * The absolute path to the binary file as a String, which will be run by an outside component.
     */
    private static final String binaryPathField = "binary_file_path";
    /**
     * Binary arguments that belong with the binary from {@link #binaryPathField} as a JSONArray {@link JSONArray}.
     * Note that all arguments given will be interpret as literal String instances. As of now, it is not possible
     * to define variables as arguments.
     * 
     * Format: ["arg1", "arg2", ..., "arg3"]
     */
    private static final String binaryArgumentsField = "binary_file_arguments";
    /**
     * Binary execution statistics received in either JSONObject {@link JSONObject} format (if there is only a
     * single statistics object) or JSONArray of JSONObjects (if there can be multiple statistics objects. One
     * such JSONArray can also have a single JSONObject).
     * 
     * For each statistic, there is a name field (has to be String) and a value field (any Object).
     * 
     * Format:
     *      JSONObject: {"stat_name_1": stat_value_1, ..., "stat_name_N": stat_value_N}
     *      JSONArray: [JSONObject_1, ..., JSONObject_M]
     */
    private static final String binaryExecutionStatsField = "binary_execution_stats";

    /**
     * The metadata that will be returned to mosaik upon {@link #init(String, Float, Map)}
     */
    private static final JSONObject meta = (JSONObject) JSONValue.parse(("{"
            + "    'api_version': '" + Simulator.API_VERSION + "',"
            + "    'type': 'time-based',"
            + "    'models': {"
            + "        "+"'"+modelName+"'"+": {" + "            'public': true,"
            + "            'params': ['"+resourceFolderPathField+"', '"+DFAFileNameField+"', '"+binaryMapFileNameField
            + "', '"+transitionChainFileNameField+"'],"
            + "            'attrs': ['"+binaryPathField+"', '"+binaryArgumentsField+"', '"+binaryExecutionStatsField+"']"
            + "        }"
            + "    }" + "}").replace("'", "\""));

    /**
     * The entry point to the software simulator.
     * 
     * @param args args[0] is the ip address to connect to
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable {
        Simulator sim = new SoftwareSimulatorMosaikAPI(simulatorName);
        SimProcess.startSimulation(args, sim);
    }

    /**
     * The object that manages the underlying software simulation.
     */
    private SoftwareSimulationController softwareSimulationController;
    private String simulatorID;

    /**
     * The object that is responsible for what {@link #cleanup()} outputs.
     */
    protected SoftwareSimulatorOutputManager softwareSimulatorOutputManager;

    /**
     * @param simulatorName Name assigned to this simulator by the mosaik scenario.
     */
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

    /**
     * Checks whether this simulator needs to send data to mosaik.
     * 
     * @return True if this simulator has something to output during the
     * co-simulation.
     */
    protected boolean hasOutput() {
        return this.softwareSimulationController.hasBinaryFilePath();
    }

    /**
     * @see {@link SoftwareSimulationController#getBinaryFilePath()}
     */
    protected String getBinaryFilePath() {
        return this.softwareSimulationController.getBinaryFilePath();
    }


    /**
     * @see {@link SoftwareSimulationController#getBinaryArguments()}
     */
    protected JSONArray getBinaryArguments() {
        return this.softwareSimulationController.getBinaryArguments();
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

            //System.out.println("sw_model created");
        } else {
            throw new IllegalArgumentException("Creating a model requires all of the folowing parameters: "
            + DFAFileNameField + ", " + binaryMapFileNameField + " and " + transitionChainFileNameField);
        }

        return entities;
    }

    /**
     * Gather the data that will be output by the software simulator and return it. This
     * method is intended to be used by {@link #getData(Map)}.
     * 
     * @param attrs A subset of attributes from "attrs" field of {@link #meta} as a list,
     * which will be used to provide data to mosaik.
     * @return A mapping of the attributes from "attrs" parameter to their current
     * value. 
     */
    protected Map<String, Object> prepareOutputData(List<String> attrs) {
        Map<String, Object> values = new HashMap<String, Object>();

        for (String attr : attrs) {
            //System.out.println("SWSimulator output attribute: " + attr);
            if (attr.equals(binaryPathField)) {
                String output = this.getBinaryFilePath();
                //System.out.println("SWSimulator outputting binaryPath: " + output);
                values.put(attr, output);
                //System.out.println("SWSimulator output binaryPath: " + values.get(attr));
            }
            else if (attr.equals(binaryArgumentsField)) {
                JSONArray output = this.getBinaryArguments();
                //System.out.println("SWSimulator outputting binaryArguments: " + output);
                values.put(attr, output);
                //System.out.println("SWSimulator output binaryArguments: " + values.get(attr));
            }
        }

        return values;
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

            if (this.hasOutput()) {
                data.put(eid, this.prepareOutputData(attrs));
            }
        }
        return data;
    }

    /**
     * Initialises the {@link #softwareSimulationController} from the given simParams.
     * 
     * @param simParams see {@link #init(String, Float, Map)}.
     * @return A {@link SoftwareSimulatorOutputManager} instance.
     */
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

        if (simParams.containsKey(softwareSimulatorOutputFileNameField)) {
            softwareSimulatorOutputFile = (String) simParams.get(softwareSimulatorOutputFileNameField);
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
        this.softwareSimulatorOutputManager = this.initSoftwareSimulatorOutputManager(simParams);

        return SoftwareSimulatorMosaikAPI.meta;
    }

    /**
     * Processes the given inputs. This method is intended to be used from
     * {@link #step(long, Map, long)}.
     * 
     * @see {@link #step(long, Map, long)} for more information.
     */
    @SuppressWarnings("unchecked")
    protected void processInputs(long time, Map<String, Object> inputs) {
        for (Map.Entry<String, Object> entity : inputs.entrySet()) {
            Map<String, Object> attrs = (Map<String, Object>) entity.getValue();

            for (Map.Entry<String, Object> attr : attrs.entrySet()) {
                //System.out.println("SWSimulator input attribute: " + attr);
                String attrName = attr.getKey();
                // Output from other simulator is the input
                if (attrName.equals(binaryExecutionStatsField)) {
                    Collection<Object> binaryExecutionStats = ((JSONObject) attr.getValue()).values();
                    if (!binaryExecutionStats.isEmpty()) {
                        Object receivedInputStats = binaryExecutionStats.stream().findFirst().get();

                        //System.out.println("SWSimulator receiving binaryExecutionStats: " + receivedInputStats);

                        if (receivedInputStats instanceof JSONArray) {
                            JSONArray inputStatsArray = (JSONArray) receivedInputStats;
                            inputStatsArray.forEach(inputStats -> {
                                this.softwareSimulationController.addBinaryExecutionStats(Long.valueOf(time), (JSONObject) inputStats);
                            });
                        }
                        else if (receivedInputStats instanceof JSONObject) {
                            this.softwareSimulationController.addBinaryExecutionStats(Long.valueOf(time), (JSONObject) receivedInputStats);
                        }
                    }
                }
                else {
                	continue;
                }
            }
        }
    }

    /**
     * @return The next time step, when {@link #step(long, Map, long)} needs to be
     * called again.
     */
    protected Long getNextTimeStep(long time, long maxAdvance) {
        //System.out.println("SWSimulator stepped at time: " + time);

        Number nextStepTime = this.softwareSimulationController.getNextEventTime();

        if (nextStepTime == null) {
            nextStepTime = Long.valueOf(time+1);
        }
        
        //System.out.println("SWSimulator next step at: " + nextStepTime.longValue());
        return nextStepTime.longValue();
    }

    /**
     * Perform the next simulation step from time <em>time</em> using input
     * values from <em>inputs</em> and return the new simulation time (the time
     * at which this method should be called again).
     *
     * Here, it receives the input provided from mosaik and advances the software
     * simulation by calling {@link SoftwareSimulationController#step()}.
     *
     * @param time is the current time in seconds from simulation start.
     * @param inputs is a map of input values (see {@link
     *               https://mosaik.readthedocs.org/en/latest/mosaik-api/low-level.html#step}).
     * @param maxAdvance tells the simulator how far it can advance its time
     *   without risking any causality error, i.e. it is guaranteed that no
     *   external step will be triggered before max_advance + 1, unless the
     *   simulator activates an output loop earlier than that. For time-based
     *   simulators (or hybrid ones without any triggering input) *max_advance*
	 *
     * @return the time at which this method should be called again in seconds
     *         since simulation start or null, if this method does not have to
     *         be called at a specific time in the future (such as some event-based
     *         simulators like the Collector.py example from the mosaik documentation).
     * @throws Exception
     */
    @Override
    public Long step(long time, Map<String, Object> inputs, long maxAdvance) throws Exception {

        this.processInputs(time, inputs);

        this.softwareSimulationController.step();

        return this.getNextTimeStep(time, maxAdvance);
    }

    /**
     * This method is executed just before the co-simulation run by mosaik ends.
     * Here, it summarises what this class receives from mosaik respecting the
     * configurations in {@link SoftwareSimulatorOutputManager}
     * @throws Exception
     */
    @Override
    public void cleanup() {
        if (this.softwareSimulatorOutputManager != null) {
            this.softwareSimulatorOutputManager.writeAccumulatedOutputToFileInOutputDir(
                this.softwareSimulationController.getExecutionStats());
        }
    }
}
