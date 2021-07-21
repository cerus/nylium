package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.packet.PacketIn;
import io.netty.buffer.ByteBuf;

/**
 * 0x0F https://wiki.vg/Protocol#Keep_Alive_.28serverbound.29
 */
public class KeepAlivePacketIn extends PacketIn {

    private long identifier;

    public KeepAlivePacketIn(final ByteBuf byteBuffer) {
        super(byteBuffer);
    }

    @Override
    protected void read(final ByteBuf byteBuffer) {
        this.identifier = byteBuffer.readLong();
    }

    public long getIdentifier() {
        return this.identifier;
    }

}
