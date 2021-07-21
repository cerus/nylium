package dev.cerus.nylium.server.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class StringComponent extends ChatComponent {

    private String text;

    public StringComponent(final String text) {
        this.text = text;
    }

    public static StringComponent of(final String text) {
        return new StringComponent(text);
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    @Override
    public JsonElement toJson() {
        final JsonObject jsonObject = super.toJson().getAsJsonObject();
        jsonObject.addProperty("text", this.text);
        return jsonObject;
    }

}
