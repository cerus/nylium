package dev.cerus.nylium.server.chat.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ClickEvent {

    private final Type type;
    private final String value;

    private ClickEvent(final Type type, final String value) {
        this.type = type;
        this.value = value;
    }

    public static ClickEvent openUrl(final String url) {
        return of(Type.OPEN_URL, url);
    }

    public static ClickEvent runCommand(final String cmd) {
        return of(Type.RUN_COMMAND, cmd);
    }

    public static ClickEvent suggestCommand(final String value) {
        return of(Type.SUGGEST_COMMAND, value);
    }

    public static ClickEvent changePage(final String value) {
        return of(Type.CHANGE_PAGE, value);
    }

    public static ClickEvent copyToClipboard(final String value) {
        return of(Type.COPY_TO_CLIPBOARD, value);
    }

    public static ClickEvent of(final Type type, final String value) {
        return new ClickEvent(type, value);
    }

    public Type getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public JsonElement toJson() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", this.type.name().toLowerCase());
        jsonObject.addProperty("value", this.value);
        return jsonObject;
    }

    public static enum Type {
        OPEN_URL, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD
    }

}
