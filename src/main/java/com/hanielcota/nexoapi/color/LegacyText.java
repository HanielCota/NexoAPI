package com.hanielcota.nexoapi.color;

import java.util.Objects;

public record LegacyText(String value) {

    public LegacyText {
        Objects.requireNonNull(value, "Legacy text cannot be null.");

        value = value.trim();
    }

    public static LegacyText from(String rawValue) {
        Objects.requireNonNull(rawValue, "Legacy text cannot be null.");

        return new LegacyText(rawValue);
    }

    public static LegacyText fromNullable(String rawValue) {
        return new LegacyText(Objects.requireNonNullElse(rawValue, ""));
    }

    public boolean isEmpty() {
        return value.isBlank();
    }
}
