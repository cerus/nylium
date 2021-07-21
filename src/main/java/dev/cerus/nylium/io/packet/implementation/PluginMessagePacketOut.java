package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.server.key.NamespacedKey;
import io.netty.buffer.ByteBuf;

public class PluginMessagePacketOut extends PacketOut {

    private final NamespacedKey channel;
    private final byte[] message;

    public PluginMessagePacketOut(final NamespacedKey channel, final byte[] message) {
        this.channel = channel;
        this.message = message;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, 0x18);
        IOUtils.writeString(byteBuf, this.channel.toString());
        byteBuf.writeBytes(this.message);
    }

}
