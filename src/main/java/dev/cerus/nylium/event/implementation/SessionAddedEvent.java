package dev.cerus.nylium.event.implementation;

import dev.cerus.nylium.event.Event;
import dev.cerus.nylium.io.session.PlayerSession;

public class SessionAddedEvent extends Event {

    private final PlayerSession session;

    public SessionAddedEvent(final PlayerSession session) {
        this.session = session;
    }

    public PlayerSession getSession() {
        return this.session;
    }

}
