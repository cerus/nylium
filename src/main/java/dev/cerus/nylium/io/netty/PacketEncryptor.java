package dev.cerus.nylium.io.netty;

import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.io.session.PlayerSessionController;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;

/**
 * Encrypts / decrypts packets if its enabled for the session
 */
public class PacketEncryptor extends ByteToMessageCodec<ByteBuf> {

    private final PlayerSessionController sessionController;

    public PacketEncryptor(final PlayerSessionController sessionController) {
        this.sessionController = sessionController;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) throws Exception {
        final PlayerSession session = this.sessionController.getByChId(ctx.channel().id());
        if (session.isEncrypted()) {
            final byte[] inBytes = new byte[msg.readableBytes()];
            msg.readBytes(inBytes);
            final int len = session.getEncryptionContainer().getCommonEncryptOutputSize(inBytes.length);
            out.writeBytes(inBytes, 0, session.getEncryptionContainer().encryptCommon(inBytes, 0, len, inBytes, 0));
        } else {
            out.writeBytes(msg.readBytes(msg.readableBytes()));
        }
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        final PlayerSession session = this.sessionController.getByChId(ctx.channel().id());
        if (session.isEncrypted()) {
            final byte[] inBytes = new byte[in.readableBytes()];
            in.readBytes(inBytes);
            final ByteBuf buffer = ctx.alloc().heapBuffer(session.getEncryptionContainer().getCommonDecryptOutputSize(inBytes.length));
            buffer.writerIndex(session.getEncryptionContainer().decryptCommon(inBytes, 0, inBytes.length, buffer.array(), buffer.arrayOffset()));
            out.add(buffer);
        } else {
            final ByteBuf outBuf = Unpooled.buffer();
            outBuf.writeBytes(in.readBytes(in.readableBytes()));
            out.add(outBuf);
        }
    }

}
