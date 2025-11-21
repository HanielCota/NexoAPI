package com.hanielcota.nexoapi.item.property;

import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the lore (description lines) of an item.
 * Each line supports MiniMessage format and has italic decoration removed.
 *
 * @param lines the list of Component lines for the lore
 * @since 1.0.0
 */
public record ItemLore(@NotNull List<Component> lines) {

    private static final ItemLore EMPTY = new ItemLore(Collections.emptyList());

    /**
     * Creates an empty ItemLore.
     *
     * @return an empty ItemLore instance
     */
    public static ItemLore empty() {
        return EMPTY;
    }

    /**
     * Creates an ItemLore from a list of raw text lines.
     * Each line will be parsed as MiniMessage and have italic decoration removed.
     *
     * @param rawLines the raw text lines, which may be null or empty
     * @return an ItemLore instance, or empty if the input is null or empty
     */
    public static ItemLore from(@Nullable List<String> rawLines) {
        if (rawLines == null || rawLines.isEmpty()) {
            return EMPTY;
        }

        final var components = rawLines.stream()
                .map(line -> MiniMessageText.of(line).toComponent())
                .map(component -> component.decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toList());

        return new ItemLore(components);
    }

    /**
     * Checks if this lore has any content.
     *
     * @return true if the lore has at least one line, false otherwise
     */
    public boolean hasContent() {
        return !lines.isEmpty();
    }
}