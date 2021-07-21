package dev.cerus.nylium.server.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.cerus.nylium.server.chat.event.ClickEvent;
import dev.cerus.nylium.server.chat.event.HoverEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class ChatComponent {

    protected ChatComponentStyle style = null;
    protected String insertion = null;
    protected HoverEvent hoverEvent = null;
    protected ClickEvent clickEvent = null;
    protected List<ChatComponent> siblings = new ArrayList<>();

    public ChatComponentStyle getStyle() {
        return this.style;
    }

    public <T extends ChatComponent> T setStyle(final ChatComponentStyle style) {
        this.style = style;
        return (T) this;
    }

    public String getInsertion() {
        return this.insertion;
    }

    public <T extends ChatComponent> T setInsertion(final String insertion) {
        this.insertion = insertion;
        return (T) this;
    }

    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }

    public <T extends ChatComponent> T setHoverEvent(final HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return (T) this;
    }

    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }

    public <T extends ChatComponent> T setClickEvent(final ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return (T) this;
    }

    public List<ChatComponent> getSiblings() {
        return this.siblings;
    }

    public <T extends ChatComponent> T addSibling(final ChatComponent sibling) {
        this.siblings.add(sibling);
        return (T) this;
    }

    public JsonElement toJson() {
        final JsonObject jsonObject = new JsonObject();
        if (this.style != null) {
            this.style.toJson(jsonObject);
        }
        if (this.insertion != null) {
            jsonObject.addProperty("insertion", this.insertion);
        }
        if (this.clickEvent != null) {
            jsonObject.add("clickEvent", this.clickEvent.toJson());
        }
        if (this.hoverEvent != null) {
            jsonObject.add("hoverEvent", this.hoverEvent.toJson());
        }
        if (!this.siblings.isEmpty()) {
            final JsonArray array = new JsonArray();
            this.siblings.forEach(chatComponent -> array.add(chatComponent.toJson()));
            jsonObject.add("extra", array);
        }
        return jsonObject;
    }

}
