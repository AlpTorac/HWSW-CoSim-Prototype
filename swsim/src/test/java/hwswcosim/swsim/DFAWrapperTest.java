package hwswcosim.swsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DFAWrapperTest {
    private final String absPath = new File("").getAbsolutePath();
    private final String resourceFolderPath = absPath + "/src/test/resources";

    private final String dfaFileName = "dfa.json";
    private final String binaryMapFileName = "binaryMap.json";

    private DFAWrapper wrapper;

    @Before
    public void setUp() {
        DFAWrapperParser parser = new DFAWrapperParser(new BinaryMapParser(new DFAParser()));
        this.wrapper = parser.parseDFAWrapper(resourceFolderPath, dfaFileName, binaryMapFileName);
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
        assertEquals(resourceFolderPath+"/"+"BNP1", this.wrapper.getCurrentBinaryPath());
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
        assertEquals(resourceFolderPath+"/"+"BNP2", this.wrapper.getCurrentBinaryPath());
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
        assertEquals(resourceFolderPath+"/"+"BNP5", this.wrapper.getCurrentBinaryPath());
        assertEquals((JSONArray) JSONValue.parse("[\"abc\"]"), this.wrapper.getCurrentBinaryArguments());
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
        assertEquals(resourceFolderPath+"/"+"BNP2", this.wrapper.getCurrentBinaryPath());
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
        assertEquals(resourceFolderPath+"/"+"BNP4", this.wrapper.getCurrentBinaryPath());
        assertEquals(JSONValue.parse("[]"), this.wrapper.getCurrentBinaryArguments());
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
        assertEquals(resourceFolderPath+"/"+"BNP6", this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
        assertEquals(null, this.wrapper.getCurrentBinaryPath());
        assertEquals(null, this.wrapper.getCurrentBinaryArguments());
        assertFalse(this.wrapper.hasBinaryFilePath());
		assertFalse(this.wrapper.hasBinaryArguments());
    }
}
