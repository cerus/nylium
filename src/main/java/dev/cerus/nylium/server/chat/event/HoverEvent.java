package dev.cerus.nylium.server.chat.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.cerus.nylium.server.chat.ChatComponent;

public class HoverEvent {

    private final Type type;
    private final ChatComponent value;

    private HoverEvent(final Type type, final ChatComponent value) {
        this.type = type;
        this.value = value;
    }

    public static HoverEvent showText(final ChatComponent value) {
        return of(Type.SHOW_TEXT, value);
    }

    public static HoverEvent showEntity(final ChatComponent value) {
        return of(Type.SHOW_ENTITY, value);
    }

    public static HoverEvent showItem(final ChatComponent value) {
        return of(Type.SHOW_ITEM, value);
    }

    public static HoverEvent of(final Type type, final ChatComponent value) {
        return new HoverEvent(type, value);
    }

    public Type getType() {
        return this.type;
    }

    public ChatComponent getValue() {
        return this.value;
    }

    public JsonElement toJson() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", this.type.name().toLowerCase());
        jsonObject.add("value", this.value.toJson());
        return jsonObject;
    }

    public static enum Type {
        SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY
    }

}
