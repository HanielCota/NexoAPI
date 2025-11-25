package com.hanielcota.nexoapi.command;

import com.hanielcota.nexoapi.command.model.CommandInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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

    /**
     * Converts the command sender to a Player if it is one.
     *
     * @return the Player instance, or null if the sender is not a player
     */
    @Nullable
    public Player asPlayer() {
        if (sender instanceof Player player) {
            return player;
        }
        return null;
    }

    /**
     * Gets the command arguments as an immutable list.
     * This is the preferred method as it avoids array allocation.
     *
     * @return the command arguments as an immutable list
     */
    @NotNull
    public List<String> argsList() {
        return input.arguments().values();
    }

    /**
     * Gets the command arguments as a String array.
     * Note: This method allocates a new array on each call.
     * Prefer using {@link #argsList()} when possible.
     *
     * @return the command arguments as a String array
     */
    @NotNull
    public String[] args() {
        return input.arguments().values().toArray(new String[0]);
    }
}
