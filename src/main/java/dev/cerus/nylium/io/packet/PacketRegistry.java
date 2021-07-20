package dev.cerus.nylium.io.packet;

import dev.cerus.nylium.io.packet.implementation.HandshakePacketIn;
import dev.cerus.nylium.io.packet.implementation.LoginStartPacketIn;
import dev.cerus.nylium.io.packet.implementation.PingPacketIn;
import dev.cerus.nylium.io.packet.implementation.RequestPacketIn;
import dev.cerus.nylium.io.session.PlayerSession;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registry for all (incoming) packets
 */
public class PacketRegistry {

    private static final Map<Function<PacketReadingContext, Boolean>, Function<ByteBuf, ? extends PacketIn>> INCOMING_PACKET_MAP = new HashMap<>() {
        {
            this.put((context) -> context.id == 0x00 && context.length > 0
                    && context.session.getState() != PlayerSession.SessionState.LOGIN, HandshakePacketIn::new);
            this.put((context) -> context.id == 0x00 && context.length == 0, RequestPacketIn::new);
            this.put((context) -> context.id == 0x01, PingPacketIn::new);
            this.put((context) -> context.id == 0x00 && context.length > 0
                    && context.session.getState() == PlayerSession.SessionState.LOGIN, LoginStartPacketIn::new);
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
        System.out.println("FINDING MATCHING PACKET FOR ID " + context.id + " LEN " + context.length);
        return INCOMING_PACKET_MAP.keySet().stream()
                .filter(fun -> fun.apply(context))
                .map(fun -> INCOMING_PACKET_MAP.get(fun).apply(byteBuffer))
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
