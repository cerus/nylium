package dev.cerus.nylium.server.listener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.Subscribe;
import dev.cerus.nylium.event.implementation.PacketProcessEvent;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.packet.implementation.HandshakePacketIn;
import dev.cerus.nylium.io.packet.implementation.PingPacketIn;
import dev.cerus.nylium.io.packet.implementation.PongPacketOut;
import dev.cerus.nylium.io.packet.implementation.RequestPacketIn;
import dev.cerus.nylium.io.packet.implementation.ResponsePacketOut;
import dev.cerus.nylium.io.session.PlayerSession;
import java.util.UUID;

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
        } else if (packet instanceof RequestPacketIn) {
            this.handleRequestPacket(event.getSession(), (RequestPacketIn) packet);
        } else if (packet instanceof PingPacketIn) {
            event.getSession().sendPacket(new PongPacketOut(((PingPacketIn) packet).getIdentifier()));
        }
    }

    private void handleRequestPacket(final PlayerSession session, final RequestPacketIn packet) {
        final PacketProcessEvent event = new PacketProcessEvent(session, packet);
        this.eventBus.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        // TODO: Proper implementation
        final JsonObject object = new JsonObject();
        final JsonObject version = new JsonObject();
        version.addProperty("name", "Nylium");
        version.addProperty("protocol", session.getProtocolVer() + 100);
        object.add("version", version);
        final JsonObject players = new JsonObject();
        players.addProperty("max", 0);
        players.addProperty("online", 1337);

        final JsonArray sample = new JsonArray();
        JsonObject sampleEntry = new JsonObject();
        sampleEntry.addProperty("name", "Never gonna give you up");
        sampleEntry.addProperty("id", new UUID(0, 0).toString());
        sample.add(sampleEntry);
        sampleEntry = new JsonObject();
        sampleEntry.addProperty("name", "Never gonna let you down");
        sampleEntry.addProperty("id", new UUID(0, 1).toString());
        sample.add(sampleEntry);
        players.add("sample", sample);

        object.add("players", players);
        final JsonObject description = new JsonObject();
        description.addProperty("text", "Nylium server");
        object.add("description", description);

        session.sendPacket(new ResponsePacketOut(object.toString()));
    }

    private void handleHandshakePacket(final PlayerSession session, final HandshakePacketIn packet) {
        session.setProtocolVer(packet.getProtocolVer());
        if (packet.getNextState() == 1) {
            // We don't really care about this
        } else if (packet.getNextState() == 2) {
            // Login
            // TODO
        }
    }

}
