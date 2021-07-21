package dev.cerus.nylium.server.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class KeybindComponent extends ChatComponent {

    private String keybind;

    public KeybindComponent(final String keybind) {
        this.keybind = keybind;
    }

    public String getKeybind() {
        return this.keybind;
    }

    public void setKeybind(final String keybind) {
        this.keybind = keybind;
    }

    @Override
    public JsonElement toJson() {
        final JsonObject jsonObject = super.toJson().getAsJsonObject();
        jsonObject.addProperty("keybind", this.keybind);
        return jsonObject;
    }

}
