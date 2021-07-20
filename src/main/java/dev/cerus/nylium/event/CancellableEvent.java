package dev.cerus.nylium.event;

/**
 * Base class for cancellable events
 */
public abstract class CancellableEvent extends Event {

    private boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

}
