package dev.cerus.nylium.io.packet.implementation.out;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import io.netty.buffer.ByteBuf;

/**
 * 0x01 https://wiki.vg/Server_List_Ping#Pong
 */
public class PongPacketOut extends PacketOut {

    private final long identifier;

    public PongPacketOut(final long identifier) {
        this.identifier = identifier;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, 0x01);
        byteBuf.writeLong(this.identifier);
    }

}
