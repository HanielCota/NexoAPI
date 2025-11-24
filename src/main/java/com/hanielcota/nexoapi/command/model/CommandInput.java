package com.hanielcota.nexoapi.command.model;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents the parsed input of a command execution.
 * Contains the command label and its arguments.
 *
 * @param label     the command label
 * @param arguments the command arguments
 * @since 1.0.0
 */
public record CommandInput(
        @NotNull CommandLabel label,
        @NotNull CommandArguments arguments
) {
    public CommandInput {
        Objects.requireNonNull(label, "Command label cannot be null.");
        Objects.requireNonNull(arguments, "Command arguments cannot be null.");
    }

    /**
     * Creates a CommandInput from raw Bukkit command parameters.
     *
     * @param rawLabel     the raw command label
     * @param rawArguments the raw command arguments array
     * @return a new CommandInput instance
     */
    public static CommandInput from(String rawLabel, String[] rawArguments) {
        var commandLabel = CommandLabel.from(rawLabel);
        var commandArguments = buildArguments(rawArguments);
        return new CommandInput(commandLabel, commandArguments);
    }

    private static CommandArguments buildArguments(String[] rawArguments) {
        if (rawArguments == null || rawArguments.length == 0) {
            return CommandArguments.empty();
        }
        List<String> argumentsList = Arrays.asList(rawArguments);
        return new CommandArguments(argumentsList);
    }
}
