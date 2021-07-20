package dev.cerus.nylium.io.packet;

import dev.cerus.nylium.io.packet.implementation.HandshakePacketIn;
import dev.cerus.nylium.io.packet.implementation.PingPacketIn;
import dev.cerus.nylium.io.packet.implementation.RequestPacketIn;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Registry for all (incoming) packets
 */
public class PacketRegistry {

    private static final Map<BiFunction<Integer, Integer, Boolean>, Function<ByteBuf, ? extends PacketIn>> INCOMING_PACKET_MAP = new HashMap<>() {
        {
            this.put((id, len) -> id == 0x00 && len > 0, HandshakePacketIn::new);
            this.put((id, len) -> id == 0x00 && len == 0, RequestPacketIn::new);
            this.put((id, len) -> id == 0x01, PingPacketIn::new);
        }
    };

    /**
     * Attempts to find and read a matching packet
     *
     * @param id         The read packet id
     * @param len        The read packet length
     * @param byteBuffer The buffer to read from
     *
     * @return A packet or null
     */
    public static PacketIn readPacket(final int id, final int len, final ByteBuf byteBuffer) {
        System.out.println("FINDING MATCHING PACKET FOR ID " + id + " LEN " + len);
        return INCOMING_PACKET_MAP.keySet().stream()
                .filter(fun -> fun.apply(id, len))
                .map(fun -> INCOMING_PACKET_MAP.get(fun).apply(byteBuffer))
                .findAny()
                .orElse(null);
    }

}
