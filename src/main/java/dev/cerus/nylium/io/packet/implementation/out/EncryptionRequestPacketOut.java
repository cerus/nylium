package dev.cerus.nylium.io.packet.implementation.out;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.io.session.encryption.EncryptionContainer;
import io.netty.buffer.ByteBuf;

/**
 * 0x01 https://wiki.vg/Protocol#Encryption_Request
 */
public class EncryptionRequestPacketOut extends PacketOut {

    private final EncryptionContainer encryptionContainer;

    public EncryptionRequestPacketOut(final EncryptionContainer encryptionContainer) {
        this.encryptionContainer = encryptionContainer;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, 0x01);
        IOUtils.writeString(byteBuf, "");
        IOUtils.writeVarInt(byteBuf, this.encryptionContainer.getPublicKeyLength());
        byteBuf.writeBytes(this.encryptionContainer.getPublicKey());
        IOUtils.writeVarInt(byteBuf, this.encryptionContainer.getVerifyToken().length);
        byteBuf.writeBytes(this.encryptionContainer.getVerifyToken());
    }

}
