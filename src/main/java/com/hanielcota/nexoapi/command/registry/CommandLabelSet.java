package com.hanielcota.nexoapi.command.registry;

import com.hanielcota.nexoapi.command.model.CommandAliases;
import com.hanielcota.nexoapi.command.model.CommandLabel;
import com.hanielcota.nexoapi.command.model.CommandName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * First-class collection for command labels (name + aliases).
 * Encapsulates all labels associated with a command.
 *
 * @since 1.0.0
 */
public final class CommandLabelSet implements Iterable<CommandLabel> {
    private final List<CommandLabel> labels;

    private CommandLabelSet(@NotNull List<CommandLabel> labels) {
        this.labels = Objects.requireNonNull(labels, "Labels cannot be null.");
    }

    /**
     * Creates a CommandLabelSet from a command name and aliases.
     *
     * @param name    the command name
     * @param aliases the command aliases
     * @return a new CommandLabelSet instance
     * @throws NullPointerException if any parameter is null
     */
    public static CommandLabelSet from(@NotNull CommandName name, @NotNull CommandAliases aliases) {
        Objects.requireNonNull(name, "Command name cannot be null.");
        Objects.requireNonNull(aliases, "Aliases cannot be null.");

        List<CommandLabel> labelList = new ArrayList<>();
        labelList.add(CommandLabel.from(name.value()));

        aliases.forEachNonBlank(alias -> labelList.add(CommandLabel.from(alias)));

        return new CommandLabelSet(List.copyOf(labelList));
    }

    /**
     * Checks if a label exists in this set.
     *
     * @param label the label to check
     * @return true if the label exists, false otherwise
     * @throws NullPointerException if label is null
     */
    public boolean contains(@NotNull CommandLabel label) {
        Objects.requireNonNull(label, "Label cannot be null.");
        return labels.contains(label);
    }

    /**
     * Checks if any label in this set conflicts with existing commands in the map.
     *
     * @param existingCommands the existing command label map
     * @return true if there's a conflict, false otherwise
     * @throws NullPointerException if existingCommands is null
     */
    public boolean hasConflictWith(@NotNull CommandLabelMap existingCommands) {
        Objects.requireNonNull(existingCommands, "Existing commands cannot be null.");
        return labels.stream().anyMatch(existingCommands::contains);
    }

    /**
     * Returns the first label (command name).
     *
     * @return the first label
     * @throws IllegalStateException if the set is empty
     */
    @NotNull
    public CommandLabel first() {
        if (labels.isEmpty()) {
            throw new IllegalStateException("Label set is empty.");
        }
        return labels.get(0);
    }

    /**
     * Returns the command name label (first label).
     *
     * @return the command name label
     */
    @NotNull
    public CommandLabel commandName() {
        return first();
    }

    @Override
    public Iterator<CommandLabel> iterator() {
        return labels.iterator();
    }

    /**
     * Returns all labels as an immutable list.
     *
     * @return an immutable list of labels
     */
    @NotNull
    public List<CommandLabel> all() {
        return List.copyOf(labels);
    }
}

