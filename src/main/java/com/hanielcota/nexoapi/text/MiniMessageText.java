package com.hanielcota.nexoapi.title.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record MiniMessageText(@NotNull String value) {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public Component toComponent() {
        if (value.isBlank()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(value);
    }

    public static MiniMessageText fromNullable(@Nullable String text) {
        return new MiniMessageText(Objects.requireNonNullElse(text, ""));
    }
}