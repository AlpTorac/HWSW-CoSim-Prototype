package hwswcosim.swsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import tel.schich.automata.DFA;
import tel.schich.automata.State;
import tel.schich.automata.transition.PlannedTransition;

public class DFAWrapper {
    private DFA dfa;
    private Map<PlannedTransition, String> binaryMap;

    private Collection<ScriptedTransitionEntry> transitionChain;
    private ArrayList<ScriptedTransitionEntry> takenTransitions;

    private State currentState;
    private String currentBinaryPath;

    public DFAWrapper(DFA dfa, Map<PlannedTransition, String> binaryMap, Collection<ScriptedTransitionEntry> transitionChain) {
        this.dfa = dfa;
        this.binaryMap = binaryMap;
        this.currentState = this.dfa.getStartState();
        this.transitionChain = transitionChain;
        this.takenTransitions = new ArrayList<ScriptedTransitionEntry>();
    }

    public void step(long time) {
        Optional<ScriptedTransitionEntry> transition = this.transitionChain.stream()
            .filter(t -> {return t.time.doubleValue() == translateTime(time).doubleValue();})
            .findFirst();

        if (transition.isPresent()) {
            ScriptedTransitionEntry t = transition.get();
            Character input = t.input;

            this.currentBinaryPath = this.binaryMap.get(this.dfa.getTransitionFor(this.currentState, input));

            this.currentState = dfa.transition(this.currentState, input.charValue());
            this.takenTransitions.add(t);
        }
    }

    public Collection<ScriptedTransitionEntry> getTakenTransitions() {
        ArrayList<ScriptedTransitionEntry> result = new ArrayList<ScriptedTransitionEntry>();
        
        for (ScriptedTransitionEntry e : this.takenTransitions) {
            result.add(e.clone());
        }

        return result;
    }

    public String getCurrentBinaryPath() {
        String binaryPath = this.currentBinaryPath;
        this.currentBinaryPath = "";
        return binaryPath;
    }

    protected Number translateTime(long time) {
        return Double.valueOf(time);
    }
}
