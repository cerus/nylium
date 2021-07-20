package dev.cerus.nylium.event.implementation;

import dev.cerus.nylium.event.CancellableEvent;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.session.PlayerSession;

/**
 * This event gets called right before the server starts to process this event.
 * If this is cancelled the server will ignore the packet.
 */
public class PacketProcessEvent extends CancellableEvent {

    private final PlayerSession session;
    private final PacketIn packet;

    public PacketProcessEvent(final PlayerSession session, final PacketIn packet) {
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
