package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Scanner;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.NoSuchElementException;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Chris Zhan
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        setUp(machine, _input.nextLine());
        while (_input.hasNextLine()) {
            String strInput = _input.nextLine();
            if (Objects.equals(strInput, "")) {
                if (_input.hasNextLine()) {
                    strInput = _input.nextLine();
                    _output.println();
                } else {
                    break;
                }
                if (Objects.equals(strInput, "")) {
                    continue;
                }
            }
            if (strInput.charAt(0) == '*') {
                setUp(machine, strInput);
            } else {
                printMessageLine(machine.convert(strInput));
            }
        }
        _output.println();

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Number of rotors must be an int");
            }
            int numRotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Number of pawls must be an int");
            }
            int numPawls = _config.nextInt();
            HashMap<String, Rotor> rotors = new HashMap<>();
            while (_config.hasNext()) {
                Rotor newRotor = readRotor();
                rotors.put(newRotor.name(), newRotor);
            }
            return new Machine(_alphabet, numRotors, numPawls, rotors.values());
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config.
     * Example: I MQ (AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV)
     *               (JZ) (S)*/
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();
            String rotorInfo = _config.next();
            char rotorType = rotorInfo.charAt(0);
            if (rotorType != 'M' && rotorType != 'N' && rotorType != 'R') {
                throw error("Rotor must be classified as M, N, or R");
            }
            String cycles = "";
            while (_config.hasNext("([(][a-zA-Z0-9&_\\.-]+[)])+")) {
                cycles = cycles + _config.next() + " ";
            }
            Permutation perm = new Permutation(cycles, _alphabet);
            if (rotorType == 'M') {
                return new MovingRotor(rotorName, perm, rotorInfo.substring(1));
            } else if (rotorType == 'N') {
                return new FixedRotor(rotorName, perm);
            } else {
                return new Reflector(rotorName, perm);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment.
     *  Example SETTINGS: * B Beta III IV I AXLE (YF) (ZH) */
    private void setUp(Machine M, String settings) {
        Scanner inSettings = new Scanner(settings);
        String checkAsterisk = inSettings.next();
        if (!Objects.equals(checkAsterisk, "*")) {
            throw error("An asterisk must appear in the first column");
        }

        String[] rotors1 = new String[M.numRotors()];
        for (int i = 0; i < rotors1.length; i += 1) {
            rotors1[i] = inSettings.next();
        }
        M.insertRotors(rotors1);
        String rotorSettings = inSettings.next();
        M.setRotors(rotorSettings);
        if (inSettings.hasNext("\\w+")) {
            String ringstellungSet = inSettings.next();
            M.setRingstellung(ringstellungSet);
        }
        String plugboardSettings = "";
        if (inSettings.hasNext()) {
            plugboardSettings = inSettings.nextLine();
        }
        M.setPlugboard(new Permutation(plugboardSettings, M.alphabet()));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters).
     *  Assumes that MSG is a single line, with no spaces */
    private void printMessageLine(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i += 1) {
            result = result + msg.charAt(i);
            if ((i + 1) % 5 == 0) {
                result = result + " ";
            }
        }
        _output.println(result);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
