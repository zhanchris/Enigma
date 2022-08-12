package enigma;

import org.junit.Test;
import ucb.junit.textui;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the enigma package.
 *  @author Chris Zhan
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("checkpoint")) {
            System.exit(textui.runClasses(PermutationTest.class,
                    MovingRotorTest.class));
        }
        System.exit(textui.runClasses(PermutationTest.class,
                MovingRotorTest.class,
                MachineTest.class));
    }

    @Test(expected = EnigmaException.class)
    public void testDistinctAlphabet() {
        String alpha = "ABBCDEFGH";
        Alphabet test = new Alphabet(alpha);
    }

    @Test
    public void testAlphabetSize() {
        Alphabet alpha = new Alphabet("ABCDEFG");
        assertEquals(7, alpha.size());
    }
}
