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
        final int i = 0;
        do {
            final int len = IOUtils.peekVarInt(in);
            if (len > in.readableBytes()) {
                throw new IllegalStateException(len + " > " + in.readableBytes());
            }

            final ByteBuf byteBuf = in.readBytes(len + IOUtils.getVarIntSize(len));

            final byte[] arr = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(byteBuf.readerIndex(), arr);


            out.add(byteBuf.retain());
        } while (in.readableBytes() > 0);
    }

}
