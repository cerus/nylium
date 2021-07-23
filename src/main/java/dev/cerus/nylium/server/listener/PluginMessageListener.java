package dev.cerus.nylium.server.listener;

import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.Subscribe;
import dev.cerus.nylium.event.implementation.PacketProcessEvent;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.event.implementation.PluginMessageReceivedEvent;
import dev.cerus.nylium.io.packet.implementation.in.PluginMessagePacketIn;
import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.server.key.MinecraftKeys;
import dev.cerus.nylium.server.key.NamespacedKey;

public class PluginMessageListener {

    private final EventBus eventBus;

    public PluginMessageListener(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handlePacket(final PacketReceivedEvent event) {
        if (!(event.getPacket() instanceof PluginMessagePacketIn packet)) {
            return;
        }

        final PlayerSession session = event.getSession();
        final PacketProcessEvent packetProcessEvent = new PacketProcessEvent(session, packet);
        this.eventBus.callEvent(packetProcessEvent);
        if (packetProcessEvent.isCancelled()) {
            return;
        }

        final PluginMessageReceivedEvent pluginMessageReceivedEvent =
                new PluginMessageReceivedEvent(session, packet.getChannel(), packet.getMessage());
        this.eventBus.callEvent(pluginMessageReceivedEvent);
    }

    @Subscribe
    public void handlePluginMessage(final PluginMessageReceivedEvent event) {
        final PlayerSession session = event.getSender();
        final NamespacedKey channel = event.getChannel();
        final byte[] message = event.getMessage();

        if (!channel.equals(MinecraftKeys.BRAND)) {
            return;
        }

        session.setClientBrand(new String(message));
    }

}
