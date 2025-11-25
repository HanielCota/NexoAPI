package com.hanielcota.nexoapi.command.registry;

import com.hanielcota.nexoapi.command.model.CommandLabel;
import com.hanielcota.nexoapi.command.model.RegisteredCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * First-class collection for command labels and aliases.
 * Encapsulates all operations related to command lookup by label.
 *
 * @since 1.0.0
 */
public final class CommandLabelMap {
    private final ConcurrentMap<CommandLabel, RegisteredCommand> commands;

    private CommandLabelMap(@NotNull ConcurrentMap<CommandLabel, RegisteredCommand> commands) {
        this.commands = Objects.requireNonNull(commands, "Commands map cannot be null.");
    }

    /**
     * Creates an empty CommandLabelMap.
     *
     * @return an empty CommandLabelMap instance
     */
    public static CommandLabelMap empty() {
        return new CommandLabelMap(new ConcurrentHashMap<>());
    }

    /**
     * Checks if a label exists in the map.
     *
     * @param label the label to check
     * @return true if the label exists, false otherwise
     * @throws NullPointerException if label is null
     */
    public boolean contains(@NotNull CommandLabel label) {
        Objects.requireNonNull(label, "Label cannot be null.");
        return commands.containsKey(label);
    }

    /**
     * Finds a registered command by label.
     *
     * @param label the label to search for
     * @return the registered command, or null if not found
     * @throws NullPointerException if label is null
     */
    @Nullable
    public RegisteredCommand find(@NotNull CommandLabel label) {
        Objects.requireNonNull(label, "Label cannot be null.");
        return commands.get(label);
    }

    /**
     * Stores a command with a single label.
     *
     * @param label   the label to store
     * @param command the command to store
     * @throws NullPointerException if any parameter is null
     */
    public void store(@NotNull CommandLabel label, @NotNull RegisteredCommand command) {
        Objects.requireNonNull(label, "Label cannot be null.");
        Objects.requireNonNull(command, "Command cannot be null.");
        commands.put(label, command);
    }

    /**
     * Stores a command with multiple labels (name + aliases).
     *
     * @param labels  the labels to store
     * @param command the command to store
     * @throws NullPointerException if any parameter is null
     */
    public void storeAll(@NotNull CommandLabelSet labels, @NotNull RegisteredCommand command) {
        Objects.requireNonNull(labels, "Labels cannot be null.");
        Objects.requireNonNull(command, "Command cannot be null.");
        labels.forEach(label -> store(label, command));
    }

    /**
     * Checks if any label in the set conflicts with existing commands.
     *
     * @param labels the labels to check
     * @return true if there's a conflict, false otherwise
     * @throws NullPointerException if labels is null
     */
    public boolean hasConflict(@NotNull CommandLabelSet labels) {
        Objects.requireNonNull(labels, "Labels cannot be null.");
        return labels.hasConflictWith(this);
    }
}

