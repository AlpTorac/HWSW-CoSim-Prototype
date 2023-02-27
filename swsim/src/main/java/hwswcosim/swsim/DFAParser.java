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

public class DFAParser {
    private final Pattern statePattern = Pattern.compile("\\w+");
    private final Pattern transitionPattern = Pattern.compile("\\(" 
        + "(" + statePattern.pattern() + ")" 
        + ",(" + statePattern.pattern() + ")" 
        + ",(" + "\\w" + ")"
        + "\\)");

    public DFAParser() {
        
    }

    protected State findState(Set<State> states, String stateName) {
        return states.stream().filter(s -> s.getLabel().equals(stateName)).findFirst().get();
    }

    protected PlannedTransition findTransition(Set<PlannedTransition> transitions, PlannedTransition transition) {
        return transitions.stream().filter(t -> t.equals(transition)).findFirst().get();
    }

    protected PlannedTransition findTransition(Set<PlannedTransition> transitions, String sourceState, String targetState, char input) {
        return transitions.stream().filter(t -> t.getOrigin().getLabel().equals(sourceState)
                && t.getDestination().getLabel().equals(targetState)
                && ((CharacterTransition) t).getWith() == input).findFirst().get();
    }

    protected NamedState parseState(String stateText) {
        Matcher m = this.statePattern.matcher(stateText);

        if (m.matches()) {
            return new NamedState(m.group());
        }

        return null;
    }

    protected Set<State> parseStates(JSONObject dfaInfo) {
        Set<State> states = new HashSet<State>();
        JSONArray stateArray = (JSONArray) dfaInfo.get("states");

        for (Object s : stateArray) {
            states.add(this.parseState((String) s));
        }

        return states;
    }

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

    protected Set<PlannedTransition> parseTransitions(Set<State> states, JSONObject dfaInfo) {
        Set<PlannedTransition> transitions = new HashSet<PlannedTransition>();
        JSONArray transitionInfo = (JSONArray) dfaInfo.get("transitions");

        for (Object s : transitionInfo) {
            transitions.add(this.parseTransition(states, (String) s));
        }

        return transitions;
    }

    protected State parseStartState(Set<State> states, JSONObject dfaInfo) {
        Matcher m = this.statePattern.matcher((String) dfaInfo.get("start_state"));

        if (m.matches()) {
            return this.findState(states, m.group());
        } else {
            throw new IllegalArgumentException("No start state could be parsed.");
        }
    }

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

    protected DFA parseDFA(String resourceFolderPath, String DFAFilePath) {
        JSONObject dfaInfo = null;

        try {
            dfaInfo = (JSONObject) JSONValue.parse(new FileReader(resourceFolderPath + "/" + DFAFilePath));
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
