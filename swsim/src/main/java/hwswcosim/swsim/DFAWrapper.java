package hwswcosim.swsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import tel.schich.automata.DFA;
import tel.schich.automata.State;
import tel.schich.automata.transition.CharacterTransition;
import tel.schich.automata.transition.PlannedTransition;

public class DFAWrapper {
    private DFA dfa;
    private Collection<BinaryMapEntry> binaryMap;
    private ArrayList<PlannedTransition> takenTransitions;

    private State currentState;
    private String currentBinaryPath;
    private String currentBinaryArguments;

    public DFAWrapper(DFA dfa, Collection<BinaryMapEntry> binaryMap) {
        this.dfa = dfa;
        this.binaryMap = binaryMap;
        this.currentState = this.dfa.getStartState();
        this.takenTransitions = new ArrayList<PlannedTransition>();
    }

    public void transition(char input) {
        PlannedTransition transition = this.dfa.getTransitionFor(currentState, input);

        BinaryMapEntry entry = this.getBinaryMapEntry(transition);

        if (entry != null) {
            this.currentBinaryPath = entry.binaryPath;
            this.currentBinaryArguments = entry.binaryArguments;

            this.currentState = dfa.transition(this.currentState, input);
            this.takenTransitions.add(transition);
        }
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

    public String getCurrentBinaryArguments() {
        String binaryArguments = this.currentBinaryArguments;
        this.currentBinaryArguments = null;
        return binaryArguments;
    }

    public boolean hasBinaryArguments() {
        return this.currentBinaryArguments != null;
    }

    public boolean hasBinaryFilePath() {
        return this.currentBinaryPath != null;
    }

    protected Number translateTime(long time) {
        return Double.valueOf(time);
    }

    private BinaryMapEntry getBinaryMapEntry(PlannedTransition transition) {
        Optional<BinaryMapEntry> entry = this.binaryMap.stream().filter(e -> e.transition.equals(transition)).findFirst();

        if (entry.isPresent()) {
            return entry.get();
        } else {
            return null;
        }
    }
}
