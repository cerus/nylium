package dev.cerus.nylium.server;

import dev.cerus.nylium.io.session.PlayerSessionController;
import dev.cerus.nylium.server.world.World;

public class NyliumServer implements NyliumTicker.Tickable {

    public static final int MIN_PROTOCOL_VERSION = 755;
    public static final int MAX_PROTOCOL_VERSION = 756;
    public static final String PROTOCOL_NAME = "Nylium 1.17";

    private final World world;

    public NyliumServer(final PlayerSessionController sessionController) {
        this.world = new World(sessionController);
    }

    @Override
    public void tick() {
        this.world.tick();
    }

}
