package dev.cerus.nylium.io.netty;

import dev.cerus.nylium.io.IOUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;

public class FrameSplitter extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) throws Exception {
        out.writeBytes(msg);
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        do {
            // Get packet length
            final int len = IOUtils.peekVarInt(in);
            if (len > in.readableBytes()) {
                throw new IllegalStateException(len + " > " + in.readableBytes());
            }

            // Read the bytes that are part of this frame and add them to the result
            final ByteBuf byteBuf = in.readBytes(len + IOUtils.getVarIntSize(len));
            out.add(byteBuf.retain());
        } while (in.readableBytes() > 0);
    }

}
