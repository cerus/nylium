package dev.cerus.nylium.event.implementation;

import dev.cerus.nylium.event.Event;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.session.PlayerSession;

/**
 * Gets called right after a new packet is received.
 * This event can not be cancelled. Use the {@link PacketProcessEvent} instead.
 */
public class PacketReceivedEvent extends Event {

    private final PlayerSession session;
    private final PacketIn packet;

    public PacketReceivedEvent(final PlayerSession session, final PacketIn packet) {
        this.session = session;
        this.packet = packet;
    }

    public PlayerSession getSession() {
        return this.session;
    }

    public PacketIn getPacket() {
        return this.packet;
    }

}
