package dev.cerus.nylium.io.session;

import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.implementation.SessionAddedEvent;
import dev.cerus.nylium.event.implementation.SessionRemovedEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import java.util.HashSet;
import java.util.Set;

public class PlayerSessionController {

    private final Set<PlayerSession> playerSessions = new HashSet<>();
    private final EventBus eventBus;

    public PlayerSessionController(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public PlayerSession getByName(final String name) {
        return null;
    }

    public PlayerSession getByCtx(final ChannelHandlerContext context) {
        return this.playerSessions.stream()
                .filter(session -> session.getContext() == context)
                .findAny()
                .orElse(null);
    }

    public PlayerSession getByChId(final ChannelId channelId) {
        return this.playerSessions.stream()
                .filter(session -> session.getContext().channel().id() == channelId)
                .findAny()
                .orElse(null);
    }

    public void addSession(final PlayerSession session) {
        this.playerSessions.add(session);
        this.eventBus.callEvent(new SessionAddedEvent(session));
    }

    public void removeSession(final PlayerSession session) {
        this.playerSessions.remove(session);
        this.eventBus.callEvent(new SessionRemovedEvent(session));
    }

}
