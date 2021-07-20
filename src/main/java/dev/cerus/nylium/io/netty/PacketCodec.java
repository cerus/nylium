package dev.cerus.nylium.io.netty;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.Packet;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.io.packet.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;

public class PacketCodec extends ByteToMessageCodec<Packet> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Packet msg, final ByteBuf out) throws Exception {
        if (!(msg instanceof PacketOut)) {
            throw new IllegalStateException("Can only encode PacketOut");
        }

        // Write packet
        ((PacketOut) msg).write(out);
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        // Read packet length and packet id
        final int len = IOUtils.readVarInt(in);
        final int id = IOUtils.readVarInt(in);

        // Attempt to read packet
        final PacketIn packet = PacketRegistry.readPacket(id, len - IOUtils.getVarIntSize(len), in);
        if (packet == null) {
            throw new NullPointerException("Packet " + id + " not found");
        }

        // Add to result
        out.add(packet);
    }

}