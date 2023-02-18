package hwswcosim.swsim;

import tel.schich.automata.transition.PlannedTransition;

public class BinaryMapEntry {
    public final PlannedTransition transition;
    public final String binaryPath;
    public final String binaryArguments;

    public BinaryMapEntry(PlannedTransition transition, String binaryPath,
    String binaryArguments) {
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
}
