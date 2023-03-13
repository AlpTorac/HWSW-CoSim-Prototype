package hwswcosim.swsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.javasim.Simulation;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

@Ignore
@FixMethodOrder
public class SoftwareSimulationControllerTest {
    
    private final String absPath = new File("").getAbsolutePath();
    private final String resourceFolderPath = absPath + "/src/test/resources";

    private final String dfaFileName = "dfa.json";
    private final String binaryMapFileName = "binaryMap.json";
    private final String transitionChainFileName = "transitionChain.json";

    private SoftwareSimulationController controller;

    private volatile boolean hasLaggerStarted = false;
    private volatile boolean hasControllerStarted = false;
    private volatile boolean isControllerDone = false;

    @Rule
    public Timeout testTimeout = new Timeout(5000);
    
    @Before
    public void setUp() {
        this.hasLaggerStarted = false;
        this.hasControllerStarted = false;
        this.isControllerDone = false;

        this.controller = new SoftwareSimulationController();
        this.controller.initSoftwareSimulation(resourceFolderPath, dfaFileName, binaryMapFileName, transitionChainFileName);
    }

    @After
    public void tearDown() {
        try {
            Simulation.reset();
        } catch (final Throwable e) {
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void terminationTest() {
        while (!this.controller.isSimulationTerminated()) {
            this.controller.step();
        }
    }

    @Test
    public void workflowTest() {
        assertEquals(6, this.controller.getRemainingTransitionChain().size());
        assertEquals(null, this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());

        this.controller.step();
        assertEquals(5, this.controller.getRemainingTransitionChain().size());
        assertTrue(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(resourceFolderPath+"/"+"BNP1", this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(null, this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());

        this.controller.step();
        assertEquals(4, this.controller.getRemainingTransitionChain().size());
        assertTrue(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(resourceFolderPath+"/"+"BNP2", this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(null, this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());

        this.controller.step();
		assertEquals(3, this.controller.getRemainingTransitionChain().size());
        assertTrue(this.controller.hasBinaryFilePath());
		assertTrue(this.controller.hasBinaryArguments());
        assertEquals(resourceFolderPath+"/"+"BNP5", this.controller.getBinaryFilePath());
        assertEquals((JSONArray) JSONValue.parse("[\"abc\"]"), this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(null, this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());

        this.controller.step();
		assertEquals(2, this.controller.getRemainingTransitionChain().size());
        assertTrue(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(resourceFolderPath+"/"+"BNP2", this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(null, this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());

        this.controller.step();
		assertEquals(1, this.controller.getRemainingTransitionChain().size());
        assertTrue(this.controller.hasBinaryFilePath());
		assertTrue(this.controller.hasBinaryArguments());
        assertEquals(resourceFolderPath+"/"+"BNP4", this.controller.getBinaryFilePath());
        assertEquals(JSONValue.parse("[]"), this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(null, this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());

        this.controller.step();
		assertEquals(0, this.controller.getRemainingTransitionChain().size());
        assertTrue(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(resourceFolderPath+"/"+"BNP6", this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
        assertEquals(null, this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());

        this.controller.step();
        assertEquals(0, this.controller.getRemainingTransitionChain().size());
        assertEquals(null, this.controller.getBinaryFilePath());
        assertEquals(null, this.controller.getBinaryArguments());
        assertFalse(this.controller.hasBinaryFilePath());
		assertFalse(this.controller.hasBinaryArguments());
    }

    @Ignore("Can take long to compute")
    @Test
    public void laggyWorkflowTest() {
        int laggerCount = Runtime.getRuntime().availableProcessors() * 8;
        ExecutorService pool = Executors.newFixedThreadPool(laggerCount+1);

        Thread controllerThread = new Thread() {
            @Override
            public void run() {
                hasControllerStarted = true;
                //System.out.println("controller waiting");
                while (!hasLaggerStarted) {

                }
                //System.out.println("controller running");
                try {
                    workflowTest();
                } catch (AssertionError e) {
                    fail("Assertion error caught: " + e.getMessage());
                }
                //System.out.println("controller is done");
                isControllerDone = true;
            }
        };

        for (int i = 0; i < laggerCount; i++) {
            pool.execute(new Thread() {
                @Override
                public void run() {
                    //System.out.println("lagger waiting");
                    while (!hasControllerStarted) {

                    }
                    //System.out.println("lagger started");
                    while (!isControllerDone) {
                        hasLaggerStarted = true;
    
                        // Stall controllerThread by jamming the executor with
                        // IO operations
                        FileReader r;
                        Stream<String> lines = null;
                        try {
                            r = new FileReader(resourceFolderPath + "/" + binaryMapFileName);
                            BufferedReader br = new BufferedReader(r);
                            lines = br.lines();
                            br.close();
                        } catch (IOException e) {
                            if (lines == null) {
                                e.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                    }
                    //System.out.println("lagger is done");
                }
            });
        }

        pool.execute(controllerThread);

        while (!isControllerDone) {

        }

        pool.shutdown();

        //System.out.println("Workflow has been jammed by " + laggerCount + " lagging threads");
    }
}
