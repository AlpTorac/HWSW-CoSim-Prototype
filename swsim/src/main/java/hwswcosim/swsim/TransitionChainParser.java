package hwswcosim.swsim;

import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TransitionChainParser {
    public TransitionChainParser() {

    }

    public Collection<ScriptedTransitionEntry> parseTransitionChain(String transitionChainFilePath) {
        JSONArray transitionChainArray = null;

        try {
            transitionChainArray = (JSONArray) JSONValue.parse(new FileReader(transitionChainFilePath));
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
