package dev.cerus.nylium.server.listener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.Subscribe;
import dev.cerus.nylium.event.implementation.PacketProcessEvent;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.packet.implementation.in.PingPacketIn;
import dev.cerus.nylium.io.packet.implementation.in.RequestPacketIn;
import dev.cerus.nylium.io.packet.implementation.out.PongPacketOut;
import dev.cerus.nylium.io.packet.implementation.out.ResponsePacketOut;
import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.server.NyliumServer;
import java.util.UUID;

/**
 * Handles the server list ping process
 */
public class PingListener {

    private final EventBus eventBus;

    public PingListener(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handlePacket(final PacketReceivedEvent event) {
        final PacketIn packetIn = event.getPacket();

        if (packetIn instanceof RequestPacketIn) {
            this.handleRequestPacket(event.getSession(), (RequestPacketIn) packetIn);
        } else if (packetIn instanceof PingPacketIn) {
            event.getSession().sendPacket(new PongPacketOut(((PingPacketIn) packetIn).getIdentifier()));
        }
    }

    private void handleRequestPacket(final PlayerSession session, final RequestPacketIn packet) {
        final PacketProcessEvent event = new PacketProcessEvent(session, packet);
        this.eventBus.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        final int protocolVer = session.getProtocolVer();

        // TODO: Proper implementation
        final JsonObject object = new JsonObject();
        final JsonObject version = new JsonObject();
        version.addProperty("name", NyliumServer.PROTOCOL_NAME);
        version.addProperty("protocol", (protocolVer >= NyliumServer.MIN_PROTOCOL_VERSION
                && protocolVer <= NyliumServer.MAX_PROTOCOL_VERSION) ? protocolVer : NyliumServer.MIN_PROTOCOL_VERSION);
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

}
