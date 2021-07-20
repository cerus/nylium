package dev.cerus.nylium.server.listener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.Subscribe;
import dev.cerus.nylium.event.implementation.PacketProcessEvent;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.packet.implementation.DisconnectPacketOut;
import dev.cerus.nylium.io.packet.implementation.EncryptionRequestPacketOut;
import dev.cerus.nylium.io.packet.implementation.EncryptionResponsePacketIn;
import dev.cerus.nylium.io.packet.implementation.HandshakePacketIn;
import dev.cerus.nylium.io.packet.implementation.JoinGamePacketOut;
import dev.cerus.nylium.io.packet.implementation.LoginStartPacketIn;
import dev.cerus.nylium.io.packet.implementation.LoginSuccessPacketOut;
import dev.cerus.nylium.io.packet.implementation.PingPacketIn;
import dev.cerus.nylium.io.packet.implementation.PongPacketOut;
import dev.cerus.nylium.io.packet.implementation.RequestPacketIn;
import dev.cerus.nylium.io.packet.implementation.ResponsePacketOut;
import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.mojang.MojangApiWrapper;
import dev.cerus.nylium.server.key.NamespacedKey;
import dev.cerus.simplenbt.tag.TagCompound;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.StreamSupport;

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
        } else if (packet instanceof LoginStartPacketIn) {
            this.handleLoginStartPacket(event.getSession(), (LoginStartPacketIn) packet);
        } else if (packet instanceof EncryptionResponsePacketIn) {
            this.handleEncryptionResponsePacket(event.getSession(), (EncryptionResponsePacketIn) packet);
        }
    }

    private void handleEncryptionResponsePacket(final PlayerSession session, final EncryptionResponsePacketIn packet) {
        final byte[] decryptedVerifyToken;
        final byte[] decryptedSharedSecret;
        try {
            decryptedVerifyToken = session.getEncryptionContainer().decryptExchange(packet.getVerifyToken());
            decryptedSharedSecret = session.getEncryptionContainer().decryptExchange(packet.getSharedSecret());
        } catch (final Exception e) {
            e.printStackTrace();
            session.getContext().close();
            return;
        }

        if (!Arrays.equals(decryptedVerifyToken, session.getEncryptionContainer().getVerifyToken())) {
            session.getContext().close();
            return;
        }

        try {
            session.getEncryptionContainer().initCommon(decryptedSharedSecret);
            final PlayerSession.GameProfile gameProfile = session.getGameProfile();

            final JsonObject jsonObject = MojangApiWrapper.hasJoined(gameProfile.getUsername(),
                    session.getEncryptionContainer().getHash());

            gameProfile.setUsername(jsonObject.get("name").getAsString());
            gameProfile.setId(UUID.fromString(jsonObject.get("id").getAsString()
                    .replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")));
            StreamSupport.stream(jsonObject.get("properties").getAsJsonArray().spliterator(), false)
                    .map(jsonElement -> {
                        final JsonObject obj = jsonElement.getAsJsonObject();
                        return new PlayerSession.GameProfile.Property(
                                obj.get("name").getAsString(),
                                obj.get("value").getAsString(),
                                obj.has("signature") ? obj.get("signature").getAsString() : null
                        );
                    })
                    .forEach(property -> gameProfile.getProperties().add(property));
        } catch (final Exception e) {
            e.printStackTrace();
            session.getContext().close();
            return;
        }

        session.setEncrypted(true);
        session.setState(PlayerSession.SessionState.PLAY);
        session.sendPacket(new LoginSuccessPacketOut(session.getGameProfile().getId(), session.getGameProfile().getUsername()));
        session.sendPacket(new JoinGamePacketOut(
                0,
                false,
                (byte) 0,
                (byte) -1,
                new NamespacedKey[] {NamespacedKey.def("affe")},
                TagCompound.createRootTag(),
                TagCompound.createRootTag(),
                NamespacedKey.def("affe"),
                1337L,
                1337,
                2,
                false,
                true,
                false,
                false
        ));
        session.sendPacket(new DisconnectPacketOut(true));
    }

    private void handleLoginStartPacket(final PlayerSession session, final LoginStartPacketIn packet) {
        session.getGameProfile().setUsername(packet.getUsername());
        session.setState(PlayerSession.SessionState.ENCRYPTING);
        session.sendPacket(new EncryptionRequestPacketOut(session.getEncryptionContainer()));
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
            session.setState(PlayerSession.SessionState.LOGIN);
        }
    }

}
