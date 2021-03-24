package dev.gigafyde.apollo.utils;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

public enum Emoji {
    HEARTBEAT("\uD83D\uDC93"),
    PINGPONG("\uD83C\uDFD3"),
    SUCCESS("\u2705"),
    ERROR("\u274C"),
    INFO("\u2139"),
    URL("\uD83D\uDD17");

    private final String emote;

    Emoji(String emote) {
        this.emote = emote;
    }

    @Override
    public String toString() {
        return emote;
    }
}
