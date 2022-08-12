package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Chris Zhan
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        _setting = 0;
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

    /** My setting, an integer corresponding to alphabet's index. */
    private int _setting;
}
