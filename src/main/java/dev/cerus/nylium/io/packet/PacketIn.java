package dev.cerus.nylium.io.packet;

import io.netty.buffer.ByteBuf;

/**
 * Base class for incoming packets
 */
public abstract class PacketIn extends Packet {

    public PacketIn(final ByteBuf byteBuffer) {
        this.read(byteBuffer);
    }

    protected abstract void read(ByteBuf byteBuffer);

}
