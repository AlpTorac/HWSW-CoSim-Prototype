package hwswcosim.swsim;

import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This class is used to parse a {@link Collection} of {@link ScriptedTransitionEntry} instances
 * from a file that contains the necessary information ({@link #parseTransitionChain(String, String)}).
 */
public class TransitionChainParser {
    public TransitionChainParser() {

    }
    /**
     * Parse a {@link Collection} of {@link ScriptedTransitionEntry} instances
     * from the given file that contains the necessary information.
     * 
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside
     * @param transitionChainFileName The name of the file that contains all {@link ScriptedTransitionEntry} information
     * @return The said parsed {@link Collection} of {@link ScriptedTransitionEntry}
     */
    public Collection<ScriptedTransitionEntry> parseTransitionChain(String resourceFolderPath, String transitionChainFileName) {
        JSONArray transitionChainArray = null;

        try {
            transitionChainArray = (JSONArray) JSONValue.parse(new FileReader(resourceFolderPath + "/" + transitionChainFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<ScriptedTransitionEntry> transitionChain = new ArrayList<ScriptedTransitionEntry>();

        for (Object o : transitionChainArray) {
            JSONObject castedO = (JSONObject) o;

            transitionChain.add(new ScriptedTransitionEntry(((String) castedO.get("input")).charAt(0), Double.parseDouble((String) castedO.get("time"))));
        }

        return transitionChain;
    }
}
