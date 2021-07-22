package dev.cerus.nylium.server.listener;

import com.google.gson.JsonObject;
import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.Subscribe;
import dev.cerus.nylium.event.implementation.PacketProcessEvent;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.packet.implementation.EncryptionResponsePacketIn;
import dev.cerus.nylium.io.packet.implementation.JoinGamePacketOut;
import dev.cerus.nylium.io.packet.implementation.LoginSuccessPacketOut;
import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.mojang.MojangApiWrapper;
import dev.cerus.nylium.server.chat.ChatColor;
import dev.cerus.nylium.server.chat.ChatComponentStyle;
import dev.cerus.nylium.server.chat.StringComponent;
import dev.cerus.nylium.server.dimension.DimensionCodec;
import dev.cerus.nylium.server.key.NamespacedKey;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.StreamSupport;

/**
 * Handles the encryption process
 */
public class EncryptionListener {

    private final EventBus eventBus;

    public EncryptionListener(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handlePacket(final PacketReceivedEvent event) {
        final PacketIn packetIn = event.getPacket();
        final PlayerSession session = event.getSession();

        if (!(packetIn instanceof final EncryptionResponsePacketIn packet)) {
            return;
        }

        final PacketProcessEvent processEvent = new PacketProcessEvent(session, packet);
        this.eventBus.callEvent(processEvent);
        if (processEvent.isCancelled()) {
            return;
        }

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
            session.disconnect(StringComponent.of("Invalid verify token")
                    .setStyle(new ChatComponentStyle()
                            .setColor(ChatColor.RED)));
            return;
        }

        // TODO: Change this try catch block, it looks ugly and I hate it
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

        session.setState(PlayerSession.SessionState.PLAY);
        session.setEncrypted(true);
        session.sendPacket(new LoginSuccessPacketOut(session.getGameProfile().getId(), session.getGameProfile().getUsername()));

        // TODO: Pull state from server
        session.sendPacket(new JoinGamePacketOut(
                0,
                false,
                (byte) 0,
                (byte) -1,
                new NamespacedKey[] {NamespacedKey.mc("world")},
                DimensionCodec.getDimensionCodec(),
                DimensionCodec.getDimensionType(0),
                NamespacedKey.mc("world"),
                1337L,
                1337,
                2,
                false,
                true,
                false,
                false
        ));
    }

}
