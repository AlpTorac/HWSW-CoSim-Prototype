package hwswcosim.swsim;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import tel.schich.automata.State;
import tel.schich.automata.transition.CharacterTransition;
import tel.schich.automata.transition.PlannedTransition;

public class BinaryMapParser {
    private DFAParser dfaParser;

    public BinaryMapParser(DFAParser dfaParser) {
        this.dfaParser = dfaParser;
    }

    public DFAParser getDFAParser() {
        return this.dfaParser;
    }

    public Collection<BinaryMapEntry> parseBinaryMap(Set<State> states, Set<PlannedTransition> transitions, String resourceFolderPath, String binaryMapFilePath) {
        JSONArray binaryMapArray = null;

        try {
            binaryMapArray = (JSONArray) JSONValue.parse(new FileReader(resourceFolderPath + "/" + binaryMapFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Collection<BinaryMapEntry> binaryMap = new ArrayList<BinaryMapEntry>();

        for (Object o : binaryMapArray) {
            JSONObject castedO = (JSONObject) o;

            CharacterTransition t = this.getDFAParser().parseTransition(states, (String) castedO.get("transition"));
            String binaryPath = resourceFolderPath + "/" + (String) castedO.get("binary");

            Object binaryArguments = castedO.get("arguments");

            if (binaryArguments == null) {
                binaryMap.add(new BinaryMapEntry(this.getDFAParser().findTransition(transitions, t), binaryPath, null));
            } else {
                binaryMap.add(new BinaryMapEntry(this.getDFAParser().findTransition(transitions, t), binaryPath, (JSONArray) binaryArguments));
            }
        }

        return binaryMap;
    }
}
