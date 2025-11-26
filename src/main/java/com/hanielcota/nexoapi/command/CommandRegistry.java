package com.hanielcota.nexoapi.command;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.hanielcota.nexoapi.command.cache.CommandDefinitionCache;
import com.hanielcota.nexoapi.command.model.*;
import com.hanielcota.nexoapi.command.registry.CommandLabelMap;
import com.hanielcota.nexoapi.command.registry.CommandLabelSet;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Registry for dynamic command registration with intelligent caching.
 * Registers commands directly to Bukkit's CommandMap without requiring plugin.yml entries.
 * <p>
 * Uses Caffeine Cache to avoid re-parsing command annotations,
 * improving registration performance especially for plugins that
 * dynamically register/re-register commands.
 * </p>
 *
 * @since 1.0.0
 */
public final class CommandRegistry {
    private final Plugin ownerPlugin;
    private final CommandLabelMap commandsByLabel;
    private final CommandDefinitionCache definitionCache;

    private CommandRegistry(
        @NotNull Plugin ownerPlugin, 
        @NotNull CommandLabelMap commandsByLabel,
        @NotNull CommandDefinitionCache definitionCache
    ) {
        this.ownerPlugin = Objects.requireNonNull(ownerPlugin, "Plugin cannot be null.");
        this.commandsByLabel = Objects.requireNonNull(commandsByLabel, "Command label map cannot be null.");
        this.definitionCache = Objects.requireNonNull(definitionCache, "Definition cache cannot be null.");
    }

    /**
     * Creates a new CommandRegistry for the given plugin.
     *
     * @param ownerPlugin the plugin that owns this registry
     * @return a new CommandRegistry instance
     */
    public static CommandRegistry create(@NotNull Plugin ownerPlugin) {
        Objects.requireNonNull(ownerPlugin, "Plugin cannot be null.");
        return new CommandRegistry(
            ownerPlugin, 
            CommandLabelMap.empty(),
            CommandDefinitionCache.create()
        );
    }

    /**
     * Registers a command handler.
     * The handler class must be annotated with @NexoCommand.
     * <p>
     * This method uses caching to avoid re-parsing annotations,
     * making subsequent registrations of the same command class very fast.
     * </p>
     *
     * @param handler the command handler instance
     * @throws IllegalArgumentException if the handler is not annotated with @NexoCommand
     * @throws IllegalStateException    if registration fails
     */
    public void register(@NotNull CommandHandler handler) {
        Objects.requireNonNull(handler, "Command handler cannot be null.");

        Class<?> handlerClass = handler.getClass();
        
        // Use cache to get command definition (much faster on subsequent calls)
        var commandDefinition = definitionCache.getDefinition(handlerClass);
        
        validateCommandNotRegistered(commandDefinition);
        var registeredCommand = new RegisteredCommand(commandDefinition, handler);

        try {
            registerOnPlatform(registeredCommand);
            storeCommandLabels(commandDefinition, registeredCommand);
        } catch (Exception exception) {
            var metadata = commandDefinition.metadata();
            var commandName = metadata.name();
            String message = "Failed to register command: " + commandName.value();
            throw new IllegalStateException(message, exception);
        }
    }
    
    /**
     * Gets cache performance statistics for command definitions.
     * <p>
     * Key metrics:
     * - Hit rate: should be 100% for commands registered multiple times
     * - Miss rate: only on first registration of each command class
     * - Load time: time spent parsing annotations
     * </p>
     *
     * @return cache statistics
     */
    public CacheStats getDefinitionCacheStats() {
        return definitionCache.getStats();
    }
    
    /**
     * Gets the number of cached command definitions.
     *
     * @return number of cached definitions
     */
    public long getCachedDefinitionCount() {
        return definitionCache.size();
    }
    
    /**
     * Clears the command definition cache.
     * Use this if you need to force re-parsing of all command annotations.
     */
    public void clearDefinitionCache() {
        definitionCache.clear();
    }

    private void validateCommandNotRegistered(@NotNull CommandDefinition commandDefinition) {
        var metadata = commandDefinition.metadata();
        var labels = CommandLabelSet.from(metadata.name(), metadata.aliases());

        if (!commandsByLabel.hasConflict(labels)) {
            return;
        }

        var conflictingLabel = labels.commandName();
        String message = "Command or alias already registered: " + conflictingLabel.value();
        throw new IllegalStateException(message);
    }

    private void storeCommandLabels(
            @NotNull CommandDefinition commandDefinition,
            @NotNull RegisteredCommand registeredCommand
    ) {
        var metadata = commandDefinition.metadata();
        var labels = CommandLabelSet.from(metadata.name(), metadata.aliases());
        commandsByLabel.storeAll(labels, registeredCommand);
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
        configureDescription(pluginCommand, metadata.description());
        configurePermission(pluginCommand, metadata.permission());
        configureAliases(pluginCommand, metadata.aliases());
    }

    private void configureDescription(@NotNull PluginCommand pluginCommand, @NotNull CommandDescription description) {
        String descriptionValue = description.value();
        if (descriptionValue.isEmpty()) {
            return;
        }
        pluginCommand.setDescription(descriptionValue);
    }

    private void configurePermission(@NotNull PluginCommand pluginCommand, @NotNull CommandPermission permission) {
        if (!permission.isRequired()) {
            return;
        }
        String permissionValue = permission.value();
        pluginCommand.setPermission(permissionValue);
    }

    private void configureAliases(@NotNull PluginCommand pluginCommand, @NotNull CommandAliases aliases) {
        if (aliases.isEmpty()) {
            return;
        }
        pluginCommand.setAliases(aliases.all());
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
