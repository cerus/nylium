package dev.cerus.nylium.server.tick;

import dev.cerus.nylium.event.Subscribe;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.event.implementation.SessionRemovedEvent;
import dev.cerus.nylium.io.packet.implementation.KeepAlivePacketOut;
import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.io.session.PlayerSessionController;
import dev.cerus.nylium.server.NyliumTicker;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Sends frequent keep alive packets and kicks clients that don't respond within 30 seconds (30 * 20 ticks)
 */
public class KeepAliveTickable implements NyliumTicker.Tickable {

    private static final Logger LOGGER = Logger.getLogger(KeepAliveTickable.class.getName());

    private final Map<Integer, Integer> ticksWithoutKeepaliveMap = new ConcurrentHashMap<>();
    private final PlayerSessionController sessionController;
    private int nextKeepalive = 0;

    public KeepAliveTickable(final PlayerSessionController sessionController) {
        this.sessionController = sessionController;
    }

    @Override
    public void tick() {
        if (this.nextKeepalive-- > 0) {
            return;
        }

        for (final PlayerSession playerSession : this.sessionController.getPlayerSessions()) {
            if (playerSession.getState() != PlayerSession.SessionState.PLAY) {
                continue;
            }

            if (this.ticksWithoutKeepaliveMap.getOrDefault(playerSession.getId(), 0) == 0) {
                playerSession.sendPacket(new KeepAlivePacketOut(System.currentTimeMillis()));
            }
            this.ticksWithoutKeepaliveMap.put(playerSession.getId(), this.ticksWithoutKeepaliveMap.getOrDefault(playerSession.getId(), 0) + 1);

            if (this.ticksWithoutKeepaliveMap.get(playerSession.getId()) >= 30) {
                // Kick after 30s without keepalive
                playerSession.disconnect("Timeout");
                LOGGER.info(playerSession.getGameProfile().getUsername() + " timed out");
            }
        }

        this.nextKeepalive = 20;
    }

    @Subscribe
    public void handleSessionRemoval(final SessionRemovedEvent event) {
        this.ticksWithoutKeepaliveMap.remove(event.getSession().getId());
    }

    @Subscribe
    public void handlePacket(final PacketReceivedEvent event) {
        this.ticksWithoutKeepaliveMap.put(event.getSession().getId(), 0);
    }

}
