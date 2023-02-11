package hwswcosim.swsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import tel.schich.automata.DFA;
import tel.schich.automata.NamedState;
import tel.schich.automata.State;
import tel.schich.automata.transition.CharacterTransition;
import tel.schich.automata.transition.PlannedTransition;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class DFAWrapperParser {
    
    private Set<State> states = new HashSet<State>();
    private Set<PlannedTransition> transitions = new HashSet<PlannedTransition>();
    private State startState;
    private Set<State> endStates = new HashSet<State>();

    private final Pattern statePattern = Pattern.compile("\\w+");
    private final Pattern transitionPattern = Pattern.compile("\\(" 
        + "(" + statePattern.pattern() + ")" 
        + ";(" + statePattern.pattern() + ")" 
        + ";(" + "\\w" + ")"
        + "\\)");

    public DFAWrapperParser() {
        states = new HashSet<State>();
        transitions = new HashSet<PlannedTransition>();
        endStates = new HashSet<State>();
    }

    protected State findState(String stateName) {
        return this.states.stream().filter(s -> s.getLabel().equals(stateName)).findFirst().get();
    }

    protected PlannedTransition findTransition(PlannedTransition transition) {
        return this.transitions.stream().filter(t -> t.equals(transition)).findFirst().get();
    }

    protected PlannedTransition findTransition(String sourceState, String targetState, char input) {
        return this.transitions.stream().filter(t -> t.getOrigin().getLabel().equals(sourceState)
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

    protected void parseStates(JSONObject dfaInfo) {
        JSONArray stateArray = (JSONArray) dfaInfo.get("states");

        for (Object s : stateArray) {
            this.states.add(this.parseState((String) s));
        }
    }

    protected CharacterTransition parseTransition(String transitionText) {
        Matcher mT = this.transitionPattern.matcher(transitionText);

        if (mT.matches()) {
            return new CharacterTransition(
                this.findState(mT.group(1)),
                mT.group(3).charAt(0),
                this.findState(mT.group(2)));
        }

        return null;
    }

    protected void parseTransitions(JSONObject dfaInfo) {
        JSONArray transitionInfo = (JSONArray) dfaInfo.get("transitions");

        for (Object s : transitionInfo) {
            this.transitions.add(this.parseTransition((String) s));
        }
    }

    protected void parseStartState(JSONObject dfaInfo) {
        Matcher m = this.statePattern.matcher((String) dfaInfo.get("start_state"));

        if (m.matches()) {
            this.startState = this.findState(m.group());
        }
    }

    protected void parseEndStates(JSONObject dfaInfo) {
        JSONArray stateInfo = (JSONArray) dfaInfo.get("end_states");

        for (Object s : stateInfo) {
            Matcher m = this.statePattern.matcher((String) s);
            if (m.matches()) {
                this.endStates.add(this.findState(m.group()));
            }
        }
    }

    protected DFA parseDFA(String DFAFilePath) {
        JSONObject dfaInfo = null;

        try {
            dfaInfo = (JSONObject) JSONValue.parse(new FileReader(DFAFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.parseStates(dfaInfo);
        this.parseTransitions(dfaInfo);
        this.parseStartState(dfaInfo);
        this.parseEndStates(dfaInfo);

        return new DFA(states, transitions, startState, endStates);
    }

    protected Map<PlannedTransition, String> parseTransitionToBinaryMap(String binaryMapFilePath) {
        JSONArray binaryMapArray = null;

        try {
            binaryMapArray = (JSONArray) JSONValue.parse(new FileReader(binaryMapFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Map<PlannedTransition, String> binaryMap = new HashMap<PlannedTransition, String>();

        for (Object o : binaryMapArray) {
            JSONObject castedO = (JSONObject) o;
            CharacterTransition t = this.parseTransition((String) castedO.get("transition"));

            binaryMap.put(this.findTransition(t), (String) castedO.get("binary"));
        }

        return binaryMap;
    }

    protected Collection<ScriptedTransitionEntry> parseTransitionChain(String transitionChainFilePath) {
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

    public DFAWrapper parseDFAWrapper(String DFAFilePath, String binaryMapFilePath, String transitionChainFilePath) {
        return new DFAWrapper(
            this.parseDFA(DFAFilePath), 
            this.parseTransitionToBinaryMap(binaryMapFilePath),
            this.parseTransitionChain(transitionChainFilePath));
    }
}
