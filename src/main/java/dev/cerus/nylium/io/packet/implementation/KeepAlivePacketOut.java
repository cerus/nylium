package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import io.netty.buffer.ByteBuf;

/**
 * 0x21 https://wiki.vg/Protocol#Keep_Alive_.28clientbound.29
 */
public class KeepAlivePacketOut extends PacketOut {

    private final long identifier;

    public KeepAlivePacketOut(final long identifier) {
        this.identifier = identifier;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, 0x21);
        byteBuf.writeLong(this.identifier);
    }

}
