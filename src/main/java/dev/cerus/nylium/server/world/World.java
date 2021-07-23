package dev.cerus.nylium.server.world;

import dev.cerus.nylium.io.session.PlayerSessionController;
import dev.cerus.nylium.server.NyliumTicker;
import dev.cerus.nylium.server.entity.Entity;
import java.util.HashSet;
import java.util.Set;

public class World implements NyliumTicker.Tickable {

    private final Set<Entity> entities = new HashSet<>();
    private final PlayerSessionController sessionController;

    public World(final PlayerSessionController sessionController) {
        this.sessionController = sessionController;
    }

    @Override
    public void tick() {
        this.entities.forEach(NyliumTicker.Tickable::tick);
        this.sessionController.getPlayerSessions().forEach(session -> {
            if (session.getPlayerEntity() != null) {
                session.getPlayerEntity().tick();
            }
        });
    }

}
