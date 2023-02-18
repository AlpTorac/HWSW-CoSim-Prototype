package hwswcosim.swsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DFAWrapperTest {
    private final String absPath = new File("").getAbsolutePath();
    private final String dfaFilePath = absPath + "/src/test/resources/dfa.json";
    private final String binaryMapFilePath = absPath + "/src/test/resources/binaryMap.json";
//    private final String transitionChainFilePath = absPath + "/src/test/resources/transitionChain.json";

    private DFAWrapper wrapper;

    @Before
    public void setUp() {
        DFAWrapperParser parser = new DFAWrapperParser();
        this.wrapper = parser.parseDFAWrapper(dfaFilePath, binaryMapFilePath);
    }

    @After
    public void tearDown() {
        this.wrapper = null;
    }

    @Test
    public void transitionTest() {
        assertTrue(this.wrapper.getTakenTransitions().isEmpty());
        assertEquals(null, this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals(null, this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());

        this.wrapper.transition('a');
        assertEquals(1, this.wrapper.getTakenTransitions().size());
        assertTrue(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals("BNP1", this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals(null, this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());

        this.wrapper.transition('a');
        assertEquals(2, this.wrapper.getTakenTransitions().size());
        assertTrue(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals("BNP2", this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals(null, this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());

        this.wrapper.transition('b');
		assertEquals(3, this.wrapper.getTakenTransitions().size());
        assertTrue(this.wrapper.hasBinaryFilePath());
		assertTrue(this.wrapper.hasBinaryArguments());
        assertEquals("BNP5", this.wrapper.getCurrentBinaryPath());
        assertEquals("abc", this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals(null, this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());

        this.wrapper.transition('a');
		assertEquals(4, this.wrapper.getTakenTransitions().size());
        assertTrue(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals("BNP2", this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals(null, this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());

        this.wrapper.transition('d');
		assertEquals(5, this.wrapper.getTakenTransitions().size());
        assertTrue(this.wrapper.hasBinaryFilePath());
		assertTrue(this.wrapper.hasBinaryArguments());
        assertEquals("BNP4", this.wrapper.getCurrentBinaryPath());
        assertEquals("", this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals(null, this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());

        this.wrapper.transition('c');
		assertEquals(6, this.wrapper.getTakenTransitions().size());
        assertTrue(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals("BNP6", this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals(null, this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
    }
}
