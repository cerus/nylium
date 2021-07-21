package dev.cerus.nylium.server.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class TranslationComponent extends ChatComponent {

    private final List<ChatComponent> with;
    private String key;

    public TranslationComponent(final String key) {
        this(key, new ArrayList<>());
    }

    public TranslationComponent(final String key, final List<ChatComponent> with) {
        this.key = key;
        this.with = with;
    }

    public String getKey() {
        return this.key;
    }

    public <T extends ChatComponent> T setKey(final String key) {
        this.key = key;
        return (T) this;
    }

    public List<ChatComponent> getWith() {
        return this.with;
    }

    public <T extends ChatComponent> T with(final ChatComponent with) {
        this.with.add(with);
        return (T) this;
    }

    @Override
    public JsonElement toJson() {
        final JsonObject jsonObject = super.toJson().getAsJsonObject();
        jsonObject.addProperty("translate", this.key);
        if (!this.with.isEmpty()) {
            final JsonArray array = new JsonArray();
            this.with.forEach(chatComponent -> array.add(chatComponent.toJson()));
            jsonObject.add("with", array);
        }
        return jsonObject;
    }

}
