package dev.cerus.nylium.io.netty;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.Packet;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.io.packet.PacketRegistry;
import dev.cerus.nylium.io.session.PlayerSessionController;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;
import java.util.logging.Logger;

public class PacketCodec extends ByteToMessageCodec<Packet> {

    private final PlayerSessionController playerSessionController;

    public PacketCodec(final PlayerSessionController playerSessionController) {
        this.playerSessionController = playerSessionController;
    }

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
        final PacketIn packet = PacketRegistry.readPacket(new PacketRegistry.PacketReadingContext(
                id,
                len - IOUtils.getVarIntSize(len),
                this.playerSessionController.getByChId(ctx.channel().id())
        ), in);
        if (packet == null) {
            throw new NullPointerException("Packet " + id + " not found");
        }

        // Check if packet has been fully read
        if (in.readableBytes() > 0) {
            Logger.getLogger(this.getClass().getName()).warning("Packet " + packet.getClass().getSimpleName() + " has not been fully read!");
            in.readBytes(in.readableBytes()).release();
        }

        // Add to result
        out.add(packet);
    }

}
