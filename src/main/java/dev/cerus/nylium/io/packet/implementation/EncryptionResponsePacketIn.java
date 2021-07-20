package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketIn;
import io.netty.buffer.ByteBuf;

public class EncryptionResponsePacketIn extends PacketIn {

    private byte[] sharedSecret;
    private byte[] verifyToken;

    public EncryptionResponsePacketIn(final ByteBuf byteBuffer) {
        super(byteBuffer);
    }

    @Override
    protected void read(final ByteBuf byteBuffer) {
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
