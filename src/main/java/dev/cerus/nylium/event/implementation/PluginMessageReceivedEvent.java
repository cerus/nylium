package dev.cerus.nylium.event.implementation;

import dev.cerus.nylium.event.Event;
import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.server.key.NamespacedKey;

public class PluginMessageReceivedEvent extends Event {

    private final PlayerSession sender;
    private final NamespacedKey channel;
    private final byte[] message;

    public PluginMessageReceivedEvent(final PlayerSession sender, final NamespacedKey channel, final byte[] message) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
    }

    public PlayerSession getSender() {
        return this.sender;
    }

    public NamespacedKey getChannel() {
        return this.channel;
    }

    public byte[] getMessage() {
        return this.message;
    }

}
