package com.hanielcota.nexoapi.color;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public enum NexoColorRole {

    PRIMARY(ColorHex.from("#00A3FF")),
    SECONDARY(ColorHex.from("#6366F1")),
    SUCCESS(ColorHex.from("#16A34A")),
    WARNING(ColorHex.from("#FACC15")),
    ERROR(ColorHex.from("#DC2626")),
    INFO(ColorHex.from("#38BDF8")),
    MUTED(ColorHex.from("#9CA3AF")),
    BACKGROUND(ColorHex.from("#020617")),
    HIGHLIGHT(ColorHex.from("#F97316")),
    TITLE(ColorHex.from("#E5E7EB")),
    SUBTITLE(ColorHex.from("#9CA3AF"));

    private final ColorHex colorHex;

    NexoColorRole(ColorHex colorHex) {
        Objects.requireNonNull(colorHex, "Color hex cannot be null.");
        this.colorHex = colorHex;
    }

    public ColorHex hex() {
        return colorHex;
    }

    public TextColor asTextColor() {
        String hexString = colorHex.value();

        return TextColor.fromCSSHexString(hexString);
    }

    public String openMiniMessageTag() {
        String hexWithoutHash = colorHex.withoutHash();

        return "<#" +
                hexWithoutHash +
                ">";
    }

    public String closeMiniMessageTag() {
        String hexWithoutHash = colorHex.withoutHash();

        return "</#" +
                hexWithoutHash +
                ">";
    }

    public MiniMessageText wrap(@NotNull MiniMessageText text) {
        Objects.requireNonNull(text, "MiniMessage text cannot be null.");

        String openTag = openMiniMessageTag();
        String closeTag = closeMiniMessageTag();
        String textValue = text.value();

        String wrapped = openTag +
                textValue +
                closeTag;
        return new MiniMessageText(wrapped);
    }
}
