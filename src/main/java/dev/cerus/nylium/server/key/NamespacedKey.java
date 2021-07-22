package dev.cerus.nylium.server.key;

import java.util.Objects;

/**
 * Simple class for Minecraft's identifiers (namespace:value (eg minecraft:player))
 */
public class NamespacedKey {

    public static final String MINECRAFT_NAMESPACE = "minecraft";
    public static final String NYLIUM_NAMESPACE = "nylium";

    private final String namespace;
    private final String value;

    private NamespacedKey(final String namespace, final String value) {
        this.namespace = namespace;
        this.value = value;
    }

    public static NamespacedKey of(final String namespace, final String value) {
        return new NamespacedKey(namespace, value);
    }

    public static NamespacedKey of(final String key) {
        final String[] split = key.split(":");
        return new NamespacedKey(split[0], split[1]);
    }

    public static NamespacedKey mc(final String value) {
        return of(MINECRAFT_NAMESPACE, value);
    }

    public static NamespacedKey nylium(final String value) {
        return of(NYLIUM_NAMESPACE, value);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final NamespacedKey that = (NamespacedKey) o;
        return this.namespace.equals(that.namespace) && this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.namespace, this.value);
    }

}
