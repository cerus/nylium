package dev.cerus.nylium.io.packet.implementation.out;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.server.entity.EntityPos;
import io.netty.buffer.ByteBuf;

public class PlayerPositionAndLookPacketOut extends PacketOut {

    private final EntityPos pos;
    private final float pitch;
    private final float yaw;

    public PlayerPositionAndLookPacketOut(final EntityPos pos, final float pitch, final float yaw) {
        this.pos = pos;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, 0x38);
        byteBuf.writeDouble(this.pos.getX());
        byteBuf.writeDouble(this.pos.getY());
        byteBuf.writeDouble(this.pos.getZ());
        byteBuf.writeFloat(this.yaw);
        byteBuf.writeFloat(this.pitch);
        byteBuf.writeByte(0);
        IOUtils.writeVarInt(byteBuf, 0);
        byteBuf.writeByte(0);
    }

}
