package dev.cerus.nylium.io.packet;

import dev.cerus.nylium.io.packet.implementation.in.ClientSettingsPacketIn;
import dev.cerus.nylium.io.packet.implementation.in.EncryptionResponsePacketIn;
import dev.cerus.nylium.io.packet.implementation.in.HandshakePacketIn;
import dev.cerus.nylium.io.packet.implementation.in.KeepAlivePacketIn;
import dev.cerus.nylium.io.packet.implementation.in.LoginStartPacketIn;
import dev.cerus.nylium.io.packet.implementation.in.PingPacketIn;
import dev.cerus.nylium.io.packet.implementation.in.PlayerPositionAndRotationPacketIn;
import dev.cerus.nylium.io.packet.implementation.in.PluginMessagePacketIn;
import dev.cerus.nylium.io.packet.implementation.in.RequestPacketIn;
import dev.cerus.nylium.io.packet.implementation.in.TeleportConfirmPacketIn;
import dev.cerus.nylium.io.session.PlayerSession;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Registry for all (incoming) packets
 */
public class PacketRegistry {

    private static final Map<Function<PacketReadingContext, Boolean>, BiFunction<ByteBuf, Integer, ? extends PacketIn>> INCOMING_PACKET_MAP = new HashMap<>() {
        {
            this.put((context) -> context.id == 0x00 && context.length > 0
                    && context.session.getState() != PlayerSession.SessionState.LOGIN
                    && context.session.getState() != PlayerSession.SessionState.PLAY, HandshakePacketIn::new);
            this.put((context) -> context.id == 0x00 && context.length == 0, RequestPacketIn::new);
            this.put((context) -> context.id == 0x01
                    && context.session.getState() != PlayerSession.SessionState.ENCRYPTING
                    && context.session.getState() != PlayerSession.SessionState.PLAY, PingPacketIn::new);
            this.put((context) -> context.id == 0x00 && context.length > 0
                    && context.session.getState() == PlayerSession.SessionState.LOGIN, LoginStartPacketIn::new);
            this.put((context) -> context.id == 0x01
                    && context.session.getState() == PlayerSession.SessionState.ENCRYPTING, EncryptionResponsePacketIn::new);
            this.put(context -> context.id == 0x05 && context.session.getState() == PlayerSession.SessionState.PLAY, ClientSettingsPacketIn::new);
            this.put(context -> context.id == 0x0F && context.session.getState() == PlayerSession.SessionState.PLAY, KeepAlivePacketIn::new);
            this.put(context -> context.id == 0x0A && context.session.getState() == PlayerSession.SessionState.PLAY, PluginMessagePacketIn::new);
            this.put(context -> context.id == 0x00 && context.session.getState() == PlayerSession.SessionState.PLAY, TeleportConfirmPacketIn::new);
            this.put(context -> context.id == 0x12 && context.session.getState() == PlayerSession.SessionState.PLAY, PlayerPositionAndRotationPacketIn::new);
        }
    };

    /**
     * Attempts to find and read a matching packet
     *
     * @param context    The context
     * @param byteBuffer The buffer to read from
     *
     * @return A packet or null
     */
    public static PacketIn readPacket(final PacketReadingContext context, final ByteBuf byteBuffer) {
        return INCOMING_PACKET_MAP.keySet().stream()
                .filter(fun -> fun.apply(context))
                .map(fun -> INCOMING_PACKET_MAP.get(fun).apply(byteBuffer, context.length))
                .findAny()
                .orElse(null);
    }

    public static class PacketReadingContext {

        public int id;
        public int length;
        public PlayerSession session;

        public PacketReadingContext(final int id, final int length, final PlayerSession session) {
            this.id = id;
            this.length = length;
            this.session = session;
        }

    }

}
