package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.server.key.NamespacedKey;
import io.netty.buffer.ByteBuf;

public class PluginMessagePacketIn extends PacketIn {

    private NamespacedKey channel;
    private byte[] message;

    public PluginMessagePacketIn(final ByteBuf byteBuffer, final int length) {
        super(byteBuffer, length);
    }

    @Override
    protected void read(final ByteBuf byteBuffer, final int length) {
        this.channel = NamespacedKey.of(IOUtils.readString(byteBuffer));
//        final int msgLen = length - IOUtils.getVarIntSize(this.channel.toString().length()) - this.channel.toString().length();
        this.message = new byte[byteBuffer.readableBytes()];
        byteBuffer.readBytes(this.message);
    }

    public NamespacedKey getChannel() {
        return this.channel;
    }

    public byte[] getMessage() {
        return this.message;
    }

}
