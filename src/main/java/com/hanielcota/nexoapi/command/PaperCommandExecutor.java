package com.hanielcota.nexoapi.command;

import com.hanielcota.nexoapi.command.model.*;
import com.hanielcota.nexoapi.command.sub.SubCommandInvoker;
import com.hanielcota.nexoapi.command.sub.SubCommandMap;
import com.hanielcota.nexoapi.command.sub.SubCommandMetadata;
import com.hanielcota.nexoapi.command.sub.SubCommandName;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Executor and tab completer for Paper commands.
 * Handles command execution, permission checking, and tab completion.
 * <p>
 * This class is intentionally not a record because it contains complex business logic,
 * multiple private helper methods, and implements interfaces that require method implementations.
 * Records are better suited for simple value objects, not classes with substantial behavior.
 *
 * @since 1.0.0
 */
@SuppressWarnings("ClassCanBeRecord")
public final class PaperCommandExecutor implements CommandExecutor, TabCompleter {
    private final RegisteredCommand registeredCommand;
    private final Plugin ownerPlugin;

    PaperCommandExecutor(@NotNull RegisteredCommand registeredCommand, @NotNull Plugin ownerPlugin) {
        this.registeredCommand = registeredCommand;
        this.ownerPlugin = ownerPlugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        Objects.requireNonNull(sender, "Sender cannot be null.");
        Objects.requireNonNull(label, "Label cannot be null.");
        Objects.requireNonNull(args, "Arguments cannot be null.");

        var commandInput = CommandInput.from(label, args);
        var commandContext = new CommandContext(sender, commandInput);
        var commandDefinition = registeredCommand.definition();

        String subCommandToken = firstArgumentOrNull(commandInput.arguments());
        if (subCommandToken == null) {
            executeRoot(commandContext, commandDefinition);
            return true;
        }

        SubCommandInvoker subCommandInvoker = findSubCommand(commandDefinition.subCommands(), subCommandToken);
        if (subCommandInvoker == null) {
            sendUnknownSubCommandMessage(commandContext.sender(), subCommandToken);
            return true;
        }

        var subCommandContext = buildSubCommandContext(commandContext);
        executeSub(subCommandContext, subCommandInvoker, commandDefinition);
        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        var commandInput = CommandInput.from(label, args);
        var commandContext = new CommandContext(sender, commandInput);
        var commandDefinition = registeredCommand.definition();
        var commandArguments = commandInput.arguments();

        int argumentCount = commandArguments.size();
        if (argumentCount == 0 || argumentCount == 1) {
            return suggestSubCommandNames(sender, commandDefinition, commandArguments);
        }

        return getCustomSuggestions(commandContext);
    }

    private List<String> getCustomSuggestions(@NotNull CommandContext commandContext) {
        var handler = registeredCommand.handler();
        if (!(handler instanceof CommandSuggestionsProvider suggestionsProvider)) {
            return List.of();
        }

        List<String> suggestions = suggestionsProvider.suggest(commandContext);
        if (suggestions == null) {
            return List.of();
        }

        return suggestions;
    }

    private String firstArgumentOrNull(@NotNull CommandArguments commandArguments) {
        if (commandArguments.isEmpty()) {
            return null;
        }
        return commandArguments.argumentAt(0);
    }

    private SubCommandInvoker findSubCommand(
            @NotNull SubCommandMap subCommandMap,
            @NotNull String subCommandToken
    ) {
        if (subCommandMap.isEmpty()) {
            return null;
        }

        var subCommandName = SubCommandName.from(subCommandToken);
        return subCommandMap.findBy(subCommandName);
    }

    private CommandContext buildSubCommandContext(@NotNull CommandContext originalContext) {
        var originalInput = originalContext.input();
        var originalLabel = originalInput.label();
        var originalArguments = originalInput.arguments();
        var filteredArguments = originalArguments.withoutFirst();
        var newInput = new CommandInput(originalLabel, filteredArguments);
        var originalSender = originalContext.sender();
        return new CommandContext(originalSender, newInput);
    }

    private void executeRoot(
            @NotNull CommandContext context,
            @NotNull CommandDefinition definition
    ) {
        var metadata = definition.metadata();
        var permission = metadata.permission();
        var sender = context.sender();

        if (!isSenderAllowedForPermission(sender, permission)) {
            sendNoPermissionMessage(sender, permission);
            return;
        }

        var handler = registeredCommand.handler();
        Runnable task = () -> executeHandlerSafely(handler, context);
        var executionType = metadata.executionType();
        runAccordingToExecutionType(executionType, task);
    }

