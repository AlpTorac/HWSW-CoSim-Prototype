package hwswcosim.swsim;

public class ScriptedTransitionEntry {
    private final Character input;
    private final Number time;

    public ScriptedTransitionEntry(Character input, Number time) {
        this.input = input;
        this.time = time;
    }

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

    public Character getInput() {
        return this.input;
    }

    public Number getTime() {
        return this.time;
    }
}
