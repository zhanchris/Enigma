package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Chris Zhan
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
        _setting = 0;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        int result = (setting() + 1) % alphabet().size();
        set(result);
    }
    @Override
    int convertForward(int p) {

        int inputAddSetting = p + setting();
        int permute = permutation().permute(inputAddSetting);
        int result = permute - setting();
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return permutation().wrap(result);
    }
    @Override
    int convertBackward(int e) {
        int inputAddSetting = e + setting();
        int permute = permutation().invert(inputAddSetting);
        int result = permute - setting();
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return permutation().wrap(result);
    }

    @Override
    void setNotches(String notches) {
        _notches = notches;
    }

    @Override
    String notches() {
        return _notches;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i += 1) {
            if (alphabet().toChar(setting()) == _notches.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    /** Notches in this rotor. */
    private String _notches;
    /** My current setting, as an alphabet index. */
    private int _setting;
}
