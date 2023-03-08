package hwswcosim.swsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;

import tel.schich.automata.DFA;
import tel.schich.automata.State;
import tel.schich.automata.transition.CharacterTransition;
import tel.schich.automata.transition.PlannedTransition;

/**
 * This class encapsulates a {@link DFA}, a binary map in form of
 * <code>Collection<BinaryMapEntry></code> and represents the control
 * flow of software, which will be used in the simulation.
 */
public class DFAWrapper {
    private DFA dfa;
    private Collection<BinaryMapEntry> binaryMap;
    /**
     * A {@link List} of {@link PlannedTransition}, which were taken so far
     * into the simulation.
     */
    private List<PlannedTransition> takenTransitions;

    private State currentState;
    private String currentBinaryPath;
    private JSONArray currentBinaryArguments;

    public DFAWrapper(DFA dfa, Collection<BinaryMapEntry> binaryMap) {
        this.dfa = dfa;
        this.binaryMap = binaryMap;
        this.currentState = this.dfa.getStartState();
        this.takenTransitions = new ArrayList<PlannedTransition>();
    }

    /**
     * Performs the transition with the given input from the state {@link #currentState}
     * 
     * @param input A given char as input for {@link #dfa}
     */
    public void transition(char input) {
        PlannedTransition transition = this.dfa.getTransitionFor(currentState, input);

        BinaryMapEntry entry = this.getBinaryMapEntry(transition);

        if (entry != null) {
            this.currentBinaryPath = entry.getBinaryPath();
            this.currentBinaryArguments = entry.getBinaryArguments();

            this.currentState = dfa.transition(this.currentState, input);
            this.takenTransitions.add(transition);
        }
    }

    /**
     * @return A deep copy of {@link #takenTransitions}
     */
    public Collection<PlannedTransition> getTakenTransitions() {
        ArrayList<PlannedTransition> result = new ArrayList<PlannedTransition>();
        
        for (PlannedTransition e : this.takenTransitions) {
            result.add(new CharacterTransition(e.getOrigin(), ((CharacterTransition) e).getWith(), e.getDestination()));
        }

        return result;
    }

    /**
     * @return {@link #currentBinaryPath} and set it to null.
     */
    public String getCurrentBinaryPath() {
        String binaryPath = this.currentBinaryPath;
        this.currentBinaryPath = null;
        return binaryPath;
    }

    /**
     * @return {@link #currentBinaryArguments} and set it to null.
     */
    public JSONArray getCurrentBinaryArguments() {
        JSONArray binaryArguments = this.currentBinaryArguments;
        this.currentBinaryArguments = null;
        return binaryArguments;
    }

    /**
     * @return True, if {@link #currentBinaryArguments} != null
     */
    public boolean hasBinaryArguments() {
        return this.currentBinaryArguments != null;
    }

    /**
     * @return True, if {@link #currentBinaryPath} != null
     */
    public boolean hasBinaryFilePath() {
        return this.currentBinaryPath != null;
    }

    /**
     * A helper method that returns the {@link BinaryMapEntry} that contains the given "transition".
     */
    private BinaryMapEntry getBinaryMapEntry(PlannedTransition transition) {
        Optional<BinaryMapEntry> entry = this.binaryMap.stream().filter(e -> e.getTransition().equals(transition)).findFirst();

        if (entry.isPresent()) {
            return entry.get();
        } else {
            return null;
        }
    }
}
