package dev.cerus.nylium.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects listeners and distributes events among them
 */
public class EventBus {

    private final List<BakedListener> listeners = new ArrayList<>();

    /**
     * Register a event listener
     *
     * @param listener A listener
     */
    public void registerListener(final Object listener) {
        this.listeners.add(BakedListener.bake(listener));
    }

    /**
     * Distribute the provided event among the listeners
     *
     * @param event The event to distribute
     */
    public void callEvent(final Event event) {
        this.listeners.forEach(bakedListener -> {
            try {
                bakedListener.handle(event);
            } catch (final Exception e) {
                e.printStackTrace();
                //TODO: Handle
            }
        });
    }

}
