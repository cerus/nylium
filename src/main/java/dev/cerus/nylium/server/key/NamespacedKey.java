package dev.cerus.nylium.server.key;

/**
 * Simple class for Minecraft's identifiers (namespace:value (eg minecraft:player))
 */
public class NamespacedKey {

    public static final String DEFAULT_NAMESPACE = "minecraft";

    private final String namespace;
    private final String value;

    private NamespacedKey(final String namespace, final String value) {
        this.namespace = namespace;
        this.value = value;
    }

    public static NamespacedKey of(final String namespace, final String value) {
        return new NamespacedKey(namespace, value);
    }

    public static NamespacedKey def(final String value) {
        return of(DEFAULT_NAMESPACE, value);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.value;
    }

}
