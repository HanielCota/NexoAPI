package com.hanielcota.nexoapi.command.model;

import com.hanielcota.nexoapi.command.CommandHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a registered command with its definition and handler.
 *
 * @param definition the command definition
 * @param handler    the command handler instance
 * @since 1.0.0
 */
public record RegisteredCommand(
        @NotNull CommandDefinition definition,
        @NotNull CommandHandler handler
) {
    public RegisteredCommand {
        Objects.requireNonNull(definition, "Command definition cannot be null.");
        Objects.requireNonNull(handler, "Command handler cannot be null.");
    }
}

