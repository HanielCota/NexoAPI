package com.hanielcota.nexoapi.command.execution;

import com.hanielcota.nexoapi.command.model.RegisteredCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;

/**
 * Service responsible for handling command execution exceptions.
 *
 * @since 1.0.0
 */
public final class ExceptionHandler {
    private final RegisteredCommand registeredCommand;
    private final org.bukkit.plugin.Plugin ownerPlugin;

    private ExceptionHandler(
            @NotNull RegisteredCommand registeredCommand,
            @NotNull org.bukkit.plugin.Plugin ownerPlugin
    ) {
        this.registeredCommand = Objects.requireNonNull(registeredCommand, "Registered command cannot be null.");
        this.ownerPlugin = Objects.requireNonNull(ownerPlugin, "Plugin cannot be null.");
    }

    /**
     * Creates a new ExceptionHandler for the given command and plugin.
     *
     * @param registeredCommand the registered command
     * @param ownerPlugin       the plugin instance
     * @return a new ExceptionHandler instance
     * @throws NullPointerException if any parameter is null
     */
    public static ExceptionHandler create(
            @NotNull RegisteredCommand registeredCommand,
            @NotNull org.bukkit.plugin.Plugin ownerPlugin
    ) {
        Objects.requireNonNull(registeredCommand, "Registered command cannot be null.");
        Objects.requireNonNull(ownerPlugin, "Plugin cannot be null.");
        return new ExceptionHandler(registeredCommand, ownerPlugin);
    }

    /**
     * Handles a command execution exception.
     *
     * @param sender    the command sender
     * @param exception the exception that occurred
     * @throws NullPointerException if any parameter is null
     */
    public void handle(@NotNull CommandSender sender, @NotNull Exception exception) {
        Objects.requireNonNull(sender, "Sender cannot be null.");
        Objects.requireNonNull(exception, "Exception cannot be null.");

        sendErrorMessage(sender, exception);
        logException(sender, exception);
    }

    private void sendErrorMessage(@NotNull CommandSender sender, @NotNull Exception exception) {
        String message = "§cErro ao executar comando: §7" + exception.getMessage();
        sender.sendMessage(message);
    }

    private void logException(@NotNull CommandSender sender, @NotNull Exception exception) {
        var metadata = registeredCommand.definition().metadata();
        var commandName = metadata.name();
        String commandNameValue = commandName.value();

        ownerPlugin.getLogger().severe("Erro ao executar comando '" + commandNameValue +
                "' enviado por " + sender.getName() + ":");
        ownerPlugin.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
    }
}

