package dev.cerus.nylium.server.block;

import dev.cerus.nylium.server.block.states.State;
import dev.cerus.nylium.server.key.NamespacedKey;

public class Block {

    private NamespacedKey type;
    private State state;

    public State getState() {
        return this.state;
    }

    private void setState(final State state) {
        this.state = state;
    }

    public NamespacedKey getType() {
        return this.type;
    }

    public void setType(final NamespacedKey type) {
        this.type = type;
        this.setState(BlockRegistry.getDefaultState(type));
    }

}
