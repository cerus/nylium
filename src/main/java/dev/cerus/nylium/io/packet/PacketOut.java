package dev.cerus.nylium.io.packet;

import io.netty.buffer.ByteBuf;

/**
 * Base class for outgoing packets
 */
public abstract class PacketOut extends Packet {

    public abstract void write(ByteBuf byteBuf);

}
