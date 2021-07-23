package dev.cerus.nylium.server.listener;

import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.Subscribe;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.io.packet.implementation.in.PlayerPositionAndRotationPacketIn;
import dev.cerus.nylium.server.entity.PlayerEntity;

public class PlayerMoveListener {

    private final EventBus eventBus;

    public PlayerMoveListener(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handlePacket(final PacketReceivedEvent event) {
        if (!(event.getPacket() instanceof PlayerPositionAndRotationPacketIn packet)) {
            return;
        }

        final PlayerEntity playerEntity = event.getSession().getPlayerEntity();
        playerEntity.getPos().setX(packet.getEntityPos().getX());
        playerEntity.getPos().setY(packet.getEntityPos().getY());
        playerEntity.getPos().setZ(packet.getEntityPos().getZ());
        playerEntity.setPitch(packet.getPitch());
        playerEntity.setYaw(packet.getYaw());
    }

}
