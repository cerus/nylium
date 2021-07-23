package dev.cerus.nylium.io.session;

import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.io.packet.implementation.out.DisconnectPacketOut;
import dev.cerus.nylium.io.session.encryption.DefaultEncryptionContainer;
import dev.cerus.nylium.io.session.encryption.EncryptionContainer;
import dev.cerus.nylium.server.chat.ChatComponent;
import dev.cerus.nylium.server.chat.StringComponent;
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

/**
 * Stores player specific information
 */
public class PlayerSession {

    public static int NEXT_ID = 0;

    private final int id = NEXT_ID++;
    private final ChannelHandlerContext context;
    private final GameProfile gameProfile;
    private final EncryptionContainer encryptionContainer;
    private PlayerEntity playerEntity;
    private int protocolVer;
    private SessionState state;
    private boolean encrypted;
    private Settings settings;
    private String clientBrand;

    public PlayerSession(final ChannelHandlerContext context) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        this(context, null);
    }

    public PlayerSession(final ChannelHandlerContext context, final PlayerEntity playerEntity) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        this.context = context;
        this.gameProfile = new GameProfile(null, null);
        this.encryptionContainer = new DefaultEncryptionContainer();
        this.playerEntity = playerEntity;
        this.state = SessionState.HANDSHAKE;
    }

    /**
     * Disconnects the client
     *
     * @param reason The reason for the disconnect
     */
    public void disconnect(final String reason) {
        this.disconnect(StringComponent.of(reason));
    }

    /**
     * Disconnects the client
     *
     * @param reason The reason for the disconnect
     */
    public void disconnect(final ChatComponent reason) {
        this.sendPacket(new DisconnectPacketOut(this.state != SessionState.PLAY, reason));
        this.context.close();
    }

    /**
     * Sends a packet to the client
     *
     * @param packet The packet to send
     */
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

    public Settings getSettings() {
        return this.settings;
    }

    public void setSettings(final Settings settings) {
        this.settings = settings;
    }

    public int getId() {
        return this.id;
    }

    public String getClientBrand() {
        return this.clientBrand;
    }

    public void setClientBrand(final String clientBrand) {
        this.clientBrand = clientBrand;
    }

    /**
     * Contains the different login states
     */
    public enum SessionState {
        // No login attempt has happened yet, only a ping
        HANDSHAKE,

        // Client attempts to log in
        LOGIN,

        // Client attempts to start encryption
        ENCRYPTING,

        // Client has logged in and is now in play mode
        PLAY
    }

    /**
     * Stores player profile information (uuid, name, skin textures)
     */
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

        public record Property(String name, String value, String signature) {
        }

    }

    public record Settings(String locale, byte viewDistance, PlayerSession.Settings.ChatMode chatMode,
                           boolean colors, PlayerSession.Settings.SkinParts skinParts, boolean rightHanded,
                           boolean disableTextFiltering) {

        public enum ChatMode {
            ENABLED(0), COMMANDS_ONLY(1), HIDDEN(2);

            private final int id;

            ChatMode(final int id) {
                this.id = id;
            }

            public static ChatMode getById(final int id) {
                for (final ChatMode value : values()) {
                    if (value.id == id) {
                        return value;
                    }
                }
                return null;
            }

            public int getId() {
                return this.id;
            }
        }

        public record SkinParts(byte backingByte) {

            public boolean cape() {
                return (this.backingByte & 0x01) == 0x01;
            }

            public boolean jacket() {
                return (this.backingByte & 0x02) == 0x02;
            }

            public boolean leftSleeve() {
                return (this.backingByte & 0x04) == 0x04;
            }

            public boolean rightSleeve() {
                return (this.backingByte & 0x08) == 0x08;
            }

            public boolean leftLeg() {
                return (this.backingByte & 0x10) == 0x10;
            }

            public boolean rightLeg() {
                return (this.backingByte & 0x20) == 0x20;
            }

            public boolean hat() {
                return (this.backingByte & 0x40) == 0x40;
            }

        }

    }

}
