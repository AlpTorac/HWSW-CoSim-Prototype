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

/**
 * This class, along with a {@link DFAParser}, is to be used to parse a
 * {@link Collection} of {@link BinaryMapEntry} instances using the contents of
 * the files provided to {@link #parseBinaryMap(Set, Set, String, String)}.
 */
public class BinaryMapParser {
    private DFAParser dfaParser;

    public BinaryMapParser(DFAParser dfaParser) {
        this.dfaParser = dfaParser;
    }

    public DFAParser getDFAParser() {
        return this.dfaParser;
    }
    /**
     * Parse a {@link Collection} of {@link BinaryMapEntry} instances using the parameters provided.
     * 
     * @param states All {@link State} instances from a {@link DFA}, which the binary map will use.
     * @param transitions All {@link PlannedTransition} instances from a {@link DFA}, which the binary map will use.
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside.
     * @param binaryMapFileName The name of the file that contains all {@link BinaryMapEntry} information.
     * @return The said {@link Collection} of {@link BinaryMapEntry} instances.
     */
    public Collection<BinaryMapEntry> parseBinaryMap(Set<State> states, Set<PlannedTransition> transitions, String resourceFolderPath, String binaryMapFileName) {
        JSONArray binaryMapArray = null;

        try {
            binaryMapArray = (JSONArray) JSONValue.parse(new FileReader(resourceFolderPath + "/" + binaryMapFileName));
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
