package com.hanielcota.nexoapi.command;

import com.hanielcota.nexoapi.command.execution.*;
import com.hanielcota.nexoapi.command.model.*;
import com.hanielcota.nexoapi.command.sub.SubCommandInvoker;
import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
    private final SubCommandResolver subCommandResolver;
    private final PermissionChecker permissionChecker;
    private final CooldownChecker cooldownChecker;
    private final ExecutionScheduler executionScheduler;
    private final ExceptionHandler exceptionHandler;
    private final TabCompletionService tabCompletionService;

    PaperCommandExecutor(@NotNull RegisteredCommand registeredCommand, @NotNull Plugin ownerPlugin) {
        this.registeredCommand = Objects.requireNonNull(registeredCommand, "Registered command cannot be null.");
        this.ownerPlugin = Objects.requireNonNull(ownerPlugin, "Plugin cannot be null.");
        this.subCommandResolver = SubCommandResolver.create();
        this.permissionChecker = PermissionChecker.create();
        this.cooldownChecker = CooldownChecker.create();
        this.executionScheduler = ExecutionScheduler.create(ownerPlugin);
        this.exceptionHandler = ExceptionHandler.create(registeredCommand, ownerPlugin);
        this.tabCompletionService = TabCompletionService.create();
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

        var subCommandMap = commandDefinition.subCommands();
        
        // If there are no subcommands defined, always execute the root handler
        if (subCommandMap.isEmpty()) {
            executeRoot(commandContext, commandDefinition);
            return true;
        }

        String subCommandToken = commandInput.arguments().firstOrNull();
        if (subCommandToken == null) {
            executeRoot(commandContext, commandDefinition);
            return true;
        }

        SubCommandInvoker subCommandInvoker = subCommandResolver.resolve(subCommandMap, subCommandToken);
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

        return tabCompletionService.suggest(commandContext, commandDefinition, registeredCommand);
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
        var cooldown = metadata.cooldown();
        var commandName = metadata.name();
        var sender = context.sender();

        if (!permissionChecker.isAllowed(sender, permission)) {
            permissionChecker.sendDeniedMessage(sender, permission);
            return;
        }

        if (!cooldownChecker.isAllowed(sender, commandName, cooldown)) {
            cooldownChecker.sendCooldownMessage(sender, commandName, cooldown);
            return;
        }

        var handler = registeredCommand.handler();
        Runnable task = createRootExecutionTask(handler, context, commandName, cooldown);
        var executionType = metadata.executionType();
        executionScheduler.schedule(task, executionType);
    }

    private void executeSub(
            @NotNull CommandContext context,
            @NotNull SubCommandInvoker invoker,
            @NotNull CommandDefinition definition
    ) {
        var permissionResolver = PermissionResolver.create();
        var subCommandMetadata = invoker.metadata();
        var rootMetadata = definition.metadata();
        var effectivePermission = permissionResolver.resolve(subCommandMetadata, rootMetadata);
        var cooldown = rootMetadata.cooldown();
        var commandName = rootMetadata.name();
        var sender = context.sender();

        if (!permissionChecker.isAllowed(sender, effectivePermission)) {
            permissionChecker.sendDeniedMessage(sender, effectivePermission);
            return;
        }

        if (!cooldownChecker.isAllowed(sender, commandName, cooldown)) {
            cooldownChecker.sendCooldownMessage(sender, commandName, cooldown);
            return;
        }

        var handler = registeredCommand.handler();
        Runnable task = createSubCommandExecutionTask(invoker, handler, context, commandName, cooldown);
        var executionType = rootMetadata.executionType();
        executionScheduler.schedule(task, executionType);
    }

    @NotNull
    private Runnable createRootExecutionTask(
            @NotNull CommandHandler handler,
            @NotNull CommandContext context,
            @NotNull CommandName commandName,
            @NotNull CommandCooldown cooldown
    ) {
        return () -> {
            try {
                handler.handle(context);
                cooldownChecker.recordExecution(context.sender(), commandName);
            } catch (Exception exception) {
                exceptionHandler.handle(context.sender(), exception);
            }
        };
    }

    @NotNull
    private Runnable createSubCommandExecutionTask(
            @NotNull SubCommandInvoker invoker,
            @NotNull CommandHandler handler,
            @NotNull CommandContext context,
            @NotNull CommandName commandName,
            @NotNull CommandCooldown cooldown
    ) {
        return () -> {
            try {
                invoker.invoke(handler, context);
                cooldownChecker.recordExecution(context.sender(), commandName);
            } catch (Exception exception) {
                exceptionHandler.handle(context.sender(), exception);
            }
        };
    }

    private static final MiniMessageText UNKNOWN_SUBCOMMAND_MESSAGE = 
            MiniMessageText.of("<red>Subcomando desconhecido: <gray>{subcommand}");

    private void sendUnknownSubCommandMessage(@NotNull CommandSender sender, @NotNull String subCommandToken) {
        Component message = UNKNOWN_SUBCOMMAND_MESSAGE.toComponent()
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("{subcommand}")
                        .replacement(subCommandToken)
                        .build());
        sender.sendMessage(message);
    }

}
