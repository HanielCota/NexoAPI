package com.hanielcota.nexoapi.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents text that has been parsed from MiniMessage format into a Component.
 * MiniMessage is a text format that supports colors, decorations, and other formatting.
 * <p>
 * This record stores the already-parsed Component to avoid re-parsing on every access.
 * </p>
 *
 * @param value the parsed Adventure Component
 * @since 1.0.0
 */
public record MiniMessageText(@NotNull Component value) {

    /**
     * An empty MiniMessageText instance.
     */
    public static final MiniMessageText EMPTY = new MiniMessageText(Component.empty());
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('§')
            .hexCharacter('#')
            .build();

    /**
     * Creates a MiniMessageText from a text string.
     * If the string is null or blank, returns the empty instance.
     * <p>
     * This method automatically detects and converts legacy color codes (using §) to MiniMessage format.
     * If the string contains legacy codes, they will be converted automatically.
     * </p>
     *
     * @param text the MiniMessage-formatted text string or legacy-formatted text, which may be null
     * @return a MiniMessageText instance, or EMPTY if the text is null or blank
     */
    public static MiniMessageText of(@Nullable String text) {
        if (text == null || text.isBlank()) {
            return EMPTY;
        }

        // Detect if the text contains legacy color codes (§)
        if (containsLegacyCodes(text)) {
            // Convert legacy codes directly to Component
            Component legacyComponent = LEGACY_SERIALIZER.deserialize(text);
            return new MiniMessageText(legacyComponent);
        }

        // Parse as regular MiniMessage
        return new MiniMessageText(MINI_MESSAGE.deserialize(text));
    }

    /**
     * Checks if a string contains legacy color codes (using § character).
     * Validates that the § character is followed by a valid hex digit (0-9, a-f, A-F)
     * or a valid color/format code (0-9, a-f, k-o, r, x).
     *
     * @param text the text to check
     * @return true if the text contains valid legacy color codes
     */
    private static boolean containsLegacyCodes(@NotNull String text) {
        int index = text.indexOf('§');
        if (index < 0 || index >= text.length() - 1) {
            return false;
        }
        
        // Check if § is followed by a valid character
        char nextChar = text.charAt(index + 1);
        // Valid legacy codes: 0-9, a-f, k-o, r, x (for hex colors)
        return (nextChar >= '0' && nextChar <= '9') ||
               (nextChar >= 'a' && nextChar <= 'f') ||
               (nextChar >= 'A' && nextChar <= 'F') ||
               (nextChar >= 'k' && nextChar <= 'o') ||
               nextChar == 'r' || nextChar == 'x';
    }

    /**
     * Returns the Component representation of this text.
     *
     * @return the Component (already parsed, no additional processing needed)
     */
    public Component toComponent() {
        return value;
    }

    /**
     * Serializes this Component back to a MiniMessage string format.
     * This is useful when you need to manipulate the text as a string
     * (e.g., wrapping it with additional MiniMessage tags).
     *
     * @return the MiniMessage string representation of this Component
     */
    @NotNull
    public String toMiniMessageString() {
        return MINI_MESSAGE.serialize(value);
    }
}