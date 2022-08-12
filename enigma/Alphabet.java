package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Chris Zhan
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
        if (_chars.length() != _chars.chars().distinct().count()) {
            throw new EnigmaException("Duplicate character in alphabet "
                   + "detected");
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return chars().length();
    }

    /** Returns the characters of this alphabet. */
    String chars() {
        return _chars;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        int i = 0;
        while (i < size()) {
            if (_chars.charAt(i) == ch) {
                return true;
            }
            i += 1;
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        int r = index % size();
        if (r < 0) {
            r += size();
        }
        return _chars.charAt(r);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int i = 0;
        while (i < size()) {
            if (_chars.charAt(i) == ch) {
                return i;
            }
            i += 1;
        }
        return i;
    }
    /** The characters of this alphabet. */
    private String _chars;
}
