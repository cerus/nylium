package dev.cerus.nylium.server.chat;

import com.google.gson.JsonObject;

public class ChatComponentStyle {

    private boolean bold;
    private boolean italic;
    private boolean underlined;
    private boolean strikethrough;
    private boolean obfuscated;
    private ChatColor color;

    public ChatComponentStyle() {
    }

    public boolean isBold() {
        return this.bold;
    }

    public ChatComponentStyle setBold(final boolean bold) {
        this.bold = bold;
        return this;
    }

    public boolean isItalic() {
        return this.italic;
    }

    public ChatComponentStyle setItalic(final boolean italic) {
        this.italic = italic;
        return this;
    }

    public boolean isUnderlined() {
        return this.underlined;
    }

    public ChatComponentStyle setUnderlined(final boolean underlined) {
        this.underlined = underlined;
        return this;
    }

    public boolean isStrikethrough() {
        return this.strikethrough;
    }

    public ChatComponentStyle setStrikethrough(final boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }

    public boolean isObfuscated() {
        return this.obfuscated;
    }

    public ChatComponentStyle setObfuscated(final boolean obfuscated) {
        this.obfuscated = obfuscated;
        return this;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public ChatComponentStyle setColor(final ChatColor color) {
        this.color = color;
        return this;
    }

    public void toJson(final JsonObject object) {
        object.addProperty("bold", this.bold);
        object.addProperty("italic", this.italic);
        object.addProperty("underlined", this.underlined);
        object.addProperty("strikethrough", this.strikethrough);
        object.addProperty("obfuscated", this.obfuscated);
        object.addProperty("color", this.color.getName());
    }

}
