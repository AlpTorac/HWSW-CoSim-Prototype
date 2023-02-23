package hwswcosim.swsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.junit.Test;

import tel.schich.automata.DFA;
import tel.schich.automata.NamedState;
import tel.schich.automata.State;
import tel.schich.automata.transition.CharacterTransition;
import tel.schich.automata.transition.PlannedTransition;

/**
 * Unit test for simple App.
 */
public class DFAWrapperParserTest 
{
    private final String absPath = new File("").getAbsolutePath();
    private final String dfaFilePath = absPath + "/src/test/resources/dfa.json";
    private final String binaryMapFilePath = absPath + "/src/test/resources/binaryMap.json";

    private DFAWrapperParser parser = new DFAWrapperParser(new BinaryMapParser(new DFAParser()));

    @SuppressWarnings("unused")
    private DFA makeTestDFA() {
        Set<State> expectedStates = new HashSet<State>();
        Set<PlannedTransition> expectedTransitions = new HashSet<PlannedTransition>();

        State q0 = new NamedState("q0");
        State q1 = new NamedState("q1");
        State q2 = new NamedState("q2");
        State q3 = new NamedState("q3");
        State q4 = new NamedState("q4");

        expectedStates.add(q0);
        expectedStates.add(q1);
        expectedStates.add(q2);
        expectedStates.add(q3);
        expectedStates.add(q4);

        CharacterTransition t1 = new CharacterTransition(q0, 'a', q1);
        CharacterTransition t2 = new CharacterTransition(q1, 'a', q2);
        CharacterTransition t3 = new CharacterTransition(q2, 'a', q3);
        CharacterTransition t4 = new CharacterTransition(q2, 'd', q0);
        CharacterTransition t5 = new CharacterTransition(q2, 'b', q1);
        CharacterTransition t6 = new CharacterTransition(q0, 'c', q4);
        
        expectedTransitions.add(t1);
        expectedTransitions.add(t2);
        expectedTransitions.add(t3);
        expectedTransitions.add(t4);
        expectedTransitions.add(t5);
        expectedTransitions.add(t6);

        Set<State> expectedEndStates = new HashSet<State>();

        expectedEndStates.add(q3);
        expectedEndStates.add(q4);

        return new DFA(expectedStates, expectedTransitions, q0, expectedEndStates);
    }

    @Test
    public void parseDFATest()
    {
        DFA parsedDFA = parser.getBinaryMapParser().getDFAParser().parseDFA(dfaFilePath);

        Set<State> states = parsedDFA.getStates();
        Set<PlannedTransition> transitions = parsedDFA.getTransitions();

        assertEquals(5, states.size());
        assertEquals(6, transitions.size());

        Set<State> parsedStates = parsedDFA.getStates();
        Set<PlannedTransition> parsedTransitions = parsedDFA.getTransitions();

        assertTrue(parsedStates.stream().anyMatch(s -> s.getLabel().equals("q0")));
        assertTrue(parsedStates.stream().anyMatch(s -> s.getLabel().equals("q1")));
        assertTrue(parsedStates.stream().anyMatch(s -> s.getLabel().equals("q2")));
        assertTrue(parsedStates.stream().anyMatch(s -> s.getLabel().equals("q3")));
        assertTrue(parsedStates.stream().anyMatch(s -> s.getLabel().equals("q4")));

        assertTrue(this.transitionSetContains(parsedTransitions, "q0", "q1", 'a'));
        assertTrue(this.transitionSetContains(parsedTransitions, "q1", "q2", 'a'));
        assertTrue(this.transitionSetContains(parsedTransitions, "q2", "q3", 'a'));
        assertTrue(this.transitionSetContains(parsedTransitions, "q2", "q0", 'd'));
        assertTrue(this.transitionSetContains(parsedTransitions, "q2", "q1", 'b'));
        assertTrue(this.transitionSetContains(parsedTransitions, "q0", "q4", 'c'));
    }

    @Test
    public void parseBinaryMapTest() {
        DFA dfa = parser.getBinaryMapParser().getDFAParser().parseDFA(dfaFilePath);

        assertNotNull(dfa);

        Collection<BinaryMapEntry> map = parser.getBinaryMapParser().parseBinaryMap(dfa.getStates(), dfa.getTransitions(), binaryMapFilePath);

        assertEquals(6, map.size());

        assertTrue(this.binaryMapContains(map, "q0", "q1", 'a', "BNP1"));
        assertTrue(this.binaryMapContains(map, "q1", "q2", 'a', "BNP2"));
        assertTrue(this.binaryMapContains(map, "q2", "q3", 'a', "BNP3", (JSONArray) JSONValue.parse("[\"abc\", \"cba\"]")));
        assertTrue(this.binaryMapContains(map, "q2", "q0", 'd', "BNP4", (JSONArray) JSONValue.parse("[]")));
        assertTrue(this.binaryMapContains(map, "q2", "q1", 'b', "BNP5", (JSONArray) JSONValue.parse("[\"abc\"]")));
        assertTrue(this.binaryMapContains(map, "q0", "q4", 'c', "BNP6"));
    }

    private boolean transitionSetContains(Set<PlannedTransition> parsedTransitions, String sourceLabel, String targetLabel, char input) {
        return parsedTransitions.stream().anyMatch(t -> {
            return transitionEquals(((CharacterTransition) t), sourceLabel, targetLabel, input);
        });
    }

    private boolean binaryMapContains(Collection<BinaryMapEntry> map, String sourceLabel, String targetLabel, char input, String binaryPath) {
        return map.stream().anyMatch(e -> {
            CharacterTransition t = (CharacterTransition) e.getTransition();
            String bp = e.getBinaryPath();

                return transitionEquals(t, sourceLabel, targetLabel, input) && bp.equals(binaryPath);
            });
    }
    private boolean binaryMapContains(Collection<BinaryMapEntry> map, String sourceLabel, String targetLabel, char input, String binaryPath, JSONArray binaryArguments) {
        return map.stream().anyMatch(e -> {
            CharacterTransition t = (CharacterTransition) e.getTransition();
            String bp = e.getBinaryPath();
            JSONArray bargs = e.getBinaryArguments();

            boolean bargsEqual = bargs == binaryArguments || (!(bargs == null ^ binaryArguments == null) && bargs.equals(binaryArguments));

                return transitionEquals(t, sourceLabel, targetLabel, input) && bp.equals(binaryPath)
                && bargsEqual;
            });
    }

    private boolean transitionEquals(CharacterTransition t1, String sourceLabel, String targetLabel, char input) {
        return t1.getOrigin().getLabel().equals(sourceLabel)
            && t1.getDestination().getLabel().equals(targetLabel)
            && (t1.getWith() == input);
    }

    @SuppressWarnings("unused")
    private boolean transitionsEqual(CharacterTransition t1, CharacterTransition t2) {
        return t1.getOrigin().getLabel().equals(t2.getOrigin().getLabel())
            && t1.getDestination().getLabel().equals(t2.getDestination().getLabel())
            && (t1.getWith() == t2.getWith());
    }
}
