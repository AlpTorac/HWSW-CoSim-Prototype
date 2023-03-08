package hwswcosim.swsim;

import org.json.simple.JSONArray;

import tel.schich.automata.transition.PlannedTransition;
/**
 * Instances of this class represent map entries, whose keys
 * are {@link #transition} and value pairs are
 * ({@link #binaryPath}, {@link #binaryArguments}).
 */
public class BinaryMapEntry {
    private final PlannedTransition transition;
    private final String binaryPath;
    private final JSONArray binaryArguments;

    /**
     * Constructs an instance of this class with the given parameters.
     * 
     * @param transition A {@link PlannedTransition} != null
     * @param binaryPath The absolute path to a binary != null
     * @param binaryArguments The runtime arguments of the binary at "binaryPath". Can be null.
     */
    public BinaryMapEntry(PlannedTransition transition, String binaryPath,
    JSONArray binaryArguments) {
        this.transition = transition;
        this.binaryPath = binaryPath;
        this.binaryArguments = binaryArguments;
    }

    /**
     * Checks whether "o" and this instance contain the same members
     * {@link #transition}, {@link #binaryPath} and {@link #binaryArguments}.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof BinaryMapEntry)) {
            return false;
        }

        BinaryMapEntry castedO = (BinaryMapEntry) o;

        if (this.binaryArguments == null && castedO.binaryArguments == null) {
            return castedO.transition == this.transition &&
            castedO.binaryPath.equals(this.binaryPath);
        }
        else if (this.binaryArguments == null ^ castedO.binaryArguments == null) {
            return false;
        }
        else {
            return castedO.transition.equals(this.transition) &&
            castedO.binaryPath.equals(this.binaryPath) &&
            castedO.binaryArguments.equals(this.binaryArguments);
        }
    }

    public PlannedTransition getTransition() {
        return this.transition;
    }

    public String getBinaryPath() {
        return this.binaryPath;
    }

    public JSONArray getBinaryArguments() {
        return this.binaryArguments;
    }
}
