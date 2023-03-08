package hwswcosim.swsim;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import tel.schich.automata.DFA;
import tel.schich.automata.NamedState;
import tel.schich.automata.State;
import tel.schich.automata.transition.CharacterTransition;
import tel.schich.automata.transition.PlannedTransition;

/**
 * This class is to be used for parsing {@link DFA} instances with
 * the files provided ({@link #parseDFA(String, String)}).
 */
public class DFAParser {
    private final Pattern statePattern = Pattern.compile("\\w+");
    private final Pattern transitionPattern = Pattern.compile("\\(" 
        + "(" + statePattern.pattern() + ")" 
        + ",(" + statePattern.pattern() + ")" 
        + ",(" + "\\w" + ")"
        + "\\)");

    public DFAParser() {
        
    }

    /**
     * A helper method that returns the {@link State} instance with the given
     * name "stateName".
     */
    protected State findState(Set<State> states, String stateName) {
        return states.stream().filter(s -> s.getLabel().equals(stateName)).findFirst().get();
    }

    /**
     * A helper method that returns the {@link PlannedTransition} instance "t" from the given "transitions" with
     * <code>t.equals(transition) == true</code>.
     */
    protected PlannedTransition findTransition(Set<PlannedTransition> transitions, PlannedTransition transition) {
        return transitions.stream().filter(t -> t.equals(transition)).findFirst().get();
    }

    /**
     * A helper method that returns the {@link PlannedTransition} instance "t" from the given "transitions", which
     * has the same origin state, target state and input.
     */
    protected PlannedTransition findTransition(Set<PlannedTransition> transitions, String sourceState, String targetState, char input) {
        return transitions.stream().filter(t -> t.getOrigin().getLabel().equals(sourceState)
                && t.getDestination().getLabel().equals(targetState)
                && ((CharacterTransition) t).getWith() == input).findFirst().get();
    }

    /**
     * @return The {@link NamedState} instance parsed from "stateText".
     */
    protected NamedState parseState(String stateText) {
        Matcher m = this.statePattern.matcher(stateText);

        if (m.matches()) {
            return new NamedState(m.group());
        }

        return null;
    }

    /**
     * @return A {@link Set} of {@link NamedState} instances parsed from
     * <code>dfaInfo.get("states")</code>.
     */
    protected Set<State> parseStates(JSONObject dfaInfo) {
        Set<State> states = new HashSet<State>();
        JSONArray stateArray = (JSONArray) dfaInfo.get("states");

        for (Object s : stateArray) {
            states.add(this.parseState((String) s));
        }

        return states;
    }

    /**
     * @return The {@link CharacterTransition} instance parsed from "transitionText".
     */
    protected CharacterTransition parseTransition(Set<State> states, String transitionText) {
        Matcher mT = this.transitionPattern.matcher(transitionText);

        if (mT.matches()) {
            return new CharacterTransition(
                this.findState(states, mT.group(1)),
                mT.group(3).charAt(0),
                this.findState(states, mT.group(2)));
        }

        return null;
    }

    /**
     * @return A {@link Set} of {@link PlannedTransition} instances parsed from
     * <code>dfaInfo.get("transitions")</code>.
     */
    protected Set<PlannedTransition> parseTransitions(Set<State> states, JSONObject dfaInfo) {
        Set<PlannedTransition> transitions = new HashSet<PlannedTransition>();
        JSONArray transitionInfo = (JSONArray) dfaInfo.get("transitions");

        for (Object s : transitionInfo) {
            transitions.add(this.parseTransition(states, (String) s));
        }

        return transitions;
    }

    /**
     * Parses the start {@link State} from <code>dfaInfo.get("start_state")</code> and
     * returns the {@link State} instance with the matching name from "states".
     */
    protected State parseStartState(Set<State> states, JSONObject dfaInfo) {
        Matcher m = this.statePattern.matcher((String) dfaInfo.get("start_state"));

        if (m.matches()) {
            return this.findState(states, m.group());
        } else {
            throw new IllegalArgumentException("No start state could be parsed.");
        }
    }

    /**
     * Parses the end {@link State} instances from <code>dfaInfo.get("end_states")</code> and
     * returns a {@link Set} of end {@link State} instances, all with matching names from "states".
     */
    protected Set<State> parseEndStates(Set<State> states, JSONObject dfaInfo) {
        Set<State> endStates = new HashSet<State>();
        JSONArray stateInfo = (JSONArray) dfaInfo.get("end_states");

        for (Object s : stateInfo) {
            Matcher m = this.statePattern.matcher((String) s);
            if (m.matches()) {
                endStates.add(this.findState(states, m.group()));
            }
        }

        return endStates;
    }

    /**
     * Parse the {@link DFA} from the file inside "resourceFolderPath" directory with
     * the name "DFAFileName".
     * 
     * @param resourceFolderPath The absolute path to the folder, in which resource files reside
     * @param DFAFileName The name of the file that describes the DFA
     * @return The said {@link DFA} instance.
     */
    protected DFA parseDFA(String resourceFolderPath, String DFAFileName) {
        JSONObject dfaInfo = null;

        try {
            dfaInfo = (JSONObject) JSONValue.parse(new FileReader(resourceFolderPath + "/" + DFAFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Set<State> states = this.parseStates(dfaInfo);
        Set<PlannedTransition> transitions = this.parseTransitions(states, dfaInfo);
        State startState = this.parseStartState(states, dfaInfo);
        Set<State> endStates = this.parseEndStates(states, dfaInfo);

        return new DFA(states, transitions, startState, endStates);
    }
}
