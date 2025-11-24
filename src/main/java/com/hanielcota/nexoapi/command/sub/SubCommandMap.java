package com.hanielcota.nexoapi.command.sub;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * First-class collection for subcommands.
 * Encapsulates all operations related to subcommand management.
 *
 * @param values the map of subcommand names to invokers (immutable)
 * @since 1.0.0
 */
public record SubCommandMap(@NotNull Map<SubCommandName, SubCommandInvoker> values) {
    public SubCommandMap {
        Objects.requireNonNull(values, "SubCommandMap values cannot be null.");
        values = values.isEmpty() ? Map.of() : Map.copyOf(values);
    }

    /**
     * Creates an empty SubCommandMap.
     *
     * @return an empty SubCommandMap
     */
    public static SubCommandMap empty() {
        return new SubCommandMap(Map.of());
    }

    /**
     * Checks if there are no subcommands.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Finds a subcommand invoker by name.
     *
     * @param name the subcommand name
     * @return the invoker, or null if not found
     */
    @Nullable
    public SubCommandInvoker findBy(@Nullable SubCommandName name) {
        if (name == null) {
            return null;
        }
        return values.get(name);
    }

    /**
     * Returns all subcommand names as strings.
     *
     * @return a list of subcommand name strings
     */
    public List<String> namesAsStrings() {
        if (values.isEmpty()) {
            return List.of();
        }

        List<String> result = new ArrayList<>();
        for (SubCommandName subCommandName : values.keySet()) {
            result.add(subCommandName.value());
        }
        return List.copyOf(result);
    }

    /**
     * Returns subcommand names that start with the given prefix.
     *
     * @param prefix the prefix to match (case-insensitive)
     * @return a list of matching subcommand names
     */
    public List<String> namesStartingWith(@Nullable String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return namesAsStrings();
        }

        String lowerCasePrefix = prefix.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        for (SubCommandName subCommandName : values.keySet()) {
            String nameValue = subCommandName.value();
            String lowerCaseName = nameValue.toLowerCase(Locale.ROOT);
            if (lowerCaseName.startsWith(lowerCasePrefix)) {
                result.add(nameValue);
            }
        }

        return List.copyOf(result);
    }
}