    private void executeSub(
            @NotNull CommandContext context,
            @NotNull SubCommandInvoker invoker,
            @NotNull CommandDefinition definition
    ) {
        var subCommandMetadata = invoker.metadata();
        var rootMetadata = definition.metadata();
        var effectivePermission = resolveSubCommandPermission(subCommandMetadata, rootMetadata);
        var sender = context.sender();

        if (!isSenderAllowedForPermission(sender, effectivePermission)) {
            sendNoPermissionMessage(sender, effectivePermission);
            return;
        }

        var handler = registeredCommand.handler();
        Runnable task = () -> invokeSubCommandSafely(invoker, handler, context);
        var executionType = rootMetadata.executionType();
        runAccordingToExecutionType(executionType, task);
    }

    private void executeHandlerSafely(
            @NotNull CommandHandler handler,
            @NotNull CommandContext context
    ) {
        try {
            handler.handle(context);
        } catch (Exception exception) {
            handleCommandException(context.sender(), exception);
        }
    }

    private void invokeSubCommandSafely(
            @NotNull SubCommandInvoker invoker,
            @NotNull CommandHandler handler,
            @NotNull CommandContext context
    ) {
        try {
            invoker.invoke(handler, context);
        } catch (Exception exception) {
            handleCommandException(context.sender(), exception);
        }
    }

    private void handleCommandException(@NotNull CommandSender sender, @NotNull Exception exception) {
        String message = "§cErro ao executar comando: §7" + exception.getMessage();
        sender.sendMessage(message);
        exception.printStackTrace();
    }

    private CommandPermission resolveSubCommandPermission(
            @NotNull SubCommandMetadata subCommandMetadata,
            @NotNull CommandMetadata rootMetadata
    ) {
        var subPermission = subCommandMetadata.permission();
        if (subPermission.isRequired()) {
            return subPermission;
        }
        return rootMetadata.permission();
    }

    private boolean isSenderAllowedForPermission(
            @NotNull CommandSender sender,
            @NotNull CommandPermission permission
    ) {
        if (!permission.isRequired()) {
            return true;
        }
        String permissionValue = permission.value();
        return sender.hasPermission(permissionValue);
    }

    private void sendNoPermissionMessage(
            @NotNull CommandSender sender,
            @NotNull CommandPermission permission
    ) {
        String permissionValue = permission.value();
        String message = "§cVocê não tem permissão para usar este comando. §7(" + permissionValue + ")";
        sender.sendMessage(message);
    }

    private void sendUnknownSubCommandMessage(@NotNull CommandSender sender, @NotNull String subCommandToken) {
        String message = "§cSubcomando desconhecido: §7" + subCommandToken;
        sender.sendMessage(message);
    }

    private void runAccordingToExecutionType(
            @NotNull CommandExecutionType executionType,
            @NotNull Runnable task
    ) {
        if (executionType.isAsync()) {
            executeAsync(task);
            return;
        }
        executeSync(task);
    }

    private void executeSync(@NotNull Runnable task) {
        task.run();
    }

    private void executeAsync(@NotNull Runnable task) {
        if (!ownerPlugin.isEnabled()) {
            return;
        }
        Server server = ownerPlugin.getServer();
        BukkitScheduler scheduler = server.getScheduler();
        scheduler.runTaskAsynchronously(ownerPlugin, task);
    }

    private List<String> suggestSubCommandNames(
            @NotNull CommandSender sender,
            @NotNull CommandDefinition definition,
            @NotNull CommandArguments arguments
    ) {
        var subCommandMap = definition.subCommands();
        if (subCommandMap.isEmpty()) {
            return List.of();
        }

        String prefix = null;
        if (!arguments.isEmpty()) {
            prefix = arguments.argumentAt(0);
        }

        List<String> allNames = subCommandMap.namesAsStrings();
        List<String> result = new ArrayList<>();

        for (String name : allNames) {
            if (shouldIncludeInSuggestions(name, prefix, sender, definition, subCommandMap)) {
                result.add(name);
            }
        }

        return List.copyOf(result);
    }

    private boolean shouldIncludeInSuggestions(
            @NotNull String name,
            @Nullable String prefix,
            @NotNull CommandSender sender,
            @NotNull CommandDefinition definition,
            @NotNull SubCommandMap subCommandMap
    ) {
        if (!matchesPrefix(prefix, name)) {
            return false;
        }

        var subCommandName = SubCommandName.from(name);
        SubCommandInvoker invoker = subCommandMap.findBy(subCommandName);
        if (invoker == null) {
            return false;
        }

        var subMetadata = invoker.metadata();
        var rootMetadata = definition.metadata();
        var effectivePermission = resolveSubCommandPermission(subMetadata, rootMetadata);
        return isSenderAllowedForPermission(sender, effectivePermission);
    }

    private boolean matchesPrefix(@Nullable String prefix, @NotNull String name) {
        if (prefix == null || prefix.isBlank()) {
            return true;
        }
        String lowerCasePrefix = prefix.toLowerCase();
        String lowerCaseName = name.toLowerCase();
        return lowerCaseName.startsWith(lowerCasePrefix);
    }
}
