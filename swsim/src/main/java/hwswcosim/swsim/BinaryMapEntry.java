package hwswcosim.swsim;

import org.json.simple.JSONArray;

import tel.schich.automata.transition.PlannedTransition;

public class BinaryMapEntry {
    private final PlannedTransition transition;
    private final String binaryPath;
    private final JSONArray binaryArguments;

    public BinaryMapEntry(PlannedTransition transition, String binaryPath,
    JSONArray binaryArguments) {
        this.transition = transition;
        this.binaryPath = binaryPath;
        this.binaryArguments = binaryArguments;
    }

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
