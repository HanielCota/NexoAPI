package com.hanielcota.nexoapi.command;

import com.hanielcota.nexoapi.command.annotation.NexoCommand;
import com.hanielcota.nexoapi.command.model.*;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for dynamic command registration.
 * Registers commands directly to Bukkit's CommandMap without requiring plugin.yml entries.
 *
 * @since 1.0.0
 */
public final class CommandRegistry {
    private final Plugin ownerPlugin;
    private final Map<String, RegisteredCommand> commandsByLabel;

    private CommandRegistry(@NotNull Plugin ownerPlugin, @NotNull Map<String, RegisteredCommand> commandsByLabel) {
        this.ownerPlugin = ownerPlugin;
        this.commandsByLabel = commandsByLabel;
    }

    /**
     * Creates a new CommandRegistry for the given plugin.
     *
     * @param ownerPlugin the plugin that owns this registry
     * @return a new CommandRegistry instance
     */
    public static CommandRegistry create(@NotNull Plugin ownerPlugin) {
        Objects.requireNonNull(ownerPlugin, "Plugin cannot be null.");
        Map<String, RegisteredCommand> internalMap = new ConcurrentHashMap<>();
        return new CommandRegistry(ownerPlugin, internalMap);
    }

    /**
     * Registers a command handler.
     * The handler class must be annotated with @NexoCommand.
     *
     * @param handler the command handler instance
     * @throws IllegalArgumentException if the handler is not annotated with @NexoCommand
     * @throws IllegalStateException    if registration fails
     */
    public void register(@NotNull CommandHandler handler) {
        Objects.requireNonNull(handler, "Command handler cannot be null.");

        Class<?> handlerClass = handler.getClass();
        NexoCommand annotation = handlerClass.getAnnotation(NexoCommand.class);
        if (annotation == null) {
            String message = "Handler class must be annotated with @NexoCommand: " + handlerClass.getName();
            throw new IllegalArgumentException(message);
        }

        var commandDefinition = CommandDefinitionFactory.from(annotation, handlerClass);
        validateCommandNotRegistered(commandDefinition);
        var registeredCommand = new RegisteredCommand(commandDefinition, handler);

        try {
            registerOnPlatform(registeredCommand);
            storeCommandByLabel(commandDefinition, registeredCommand);
        } catch (Exception exception) {
            String message = "Failed to register command: " + commandDefinition.metadata().name().value();
            throw new IllegalStateException(message, exception);
        }
    }

    private void validateCommandNotRegistered(@NotNull CommandDefinition commandDefinition) {
        var metadata = commandDefinition.metadata();
        var commandName = metadata.name();
        String commandNameValue = commandName.value();

        if (commandsByLabel.containsKey(commandNameValue)) {
            String message = "Command already registered: " + commandNameValue;
            throw new IllegalStateException(message);
        }

        var aliases = metadata.aliases();
        for (String alias : aliases) {
            String lowerCaseAlias = alias.toLowerCase();
            if (commandsByLabel.containsKey(lowerCaseAlias)) {
                String message = "Alias already registered: " + alias;
                throw new IllegalStateException(message);
            }
        }
    }

    private void storeCommandByLabel(
            @NotNull CommandDefinition commandDefinition,
            @NotNull RegisteredCommand registeredCommand
    ) {
        var metadata = commandDefinition.metadata();
        var commandName = metadata.name();
        String commandNameValue = commandName.value();
        commandsByLabel.put(commandNameValue, registeredCommand);
    }

    private void registerOnPlatform(@NotNull RegisteredCommand registeredCommand) {
        var definition = registeredCommand.definition();
        var metadata = definition.metadata();
        var commandName = metadata.name();
        String label = commandName.value();

        PluginCommand pluginCommand = createPluginCommand(label, metadata);
        PaperCommandExecutor executor = new PaperCommandExecutor(registeredCommand, ownerPlugin);
        configurePluginCommand(pluginCommand, executor);

        CommandMap commandMap = findCommandMap();
        String pluginName = ownerPlugin.getName();
        boolean registered = commandMap.register(pluginName, pluginCommand);
        if (!registered) {
            String message = "Failed to register command: " + label;
            throw new IllegalStateException(message);
        }
    }

    private void configurePluginCommand(
            @NotNull PluginCommand pluginCommand,
            @NotNull PaperCommandExecutor executor
    ) {
        pluginCommand.setExecutor(executor);
        pluginCommand.setTabCompleter(executor);
    }

    private PluginCommand createPluginCommand(
            @NotNull String label,
            @NotNull CommandMetadata metadata
    ) {
        try {
            PluginCommand pluginCommand = instantiatePluginCommand(label);
            configureCommandMetadata(pluginCommand, metadata);
            return pluginCommand;

        } catch (ReflectiveOperationException exception) {
            String message = "Failed to create PluginCommand for label: " + label;
            throw new IllegalStateException(message, exception);
        }
    }

    private PluginCommand instantiatePluginCommand(@NotNull String label) throws ReflectiveOperationException {
        Constructor<PluginCommand> constructor = PluginCommand.class
                .getDeclaredConstructor(String.class, Plugin.class);
        constructor.setAccessible(true);
        return constructor.newInstance(label, ownerPlugin);
    }

    private void configureCommandMetadata(
            @NotNull PluginCommand pluginCommand,
            @NotNull CommandMetadata metadata
    ) {
        var description = metadata.description();
        String descriptionValue = description.value();
        if (!descriptionValue.isEmpty()) {
            pluginCommand.setDescription(descriptionValue);
        }

        CommandPermission permission = metadata.permission();
        if (permission.isRequired()) {
            String permissionValue = permission.value();
            pluginCommand.setPermission(permissionValue);
        }

        var aliases = metadata.aliases();
        pluginCommand.setAliases(aliases);
    }

    private CommandMap findCommandMap() {
        try {
            Server server = ownerPlugin.getServer();
            Object commandMapObject = invokeGetCommandMap(server);
            return castToCommandMap(commandMapObject);

        } catch (ReflectiveOperationException exception) {
            String message = "Failed to access Bukkit CommandMap via reflection.";
            throw new IllegalStateException(message, exception);
        }
    }

    private Object invokeGetCommandMap(@NotNull Server server) throws ReflectiveOperationException {
        Class<?> serverClass = server.getClass();
        Method getCommandMapMethod = serverClass.getMethod("getCommandMap");
        return getCommandMapMethod.invoke(server);
    }

    private CommandMap castToCommandMap(@NotNull Object commandMapObject) {
        if (commandMapObject instanceof CommandMap commandMap) {
            return commandMap;
        }
        String message = "Bukkit CommandMap not found on server implementation.";
        throw new IllegalStateException(message);
    }
}
