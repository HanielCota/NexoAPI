package com.hanielcota.nexoapi.color;

import java.util.Objects;

public record ColorHex(String value) {

    public ColorHex {
        Objects.requireNonNull(value, "Hex value cannot be null.");

        String trimmedValue = value.trim();
        boolean isBlank = trimmedValue.isEmpty();
        if (isBlank) {
            String message = "Hex value cannot be blank.";
            throw new IllegalArgumentException(message);
        }

        String normalizedValue = normalize(trimmedValue);
        validate(normalizedValue);

        value = normalizedValue;
    }

    public static ColorHex from(String rawValue) {
        return new ColorHex(rawValue);
    }

    private static String normalize(String rawValue) {
        boolean alreadyHasHashPrefix = rawValue.startsWith("#");
        if (alreadyHasHashPrefix) {
            return rawValue;
        }

        return "#" + rawValue;
    }

    private static void validate(String hexadecimalValue) {
        int expectedLength = 7;
        int length = hexadecimalValue.length();
        boolean invalidLength = length != expectedLength;

        if (invalidLength) {
            String message = "Hex value must be in format #RRGGBB: " + hexadecimalValue;
            throw new IllegalArgumentException(message);
        }

        int startIndex = 1;
        int endIndex = hexadecimalValue.length();

        for (int index = startIndex; index < endIndex; index++) {
            char character = hexadecimalValue.charAt(index);
            validateCharacter(character, hexadecimalValue);
        }
    }

    private static void validateCharacter(char character, String fullHexadecimalValue) {
        boolean isDigit = character >= '0' && character <= '9';
        if (isDigit) {
            return;
        }

        boolean isUppercaseHex = character >= 'A' && character <= 'F';
        if (isUppercaseHex) {
            return;
        }

        boolean isLowercaseHex = character >= 'a' && character <= 'f';
        if (isLowercaseHex) {
            return;
        }

        String message = "Invalid hex color: " + fullHexadecimalValue;
        throw new IllegalArgumentException(message);
    }

    public String withoutHash() {
        String hexadecimalValue = value;
        boolean startsWithHash = hexadecimalValue.startsWith("#");
        if (!startsWithHash) {
            return hexadecimalValue;
        }

        return hexadecimalValue.substring(1);
    }
}
