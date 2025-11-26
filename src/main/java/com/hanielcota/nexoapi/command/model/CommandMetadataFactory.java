package com.hanielcota.nexoapi.command.model;

import com.hanielcota.nexoapi.command.annotation.NexoCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Factory for creating CommandMetadata from annotations.
 *
 * @since 1.0.0
 */
public final class CommandMetadataFactory {

    private CommandMetadataFactory() {
        throw new UnsupportedOperationException("Utility class.");
    }

    /**
     * Creates CommandMetadata from a @NexoCommand annotation.
     *
     * @param annotation the annotation instance
     * @return a new CommandMetadata instance
     */
    public static CommandMetadata from(@NotNull NexoCommand annotation) {
        Objects.requireNonNull(annotation, "@NexoCommand cannot be null.");

        var commandName = CommandName.from(annotation.name());
        var commandDescription = CommandDescription.from(annotation.description());
        var commandPermission = CommandPermission.from(annotation.permission());
        var aliases = extractAliases(annotation);
        var executionType = resolveExecutionType(annotation);
        var cooldown = CommandCooldown.from(annotation.cooldown());

        return new CommandMetadata(
                commandName,
                commandDescription,
                commandPermission,
                aliases,
                executionType,
                cooldown
        );
    }

    private static CommandAliases extractAliases(@NotNull NexoCommand annotation) {
        String[] rawAliases = annotation.aliases();
        if (rawAliases == null || rawAliases.length == 0) {
            return CommandAliases.empty();
        }
        return CommandAliases.from(rawAliases);
    }

    private static CommandExecutionType resolveExecutionType(@NotNull NexoCommand annotation) {
        if (annotation.async()) {
            return CommandExecutionType.ASYNC;
        }
        return CommandExecutionType.SYNC;
    }
}

