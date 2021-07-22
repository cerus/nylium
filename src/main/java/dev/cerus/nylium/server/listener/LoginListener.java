package dev.cerus.nylium.server.listener;

import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.Subscribe;
import dev.cerus.nylium.event.implementation.PacketProcessEvent;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.packet.implementation.EncryptionRequestPacketOut;
import dev.cerus.nylium.io.packet.implementation.HandshakePacketIn;
import dev.cerus.nylium.io.packet.implementation.LoginStartPacketIn;
import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.server.NyliumServer;
import dev.cerus.nylium.server.chat.ChatColor;
import dev.cerus.nylium.server.chat.ChatComponentStyle;
import dev.cerus.nylium.server.chat.StringComponent;

/**
 * Handles the login process
 */
public class LoginListener {

    private final EventBus eventBus;

    public LoginListener(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handlePacket(final PacketReceivedEvent event) {
        final PacketIn packet = event.getPacket();

        if (packet instanceof HandshakePacketIn) {
            this.handleHandshakePacket(event.getSession(), (HandshakePacketIn) packet);
        } else if (packet instanceof LoginStartPacketIn) {
            this.handleLoginStartPacket(event.getSession(), (LoginStartPacketIn) packet);
        }
    }

    private void handleLoginStartPacket(final PlayerSession session, final LoginStartPacketIn packet) {
        final PacketProcessEvent event = new PacketProcessEvent(session, packet);
        this.eventBus.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (session.getProtocolVer() < NyliumServer.MIN_PROTOCOL_VERSION) {
            session.disconnect(StringComponent.of("Client too old")
                    .setStyle(new ChatComponentStyle()
                            .setColor(ChatColor.RED)));
            return;
        }
        if (session.getProtocolVer() > NyliumServer.MAX_PROTOCOL_VERSION) {
            session.disconnect(StringComponent.of("Server too old")
                    .setStyle(new ChatComponentStyle()
                            .setColor(ChatColor.RED)));
            return;
        }

        session.getGameProfile().setUsername(packet.getUsername());
        session.setState(PlayerSession.SessionState.ENCRYPTING);
        session.sendPacket(new EncryptionRequestPacketOut(session.getEncryptionContainer()));
    }

    private void handleHandshakePacket(final PlayerSession session, final HandshakePacketIn packet) {
        final PacketProcessEvent event = new PacketProcessEvent(session, packet);
        this.eventBus.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        session.setProtocolVer(packet.getProtocolVer());
        if (packet.getNextState() == 1) {
            // We don't really care about this
        } else if (packet.getNextState() == 2) {
            // Client wants to Log in
            session.setState(PlayerSession.SessionState.LOGIN);
        }
    }

}
