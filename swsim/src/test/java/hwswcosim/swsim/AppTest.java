package hwswcosim.swsim;

import static org.junit.Assert.assertTrue;

import org.javasim.Simulation;
import org.junit.Test;

import tel.schich.automata.DFA;
import de.offis.mosaik.api.Simulator;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        Simulation.printQueue();
        System.out.println(Simulator.API_VERSION);
    }
}
