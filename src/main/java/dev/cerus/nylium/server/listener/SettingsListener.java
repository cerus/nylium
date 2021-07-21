package dev.cerus.nylium.server.listener;

import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.Subscribe;
import dev.cerus.nylium.event.implementation.PacketProcessEvent;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.io.packet.implementation.ClientSettingsPacketIn;

/**
 * Listens for the client settings packet to update the session
 */
public class SettingsListener {

    private final EventBus eventBus;

    public SettingsListener(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handlePacket(final PacketReceivedEvent event) {
        if (!(event.getPacket() instanceof ClientSettingsPacketIn packet)) {
            return;
        }

        final PacketProcessEvent packetProcessEvent = new PacketProcessEvent(event.getSession(), packet);
        this.eventBus.callEvent(packetProcessEvent);
        if (packetProcessEvent.isCancelled()) {
            return;
        }

        event.getSession().setSettings(packet.getSettings());
    }

}
