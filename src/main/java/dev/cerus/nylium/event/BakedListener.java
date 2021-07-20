package dev.cerus.nylium.event;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A wrapper for listener objects that has extracted the available event handler methods
 */
class BakedListener {

    private final Object object;
    private final Map<Method, Class<? extends Event>> eventHandlerMethodMap;

    BakedListener(final Object object, final Map<Method, Class<? extends Event>> eventHandlerMethodMap) {
        this.object = object;
        this.eventHandlerMethodMap = eventHandlerMethodMap;
    }

    /**
     * Wrap the provided object and extract the event handler methods
     *
     * @param o The listener
     *
     * @return A baked (wrapped) listener
     */
    static BakedListener bake(final Object o) {
        final Class<?> clazz = o.getClass();
        final Map<Method, Class<? extends Event>> map = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Subscribe.class)) // Find methods with the @Subscribe annotation
                .filter(method -> method.getParameterCount() == 1) // Filter out methods with an invalid amount of parameters
                .filter(method -> Event.class.isAssignableFrom(method.getParameterTypes()[0])) // Filter out non event handler methods
                .collect(Collectors.toMap(method -> method, method -> (Class<? extends Event>) method.getParameterTypes()[0]));
        return new BakedListener(o, map);
    }

    /**
     * Handles provided events
     *
     * @param event The event to handle
     *
     * @throws Exception when the method can not be invoked or when the event handler throws an exception
     */
    void handle(final Event event) throws Exception {
        for (final Map.Entry<Method, Class<? extends Event>> entry : this.eventHandlerMethodMap.entrySet()) {
            if (entry.getValue() == event.getClass()) {
                final Method method = entry.getKey();
                method.setAccessible(true);
                method.invoke(this.object, event);
            }
        }
    }

}
