package dev.cerus.nylium.server.entity;

import dev.cerus.nylium.io.packet.implementation.out.TimePacketOut;
import dev.cerus.nylium.io.session.PlayerSession;

public class PlayerEntity extends LivingEntity {

    private final PlayerSession session;
    private int ticks = 0;

    public PlayerEntity(final PlayerSession session) {
        this.session = session;
        this.pos.setX(8);
        this.pos.setY(3);
        this.pos.setZ(8);
    }

    @Override
    public void tick() {
        if (this.session.getState() != PlayerSession.SessionState.PLAY) {
            return;
        }
        if (this.ticks++ < 20) {
            return;
        }

        this.session.sendPacket(new TimePacketOut(0, 0));
        this.ticks = 0;
    }

    @Override
    public void damage(final double damage) {

    }

    @Override
    public double getMaximumHealth() {
        return 20;
    }

}
