package dev.cerus.nylium.event.implementation;

import dev.cerus.nylium.event.Event;
import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.server.key.NamespacedKey;

public class PluginMessageSentEvent extends Event {

    private final PlayerSession receiver;
    private final NamespacedKey channel;
    private final byte[] message;

    public PluginMessageSentEvent(final PlayerSession receiver, final NamespacedKey channel, final byte[] message) {
        this.receiver = receiver;
        this.channel = channel;
        this.message = message;
    }

    public PlayerSession getReceiver() {
        return this.receiver;
    }

    public NamespacedKey getChannel() {
        return this.channel;
    }

    public byte[] getMessage() {
        return this.message;
    }

}
