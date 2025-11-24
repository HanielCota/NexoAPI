package com.hanielcota.nexoapi.menu.property;

import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents the title of a menu.
 * Supports both plain text and MiniMessage formatting.
 *
 * @param value the Component representation of the title
 * @since 1.0.0
 */
public record MenuTitle(@NotNull Component value) {

    public MenuTitle {
        Objects.requireNonNull(value, "value cannot be null");
    }

    /**
     * Creates a MenuTitle from plain text.
     *
     * @param text the plain text for the title
     * @return a new MenuTitle instance
     * @throws NullPointerException if text is null
     */
    public static MenuTitle ofPlain(@NotNull String text) {
        Objects.requireNonNull(text, "text cannot be null");

        Component component = Component.text(text);
        return new MenuTitle(component);
    }

    /**
     * Creates a MenuTitle from MiniMessage formatted text.
     *
     * @param miniMessage the MiniMessage formatted string
     * @return a new MenuTitle instance
     * @throws NullPointerException if miniMessage is null
     */
    public static MenuTitle ofMiniMessage(@NotNull String miniMessage) {
        Objects.requireNonNull(miniMessage, "miniMessage cannot be null");

        MiniMessageText miniMessageText = MiniMessageText.of(miniMessage);
        Component component = miniMessageText.toComponent();

        return new MenuTitle(component);
    }
}

