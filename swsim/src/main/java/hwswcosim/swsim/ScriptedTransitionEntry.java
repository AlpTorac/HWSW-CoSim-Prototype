package hwswcosim.swsim;

/**
 * This class contains an input that will be used to trigger a transition
 * and the time point, at which the said transition will take place. It is
 * meant to be used by {@link SoftwareSimulationController}.
 */
public class ScriptedTransitionEntry implements Cloneable {
    private final Character input;
    private final Number time;

    /**
     * Constructs an instance of this class.
     * 
     * @param input A given input {@link Character} != null
     * @param time A given {@link Number} != null
     */
    public ScriptedTransitionEntry(Character input, Number time) {
        this.input = input;
        this.time = time;
    }

    /**
     * Check if "o" is another instance of this class that
     * has the same members {@link #input} and {@link #time}.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ScriptedTransitionEntry)) {
            return false;
        }

        ScriptedTransitionEntry castedO = (ScriptedTransitionEntry) o;
        return castedO.input.charValue() == this.input.charValue() &&
            castedO.time.doubleValue() == this.time.doubleValue();
    }

    @Override
    public String toString() {
        return "input: " + this.input + ", time: " + this.time.doubleValue();
    }

    /**
     * Returns a deep copy of the calling instance.
     */
    @Override
    public ScriptedTransitionEntry clone() {
        return new ScriptedTransitionEntry(Character.valueOf(this.input.charValue()), Double.valueOf(this.time.doubleValue()));
    }

    public Character getInput() {
        return this.input;
    }

    public Number getTime() {
        return this.time;
    }
}
