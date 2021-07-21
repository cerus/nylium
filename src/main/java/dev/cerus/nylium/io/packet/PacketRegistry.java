package dev.cerus.nylium.io.packet;

import dev.cerus.nylium.io.packet.implementation.ClientSettingsPacketIn;
import dev.cerus.nylium.io.packet.implementation.EncryptionResponsePacketIn;
import dev.cerus.nylium.io.packet.implementation.HandshakePacketIn;
import dev.cerus.nylium.io.packet.implementation.KeepAlivePacketIn;
import dev.cerus.nylium.io.packet.implementation.LoginStartPacketIn;
import dev.cerus.nylium.io.packet.implementation.PingPacketIn;
import dev.cerus.nylium.io.packet.implementation.PluginMessagePacketIn;
import dev.cerus.nylium.io.packet.implementation.RequestPacketIn;
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
                    && context.session.getState() != PlayerSession.SessionState.LOGIN, HandshakePacketIn::new);
            this.put((context) -> context.id == 0x00 && context.length == 0, RequestPacketIn::new);
            this.put((context) -> context.id == 0x01
                    && context.session.getState() != PlayerSession.SessionState.ENCRYPTING, PingPacketIn::new);
            this.put((context) -> context.id == 0x00 && context.length > 0
                    && context.session.getState() == PlayerSession.SessionState.LOGIN, LoginStartPacketIn::new);
            this.put((context) -> context.id == 0x01
                    && context.session.getState() == PlayerSession.SessionState.ENCRYPTING, EncryptionResponsePacketIn::new);
            this.put(context -> context.id == 0x05 && context.session.getState() == PlayerSession.SessionState.PLAY, ClientSettingsPacketIn::new);
            this.put(context -> context.id == 0x0F && context.session.getState() == PlayerSession.SessionState.PLAY, KeepAlivePacketIn::new);
            this.put(context -> context.id == 0x0A && context.session.getState() == PlayerSession.SessionState.PLAY, PluginMessagePacketIn::new);
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
