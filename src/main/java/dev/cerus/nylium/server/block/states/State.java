package dev.cerus.nylium.server.block.states;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class State {

    private final Map<String, Set<String>> allowedValues;
    private final Map<String, String> properties;
    private final boolean def;

    public State(final Map<String, Set<String>> allowedValues, final Map<String, String> properties, final boolean def) {
        this.allowedValues = allowedValues;
        this.properties = properties;
        this.def = def;
    }

    public String get(final String key) {
        return this.properties.get(key);
    }

    public boolean getBool(final String key) {
        return Boolean.parseBoolean(this.get(key));
    }

    public int getInt(final String key) {
        return Integer.parseInt(this.get(key));
    }

    public long getLong(final String key) {
        return Long.parseLong(this.get(key));
    }

    public double getDouble(final String key) {
        return Double.parseDouble(this.get(key));
    }

    public float getFloat(final String key) {
        return Float.parseFloat(this.get(key));
    }

    public byte getByte(final String key) {
        return Byte.parseByte(this.get(key));
    }

    public short getShort(final String key) {
        return Short.parseShort(this.get(key));
    }

    public void set(final String key, final Object o) {
        final String value = o.toString();
        if (!this.allowedValues.containsKey(key)) {
            return;
        }
        if (!this.allowedValues.get(key).contains(value)) {
            return;
        }
        this.properties.put(key, value);
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public boolean isDefault() {
        return this.def;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final State state = (State) o;
        return this.def == state.def && Objects.equals(this.allowedValues, state.allowedValues) && Objects.equals(this.properties, state.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.allowedValues, this.properties, this.def);
    }

}
