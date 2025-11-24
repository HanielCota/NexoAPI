package com.hanielcota.nexoapi.command.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents the metadata of a command.
 * Contains all information about a command definition.
 *
 * @param name          the command name
 * @param description   the command description
 * @param permission    the command permission
 * @param aliases       the command aliases (immutable list)
 * @param executionType the execution type (sync/async)
 * @since 1.0.0
 */
public record CommandMetadata(
        @NotNull CommandName name,
        @NotNull CommandDescription description,
        @NotNull CommandPermission permission,
        @NotNull List<String> aliases,
        @NotNull CommandExecutionType executionType
) {
    public CommandMetadata {
        Objects.requireNonNull(name, "Command name cannot be null.");
        Objects.requireNonNull(description, "Command description cannot be null.");
        Objects.requireNonNull(permission, "Command permission cannot be null.");
        Objects.requireNonNull(executionType, "Execution type cannot be null.");
        Objects.requireNonNull(aliases, "Command aliases cannot be null.");
        aliases = List.copyOf(aliases);
    }
}

