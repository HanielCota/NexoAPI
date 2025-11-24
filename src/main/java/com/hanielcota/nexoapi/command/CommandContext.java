package com.hanielcota.nexoapi.command;

import com.hanielcota.nexoapi.command.model.CommandInput;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents the context of a command execution.
 * Contains the sender and the parsed command input.
 *
 * @param sender the command sender (player, console, etc.)
 * @param input  the parsed command input (label and arguments)
 * @since 1.0.0
 */
public record CommandContext(
        @NotNull CommandSender sender,
        @NotNull CommandInput input
) {
    public CommandContext {
        Objects.requireNonNull(sender, "Command sender cannot be null.");
        Objects.requireNonNull(input, "Command input cannot be null.");
    }
}
