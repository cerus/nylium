package dev.cerus.nylium.io.packet.implementation.in;

import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.server.entity.EntityPos;
import io.netty.buffer.ByteBuf;

public class PlayerPositionAndRotationPacketIn extends PacketIn {

    private EntityPos entityPos;
    private float pitch;
    private float yaw;
    private boolean onGround;

    public PlayerPositionAndRotationPacketIn(final ByteBuf byteBuffer, final int length) {
        super(byteBuffer, length);
    }

    @Override
    protected void read(final ByteBuf byteBuffer, final int length) {
        this.entityPos = new EntityPos(
                byteBuffer.readDouble(),
                byteBuffer.readDouble(),
                byteBuffer.readDouble()
        );
        this.yaw = byteBuffer.readFloat();
        this.pitch = byteBuffer.readFloat();
        this.onGround = byteBuffer.readByte() == 0x01;
    }

    public EntityPos getEntityPos() {
        return this.entityPos;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

}
