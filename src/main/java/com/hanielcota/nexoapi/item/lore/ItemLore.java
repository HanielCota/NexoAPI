package com.hanielcota.nexoapi.item.lore;

import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a list of formatted lore lines for an item.
 * Uses MiniMessage formatting and removes italic decoration.
 *
 * @param components the list of Component lines for the lore
 * @since 1.0.0
 */
public record ItemLore(@NotNull List<Component> components) {

    private static final ItemLore EMPTY = new ItemLore(List.of());

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

        List<Component> converted = rawLines.stream()
                .map(ItemLore::toComponentLine)
                .toList();

        return new ItemLore(converted);
    }

    private static Component toComponentLine(String line) {
        MiniMessageText text = MiniMessageText.of(line);
        Component component = text.toComponent();
        return stripItalic(component);
    }

    private static Component stripItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Checks if this lore has any content.
     *
     * @return true if the lore has at least one line, false otherwise
     */
    public boolean hasContent() {
        return !components.isEmpty();
    }
}
