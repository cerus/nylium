package dev.cerus.nylium.server.block.states;

import dev.cerus.nylium.server.block.Block;
import dev.cerus.nylium.server.key.NamespacedKey;
import java.util.Objects;

public class IdentifiableState {

    private final NamespacedKey identifier;
    private final State state;

    public IdentifiableState(final NamespacedKey identifier, final State state) {
        this.identifier = identifier;
        this.state = state;
    }

    public static IdentifiableState of(final Block block) {
        return new IdentifiableState(block.getType(), block.getState());
    }

    public static IdentifiableState of(final NamespacedKey type, final State state) {
        return new IdentifiableState(type, state);
    }

    public NamespacedKey getIdentifier() {
        return this.identifier;
    }

    public State getState() {
        return this.state;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final IdentifiableState that = (IdentifiableState) o;
        return Objects.equals(this.identifier, that.identifier) && Objects.equals(this.state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identifier, this.state);
    }

}
