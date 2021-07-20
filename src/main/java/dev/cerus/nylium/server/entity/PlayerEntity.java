package dev.cerus.nylium.server.entity;

import dev.cerus.nylium.io.session.PlayerSession;

public class PlayerEntity extends Entity {

    private final PlayerSession session;

    public PlayerEntity(final PlayerSession session) {
        this.session = session;
    }
}
