package com.hanielcota.nexoapi.command.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents the metadata of a command.
 * Contains all information about a command definition.
 *
 * @param name          the command name
 * @param description   the command description
 * @param permission    the command permission
 * @param aliases       the command aliases (first-class collection)
 * @param executionType the execution type (sync/async)
 * @param cooldown      the command cooldown in seconds
 * @since 1.0.0
 */
public record CommandMetadata(
        @NotNull CommandName name,
        @NotNull CommandDescription description,
        @NotNull CommandPermission permission,
        @NotNull CommandAliases aliases,
        @NotNull CommandExecutionType executionType,
        @NotNull CommandCooldown cooldown
) {
    public CommandMetadata {
        Objects.requireNonNull(name, "Command name cannot be null.");
        Objects.requireNonNull(description, "Command description cannot be null.");
        Objects.requireNonNull(permission, "Command permission cannot be null.");
        Objects.requireNonNull(executionType, "Execution type cannot be null.");
        Objects.requireNonNull(aliases, "Command aliases cannot be null.");
        Objects.requireNonNull(cooldown, "Command cooldown cannot be null.");
    }
}

