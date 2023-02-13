package hwswcosim.swsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import tel.schich.automata.DFA;
import tel.schich.automata.State;
import tel.schich.automata.transition.CharacterTransition;
import tel.schich.automata.transition.PlannedTransition;

public class DFAWrapper {
    private DFA dfa;
    private Map<PlannedTransition, String> binaryMap;
    private ArrayList<PlannedTransition> takenTransitions;

    private State currentState;
    private String currentBinaryPath;

    public DFAWrapper(DFA dfa, Map<PlannedTransition, String> binaryMap) {
        this.dfa = dfa;
        this.binaryMap = binaryMap;
        this.currentState = this.dfa.getStartState();
        this.takenTransitions = new ArrayList<PlannedTransition>();
    }

    public void transition(char input) {
        PlannedTransition transition = this.dfa.getTransitionFor(currentState, input);

        this.currentBinaryPath = this.binaryMap.get(transition);

        this.currentState = dfa.transition(this.currentState, input);
        this.takenTransitions.add(transition);
    }

    public Collection<PlannedTransition> getTakenTransitions() {
        ArrayList<PlannedTransition> result = new ArrayList<PlannedTransition>();
        
        for (PlannedTransition e : this.takenTransitions) {
            result.add(new CharacterTransition(e.getOrigin(), ((CharacterTransition) e).getWith(), e.getDestination()));
        }

        return result;
    }

    public String getCurrentBinaryPath() {
        String binaryPath = this.currentBinaryPath;
        this.currentBinaryPath = null;
        return binaryPath;
    }

    public boolean hasBinaryFilePath() {
        return this.currentBinaryPath != null;
    }

    protected Number translateTime(long time) {
        return Double.valueOf(time);
    }
}
