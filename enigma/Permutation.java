package enigma;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Chris Zhan
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = new ArrayList<>();
        Matcher cyclePattern = Pattern.compile("([a-zA-Z0-9&_\\.-]+)")
                .matcher(cycles);
        while (cyclePattern.find()) {
            _cycles.add(cyclePattern.group());
        }
        for (int i = 0; i < _cycles.size(); i += 1) {
            for (int c = 0; c < _cycles.get(i).length(); c += 1) {
                if (!alphabet().contains(_cycles.get(i).charAt(c))) {
                    throw new EnigmaException("Cycles contains a +"
                            + "character not found in alphabet");
                }
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char oneChar = alphabet().toChar(wrap(p));
        for (int c = 0; c < _cycles.size(); c += 1) {
            for (int r = 0; r < _cycles.get(c).length(); r += 1) {
                if (oneChar == _cycles.get(c).charAt(r)) {
                    if (r == _cycles.get(c).length() - 1) {
                        char permuteChar = _cycles.get(c).charAt(0);
                        int permuteInt = alphabet().toInt(permuteChar);
                        return permuteInt;
                    } else {
                        char permuteChar = _cycles.get(c).charAt(r + 1);
                        int permuteInt = alphabet().toInt(permuteChar);
                        return permuteInt;
                    }
                }
            }

        }
        return wrap(p);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char oneChar = _alphabet.toChar(wrap(c));
        for (int d = 0; d < _cycles.size(); d += 1) {
            for (int r = 0; r < _cycles.get(d).length(); r += 1) {
                if (oneChar == _cycles.get(d).charAt(r)) {
                    if (oneChar == _cycles.get(d).charAt(0)) {
                        char permuteChar = _cycles.get(d)
                                .charAt(_cycles.get(d).length() - 1);
                        int permuteInt = _alphabet.toInt(permuteChar);
                        return permuteInt;
                    } else {
                        char permuteChar = _cycles.get(d).charAt(r - 1);
                        int permuteInt = _alphabet.toInt(permuteChar);
                        return permuteInt;
                    }
                }
            }

        }
        return wrap(c);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            throw new EnigmaException("Char not in alphabet");
        }
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw new EnigmaException("Char not in alphabet");
        }
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** The cycles that represent this permutation. */
    private ArrayList<String> _cycles;

}
