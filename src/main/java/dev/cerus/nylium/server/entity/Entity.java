package dev.cerus.nylium.server.entity;

import dev.cerus.nylium.server.NyliumTicker;

public abstract class Entity implements NyliumTicker.Tickable {

    protected EntityPos pos = new EntityPos(0, 0, 0);
    protected float yaw = 0;
    protected float pitch = 0;

    public EntityPos getPos() {
        return this.pos;
    }

    public void setPos(final EntityPos pos) {
        this.pos = pos;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }

}
