package dev.cerus.nylium.io.packet.implementation.in;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketIn;
import io.netty.buffer.ByteBuf;

/**
 * 0x01 https://wiki.vg/Protocol#Encryption_Response
 */
public class EncryptionResponsePacketIn extends PacketIn {

    private byte[] sharedSecret;
    private byte[] verifyToken;

    public EncryptionResponsePacketIn(final ByteBuf byteBuffer, final int length) {
        super(byteBuffer, length);
    }

    @Override
    protected void read(final ByteBuf byteBuffer, final int length) {
        int len = IOUtils.readVarInt(byteBuffer);
        this.sharedSecret = new byte[len];
        byteBuffer.readBytes(this.sharedSecret);

        len = IOUtils.readVarInt(byteBuffer);
        this.verifyToken = new byte[len];
        byteBuffer.readBytes(this.verifyToken);
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    public byte[] getSharedSecret() {
        return this.sharedSecret;
    }

}
