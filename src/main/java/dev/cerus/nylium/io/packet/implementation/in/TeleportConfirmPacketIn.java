package dev.cerus.nylium.io.packet.implementation.in;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketIn;
import io.netty.buffer.ByteBuf;

public class TeleportConfirmPacketIn extends PacketIn {

    private int teleportId;

    public TeleportConfirmPacketIn(final ByteBuf byteBuffer, final int length) {
        super(byteBuffer, length);
    }

    @Override
    protected void read(final ByteBuf byteBuffer, final int length) {
        this.teleportId = IOUtils.readVarInt(byteBuffer);
    }

    public int getTeleportId() {
        return this.teleportId;
    }

}
