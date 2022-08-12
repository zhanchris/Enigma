package enigma;

import java.util.HashMap;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Chris Zhan
 */
public class MachineTest {

    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTS ***** */

    private static final Alphabet AZ = new Alphabet(TestUtils.UPPER_STRING);

    private static final HashMap<String, Rotor> ROTORS = new HashMap<>();

    static {
        HashMap<String, String> nav = TestUtils.NAVALA;
        ROTORS.put("B", new Reflector("B", new Permutation(nav.get("B"), AZ)));
        ROTORS.put("Beta",
                new FixedRotor("Beta",
                        new Permutation(nav.get("Beta"), AZ)));
        ROTORS.put("III",
                new MovingRotor("III",
                        new Permutation(nav.get("III"), AZ), "V"));
        ROTORS.put("IV",
                new MovingRotor("IV", new Permutation(nav.get("IV"), AZ),
                        "J"));
        ROTORS.put("I",
                new MovingRotor("I", new Permutation(nav.get("I"), AZ),
                        "Q"));
    }

    private static final String[] ROTORS1 = {"B", "Beta", "III", "IV", "I"};
    private static final String SETTING1 = "AXLE";

    private Machine mach1() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        mach.setRotors(SETTING1);
        return mach;
    }

    @Test
    public void testInsertRotors() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        assertEquals(5, mach.numRotors());
        assertEquals(3, mach.numPawls());
        assertEquals(AZ, mach.alphabet());
        assertEquals(ROTORS.get("B"), mach.getRotor(0));
        assertEquals(ROTORS.get("Beta"), mach.getRotor(1));
        assertEquals(ROTORS.get("III"), mach.getRotor(2));
        assertEquals(ROTORS.get("IV"), mach.getRotor(3));
        assertEquals(ROTORS.get("I"), mach.getRotor(4));
    }

    @Test
    public void testConvertChar() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(YF) (HZ)", AZ));
        assertEquals(25, mach.convert(24));
    }

    @Test
    public void testConvertMsg() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", AZ));
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                mach.convert("FROMHISSHOULDERHIAWATHA"));
    }

    @Test
    public void testSetRotors() {
        Machine mach = mach1();
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(1).setting());
        assertEquals(mach.alphabet().toInt('X'), mach.getRotor(2).setting());
        assertEquals(mach.alphabet().toInt('L'), mach.getRotor(3).setting());
        assertEquals(mach.alphabet().toInt('E'), mach.getRotor(4).setting());
    }

    @Test(expected = EnigmaException.class)
    public void testSetRotorsNotUpdatingEveryRotor() {
        Machine mach = mach1();
        mach.setRotors("Z");
    }

    @Test(expected = EnigmaException.class)
    public void testSetRotorsNotInAlphabet() {
        Machine mach = new Machine(new Alphabet("ABCD"), 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        mach.setRotors("ABCE");
    }

    private static final Alphabet BASICABC = new Alphabet("ABC");
    private static final HashMap<String, Rotor> BASICROTORS = new HashMap<>();

    static {
        BASICROTORS.put("1", new Reflector("1", new Permutation("", BASICABC)));
        BASICROTORS.put("2", new MovingRotor("2",
                new Permutation("", BASICABC), "C"));
        BASICROTORS.put("3", new MovingRotor("3",
                new Permutation("", BASICABC), "C"));
        BASICROTORS.put("4", new MovingRotor("4",
                new Permutation("", BASICABC), "C"));
    }

    public static Machine simple() {
        Machine simple = new Machine(BASICABC, 4, 3, BASICROTORS.values());
        String[] basicRotorsString = {"1", "2", "3", "4"};
        simple.insertRotors(basicRotorsString);
        return simple;
    }

    @Test
    public void testAdvanceRotors() {
        Machine mach = simple();
        mach.advanceRotors();
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(0).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(1).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(2).setting());
        assertEquals(mach.alphabet().toInt('B'), mach.getRotor(3).setting());
        mach.advanceRotors();
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(0).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(1).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(2).setting());
        assertEquals(mach.alphabet().toInt('C'), mach.getRotor(3).setting());
        mach.advanceRotors();
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(0).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(1).setting());
        assertEquals(mach.alphabet().toInt('B'), mach.getRotor(2).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(3).setting());
        mach.advanceRotors();
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(0).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(1).setting());
        assertEquals(mach.alphabet().toInt('B'), mach.getRotor(2).setting());
        assertEquals(mach.alphabet().toInt('B'), mach.getRotor(3).setting());
        mach.advanceRotors();
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(0).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(1).setting());
        assertEquals(mach.alphabet().toInt('B'), mach.getRotor(2).setting());
        assertEquals(mach.alphabet().toInt('C'), mach.getRotor(3).setting());
        mach.advanceRotors();
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(0).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(1).setting());
        assertEquals(mach.alphabet().toInt('C'), mach.getRotor(2).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(3).setting());
        mach.advanceRotors();
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(0).setting());
        assertEquals(mach.alphabet().toInt('B'), mach.getRotor(1).setting());
        assertEquals(mach.alphabet().toInt('A'), mach.getRotor(2).setting());
        assertEquals(mach.alphabet().toInt('B'), mach.getRotor(3).setting());
    }
}

