package hwswcosim.swsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DFAWrapperTest {
    private final String absPath = new File("").getAbsolutePath();
    private final String dfaFilePath = absPath + "/src/test/resources/dfa.json";
    private final String binaryMapFilePath = absPath + "/src/test/resources/binaryMap.json";
    private final String transitionChainFilePath = absPath + "/src/test/resources/transitionChain.json";

    private DFAWrapper wrapper;

    @Before
    public void setUp() {
        DFAWrapperParser parser = new DFAWrapperParser();
        this.wrapper = parser.parseDFAWrapper(dfaFilePath, binaryMapFilePath, transitionChainFilePath);
    }

    @After
    public void tearDown() {
        this.wrapper = null;
    }

    @Test
    public void stepTest() {
        assertTrue(this.wrapper.getTakenTransitions().isEmpty());
        this.wrapper.step(0);
        assertTrue(this.wrapper.getTakenTransitions().isEmpty());

        this.wrapper.step(1);
        assertEquals(1, this.wrapper.getTakenTransitions().size());
        assertEquals("BNP1", this.wrapper.getCurrentBinaryPath());
        assertEquals("", this.wrapper.getCurrentBinaryPath());

        this.wrapper.step(2);
        assertEquals(2, this.wrapper.getTakenTransitions().size());
        assertEquals("BNP2", this.wrapper.getCurrentBinaryPath());
        assertEquals("", this.wrapper.getCurrentBinaryPath());

        this.wrapper.step(3);
		assertEquals(3, this.wrapper.getTakenTransitions().size());
        assertEquals("BNP5", this.wrapper.getCurrentBinaryPath());
        assertEquals("", this.wrapper.getCurrentBinaryPath());

        this.wrapper.step(4);
		assertEquals(4, this.wrapper.getTakenTransitions().size());
        assertEquals("BNP2", this.wrapper.getCurrentBinaryPath());
        assertEquals("", this.wrapper.getCurrentBinaryPath());

        this.wrapper.step(5);
		assertEquals(5, this.wrapper.getTakenTransitions().size());
        assertEquals("BNP4", this.wrapper.getCurrentBinaryPath());
        assertEquals("", this.wrapper.getCurrentBinaryPath());

        this.wrapper.step(6);
		assertEquals(6, this.wrapper.getTakenTransitions().size());
        assertEquals("BNP6", this.wrapper.getCurrentBinaryPath());
        assertEquals("", this.wrapper.getCurrentBinaryPath());
    }
}
