package enigma;

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Chris Zhan
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        if (pawls < 0 || pawls >= numRotors) {
            throw new EnigmaException("Number of PAWLS must be "
                    + ">= 0 and < NUMROTORS");
        } else {
            _pawls = pawls;
        }
        _allRotors = allRotors;
        _plugboard = new Permutation("", alpha);
        _rotorSlots = new HashMap<Integer, Rotor>();
        _ringStellungSet = false;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotorSlots.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        Iterator<Rotor> allRotorsIterator = _allRotors.iterator();
        HashMap<String, Rotor> allRotorsMap = new HashMap<String, Rotor>();
        while (allRotorsIterator.hasNext()) {
            Rotor temp = allRotorsIterator.next();
            allRotorsMap.put(temp.name(), temp);
        }
        int i = 0;
        int movingRotors = 0;
        while (i < rotors.length) {
            Rotor nextRotor = allRotorsMap.get(rotors[i]);
            if (nextRotor == null) {
                throw new EnigmaException("Rotor name not in "
                        + "allRotors collection");
            }
            if (nextRotor.getClass().getName() == "enigma.MovingRotor") {
                movingRotors += 1;
            }
            _rotorSlots.put(i, allRotorsMap.get(rotors[i]));
            i += 1;
        }
        if (_rotorSlots.size() > numRotors()) {
            throw new EnigmaException("Number of rotors in setting "
                   + "line exceeds number of rotors in machine");
        }
        if (movingRotors != numPawls()) {
            throw new EnigmaException("Incorrect number of MovingRotor");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("setting must be numRotors() - 1");
        }
        for (int i = 0; i < setting.length(); i += 1) {
            if (!alphabet().contains(setting.charAt(i))) {
                throw new EnigmaException("setting must contain "
                        + "characters in my alphabet");
            } else {
                Rotor temp = getRotor(i + 1);
                temp.set(setting.charAt(i));
            }
        }
    }

    /** Set my rotors Ringstellung according to RINGSTELLUNG, which must
     * be a string of numRotors()-1 characters in my alphabet. The first
     * letter refers to the leftmost rotor setting (not counting reflector). */
    void setRingstellung(String ringstellung) {
        if (ringstellung.length() != numRotors() - 1) {
            throw new EnigmaException("ringstellung must be numRotors() - 1");
        }
        for (int i = 0; i < ringstellung.length(); i += 1) {
            if (!alphabet().contains(ringstellung.charAt(i))) {
                throw new EnigmaException("ringstellung must contain "
                        + "characters in my alphabet");
            } else {
                Rotor currentRotor = getRotor(i + 1);
                int currentSetting = currentRotor.setting();
                int newIntSetting = currentRotor.permutation()
                        .wrap(currentSetting - alphabet()
                                .toInt(ringstellung.charAt(i)));
                currentRotor.set(newIntSetting);
                if (currentRotor.getClass().getName()
                        == "enigma.MovingRotor" && !_ringStellungSet) {
                    String newNotches = "";
                    for (int n = 0; n < currentRotor.notches()
                            .length(); n += 1) {
                        int currentNotch = alphabet()
                                .toInt(currentRotor.notches().charAt(n));
                        int newNotch = currentRotor.permutation()
                                .wrap(currentNotch - alphabet()
                                        .toInt(ringstellung.charAt(i)));
                        char newCharNotch = alphabet().toChar(newNotch);
                        newNotches += newCharNotch;
                    }
                    currentRotor.setNotches(newNotches);
                }
            }
        }
        _ringStellungSet = true;
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    void advanceRotors() {
        int i = 0;
        while (i < numRotors()) {
            Rotor currentRotor = getRotor(i);
            Rotor rightRotor = getRotor(i + 1);
            if (i == numRotors() - 1) {
                currentRotor.advance();
                i += 1;
            } else if (rightRotor.atNotch() && currentRotor.rotates()) {
                currentRotor.advance();
                rightRotor.advance();
                i += 2;
            } else {
                i += 1;
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int f = numRotors() - 1;
        int result = c;
        while (f >= 0) {
            Rotor currentRotor = getRotor(f);
            result = currentRotor.convertForward(result);
            f -= 1;
        }
        int r = 1;
        while (r < numRotors()) {
            Rotor currentRotor = getRotor(r);
            result = currentRotor.convertBackward(result);
            r += 1;
        }
        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i += 1) {
            if (msg.charAt(i) != ' ') {
                int intNewChar = convert(alphabet().toInt(msg.charAt(i)));
                char newChar = alphabet().toChar(intNewChar);
                result = result + newChar;
            }
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** The number of rotors I have. */
    private int _numRotors;
    /** The number of pawls I have. */
    private int _pawls;
    /** The collection of all rotors from config. */
    private Collection<Rotor> _allRotors;
    /** My plugboard permutation. */
    private Permutation _plugboard;
    /** The rotors I have in my machine. */
    private HashMap<Integer, Rotor> _rotorSlots;
    /** Whether I've already set my ringstellung. */
    private boolean _ringStellungSet;
}
