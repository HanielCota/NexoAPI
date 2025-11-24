package com.hanielcota.nexoapi.color;

import java.util.Objects;

public record MiniMessageText(String value) {

    public MiniMessageText {
        Objects.requireNonNull(value, "MiniMessage text cannot be null.");

        value = value.trim();
    }

    public static MiniMessageText from(String rawValue) {
        Objects.requireNonNull(rawValue, "MiniMessage text cannot be null.");

        return new MiniMessageText(rawValue);
    }

    public static MiniMessageText empty() {
        return new MiniMessageText("");
    }

    public boolean isEmpty() {
        return value.isBlank();
    }
}
