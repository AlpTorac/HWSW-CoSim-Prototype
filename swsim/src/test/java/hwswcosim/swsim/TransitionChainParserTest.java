package hwswcosim.swsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

public class TransitionChainParserTest {
    private final String absPath = new File("").getAbsolutePath();
    private final String transitionChainFilePath = absPath + "/src/test/resources/transitionChain.json";

    @Test
    public void parseTransitionChainTest() {
        TransitionChainParser parser = new TransitionChainParser();
        Collection<ScriptedTransitionEntry> chain = parser.parseTransitionChain(transitionChainFilePath);

        assertEquals(chain.size(), 6);

        assertTrue(this.transitionChainContains(chain, 'a', Double.valueOf(1)));
        assertTrue(this.transitionChainContains(chain, 'a', Double.valueOf(2)));
        assertTrue(this.transitionChainContains(chain, 'b', Double.valueOf(3)));
        assertTrue(this.transitionChainContains(chain, 'a', Double.valueOf(4)));
        assertTrue(this.transitionChainContains(chain, 'd', Double.valueOf(5)));
        assertTrue(this.transitionChainContains(chain, 'c', Double.valueOf(6)));
    }

    private boolean transitionChainContains(Collection<ScriptedTransitionEntry> transitionChain, char input, Number time) {
        return transitionChain.stream().anyMatch(e -> e.input.charValue() == input && e.time.doubleValue() == time.doubleValue());
    }
}