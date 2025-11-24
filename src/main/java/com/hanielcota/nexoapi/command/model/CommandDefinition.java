package com.hanielcota.nexoapi.command.model;

import com.hanielcota.nexoapi.command.sub.SubCommandMap;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a complete command definition.
 * Contains the root command metadata and all its subcommands.
 *
 * @param metadata    the root command metadata
 * @param subCommands the map of subcommands
 * @since 1.0.0
 */
public record CommandDefinition(
        @NotNull CommandMetadata metadata,
        @NotNull SubCommandMap subCommands
) {
    public CommandDefinition {
        Objects.requireNonNull(metadata, "Command metadata cannot be null.");
        Objects.requireNonNull(subCommands, "SubCommandMap cannot be null.");
    }
}
