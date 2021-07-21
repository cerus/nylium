package dev.cerus.nylium.server.chat;

public enum ChatColor {

    BLACK("black", '0'),
    DARK_BLUE("dark_blue", '1'),
    DARK_GREEN("dark_green", '2'),
    DARK_CYAN("dark_cyan", '3'),
    DARK_RED("dark_red", '4'),
    PURPLE("dark_purple", '5'),
    GOLD("gold", '6'),
    GRAY("gray", '7'),
    DARK_GRAY("dark_gray", '8'),
    BLUE("blue", '9'),
    GREEN("green", 'a'),
    CYAN("cyan", 'b'),
    RED("red", 'c'),
    PINK("light_purple", 'd'),
    YELLOW("yellow", 'e'),
    WHITE("white", 'f');

    private final String name;
    private final char code;

    ChatColor(final String name, final char code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public char getCode() {
        return this.code;
    }

}
