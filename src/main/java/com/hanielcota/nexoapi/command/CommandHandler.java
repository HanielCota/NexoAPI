package com.hanielcota.nexoapi.command;

/**
 * Handler interface for command execution.
 * Classes implementing this interface must be annotated with {@link com.hanielcota.nexoapi.command.annotation.NexoCommand}.
 *
 * @since 1.0.0
 */
public interface CommandHandler {
    /**
     * Handles the command execution.
     *
     * @param context the command context containing sender and input
     */
    void handle(CommandContext context);
}
