package dev.cerus.nylium.server.entity;

public abstract class LivingEntity extends Entity {

    protected double health = this.getMaximumHealth();

    public double getHealth() {
        return this.health;
    }

    public void setHealth(final double health) {
        this.health = health;
    }

    public abstract void damage(double damage);

    public abstract double getMaximumHealth();

}
