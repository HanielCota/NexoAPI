package com.hanielcota.nexoapi.item;

import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record ItemLore(@NotNull List<Component> lines) {

    private static final ItemLore EMPTY = new ItemLore(Collections.emptyList());

    public static ItemLore empty() {
        return EMPTY;
    }

    public static ItemLore from(@Nullable List<String> rawLines) {
        if (rawLines == null || rawLines.isEmpty()) {
            return EMPTY;
        }

        final var components = rawLines.stream()
                .map(line -> MiniMessageText.fromNullable(line).toComponent())
                .map(component -> component.decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toList());

        return new ItemLore(components);
    }

    public boolean hasContent() {
        return !lines.isEmpty();
    }
}