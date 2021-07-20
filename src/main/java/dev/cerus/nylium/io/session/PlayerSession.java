package dev.cerus.nylium.io.session;

import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.io.session.encryption.DefaultEncryptionContainer;
import dev.cerus.nylium.io.session.encryption.EncryptionContainer;
import dev.cerus.nylium.server.entity.PlayerEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.crypto.NoSuchPaddingException;

public class PlayerSession {

    private final ChannelHandlerContext context;
    private final GameProfile gameProfile;
    private final EncryptionContainer encryptionContainer;
    private PlayerEntity playerEntity;
    private int protocolVer;
    private SessionState state;
    private boolean encrypted;

    public PlayerSession(final ChannelHandlerContext context) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        this(context, null);
    }

    public PlayerSession(final ChannelHandlerContext context, final PlayerEntity playerEntity) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        this.context = context;
        this.gameProfile = new GameProfile(null, null);
        this.encryptionContainer = new DefaultEncryptionContainer();
        this.playerEntity = playerEntity;
        this.state = SessionState.NONE;
    }

    public void sendPacket(final PacketOut packet) {
        System.out.println("SENDING " + packet.getClass().getSimpleName());
        final ByteBuf buf = Unpooled.buffer();

        packet.write(buf);
        final byte[] arr = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), arr);

        this.context.writeAndFlush(buf);
    }

    public ChannelHandlerContext getContext() {
        return this.context;
    }

    public PlayerEntity getPlayerEntity() {
        return this.playerEntity;
    }

    public void setPlayerEntity(final PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public int getProtocolVer() {
        return this.protocolVer;
    }

    public void setProtocolVer(final int protocolVer) {
        this.protocolVer = protocolVer;
    }

    public SessionState getState() {
        return this.state;
    }

    public void setState(final SessionState state) {
        this.state = state;
    }

    public EncryptionContainer getEncryptionContainer() {
        return this.encryptionContainer;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public void setEncrypted(final boolean encrypted) {
        this.encrypted = encrypted;
    }

    public enum SessionState {
        NONE, LOGIN, ENCRYPTING, PLAY
    }

    public static class GameProfile {

        private final List<Property> properties;
        private String username;
        private UUID id;

        public GameProfile(final String username, final UUID uuid) {
            this.properties = new ArrayList<>();
            this.username = username;
            this.id = uuid;
        }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public UUID getId() {
            return this.id;
        }

        public void setId(final UUID id) {
            this.id = id;
        }

        public List<Property> getProperties() {
            return this.properties;
        }

        public static class Property {

            private final String name;
            private final String value;
            private final String signature;

            public Property(final String name, final String value, final String signature) {
                this.name = name;
                this.value = value;
                this.signature = signature;
            }

            public String getName() {
                return this.name;
            }

            public String getValue() {
                return this.value;
            }

            public String getSignature() {
                return this.signature;
            }

        }

    }

}
