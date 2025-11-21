package com.hanielcota.nexoapi.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * An empty MiniMessageText instance.
     */
    public static final MiniMessageText EMPTY = new MiniMessageText(Component.empty());

    /**
     * Creates a MiniMessageText from a text string.
     * If the string is null or blank, returns the empty instance.
     *
     * @param text the MiniMessage-formatted text string, which may be null
     * @return a MiniMessageText instance, or EMPTY if the text is null or blank
     */
    public static MiniMessageText of(@Nullable String text) {
        if (text == null || text.isBlank()) {
            return EMPTY;
        }

        return new MiniMessageText(MINI_MESSAGE.deserialize(text));
    }

    /**
     * Returns the Component representation of this text.
     *
     * @return the Component (already parsed, no additional processing needed)
     */
    public Component toComponent() {
        return value;
    }
}