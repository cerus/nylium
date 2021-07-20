package dev.cerus.nylium.io.netty;

import dev.cerus.nylium.io.IOUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketLengthPrepender extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) throws Exception {
        final int len = msg.readableBytes();
        IOUtils.writeVarInt(out, len);
        out.writeBytes(msg);
    }

}
