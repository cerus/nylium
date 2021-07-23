package dev.cerus.nylium.io.packet.implementation.in;

import dev.cerus.nylium.io.packet.PacketIn;
import io.netty.buffer.ByteBuf;

/**
 * 0x01 https://wiki.vg/Server_List_Ping#Ping
 */
public class PingPacketIn extends PacketIn {

    private long identifier;

    public PingPacketIn(final ByteBuf byteBuffer, final int length) {
        super(byteBuffer, length);
    }

    @Override
    protected void read(final ByteBuf byteBuffer, final int length) {
        this.identifier = byteBuffer.readLong();
    }

    public long getIdentifier() {
        return this.identifier;
    }

}
