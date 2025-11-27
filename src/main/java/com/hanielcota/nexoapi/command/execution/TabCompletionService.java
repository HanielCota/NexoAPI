package com.hanielcota.nexoapi.command.execution;

import com.hanielcota.nexoapi.command.CommandSuggestionsProvider;
import com.hanielcota.nexoapi.command.model.CommandArguments;
import com.hanielcota.nexoapi.command.model.CommandInput;
import com.hanielcota.nexoapi.command.CommandContext;
import com.hanielcota.nexoapi.command.model.CommandDefinition;
import com.hanielcota.nexoapi.command.model.RegisteredCommand;
import com.hanielcota.nexoapi.command.sub.SubCommandMap;
import com.hanielcota.nexoapi.command.sub.SubCommandName;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service responsible for providing tab completion suggestions.
 *
 * @since 1.0.0
 */
public final class TabCompletionService {
    private final PermissionResolver permissionResolver;
    private final PermissionChecker permissionChecker;

    private TabCompletionService(
            @NotNull PermissionResolver permissionResolver,
            @NotNull PermissionChecker permissionChecker
    ) {
        this.permissionResolver = Objects.requireNonNull(permissionResolver, "Permission resolver cannot be null.");
        this.permissionChecker = Objects.requireNonNull(permissionChecker, "Permission checker cannot be null.");
    }

    /**
     * Creates a new TabCompletionService instance.
     *
     * @return a new TabCompletionService instance
     */
    public static TabCompletionService create() {
        return new TabCompletionService(
                PermissionResolver.create(),
                PermissionChecker.create()
        );
    }

    /**
     * Provides tab completion suggestions for a command context.
     *
     * @param context          the command context
     * @param definition       the command definition
     * @param registeredCommand the registered command
     * @return a list of suggestions
     * @throws NullPointerException if any parameter is null
     */
    @NotNull
    public List<String> suggest(
            @NotNull CommandContext context,
            @NotNull CommandDefinition definition,
            @NotNull RegisteredCommand registeredCommand
    ) {
        Objects.requireNonNull(context, "Context cannot be null.");
        Objects.requireNonNull(definition, "Definition cannot be null.");
        Objects.requireNonNull(registeredCommand, "Registered command cannot be null.");

        var commandArguments = context.input().arguments();
        var subCommandMap = definition.subCommands();

        // If no subcommands, use custom suggestions from root handler
        if (subCommandMap.isEmpty()) {
            return getCustomSuggestions(context, registeredCommand);
        }

        // Check if first argument is a valid subcommand
        String firstArg = commandArguments.firstOrNull();
        if (firstArg != null) {
            var subCommandName = SubCommandName.from(firstArg);
            var subCommandInvoker = subCommandMap.findBy(subCommandName);
            
            if (subCommandInvoker != null) {
                // First argument is a valid subcommand, build subcommand context and get its suggestions
                var subCommandContext = buildSubCommandContext(context);
                return getCustomSuggestions(subCommandContext, registeredCommand);
            }
        }

        // First argument is not a valid subcommand (or no first argument)
        // Suggest subcommands if we're on the first argument
        if (commandArguments.size() <= 1) {
            return suggestSubCommands(context, definition, commandArguments);
        }

        // More than one argument and first is not a valid subcommand
        // Return empty list (no suggestions)
        return List.of();
    }

    @NotNull
    private CommandContext buildSubCommandContext(@NotNull CommandContext originalContext) {
        var originalInput = originalContext.input();
        var originalLabel = originalInput.label();
        var originalArguments = originalInput.arguments();
        var filteredArguments = originalArguments.withoutFirst();
        var newInput = new CommandInput(originalLabel, filteredArguments);
        var originalSender = originalContext.sender();
        return new CommandContext(originalSender, newInput);
    }

    @NotNull
    private List<String> suggestSubCommands(
            @NotNull CommandContext context,
            @NotNull CommandDefinition definition,
            @NotNull CommandArguments arguments
    ) {
        var sender = context.sender();
        var subCommandMap = definition.subCommands();
        String prefix = extractPrefix(arguments);

        var matchingNames = subCommandMap.namesStartingWith(prefix);
        return filterByPermission(sender, definition, subCommandMap, matchingNames);
    }

    @NotNull
    private String extractPrefix(@NotNull CommandArguments arguments) {
        if (arguments.isEmpty()) {
            return "";
        }
        String firstArg = arguments.argumentAt(0);
        return firstArg != null ? firstArg : "";
    }

    @NotNull
    private List<String> filterByPermission(
            @NotNull CommandSender sender,
            @NotNull CommandDefinition definition,
            @NotNull SubCommandMap subCommandMap,
            @NotNull List<String> names
    ) {
        List<String> result = new ArrayList<>();

        for (String name : names) {
            if (isAllowedForSender(sender, name, definition, subCommandMap)) {
                result.add(name);
            }
        }

        return List.copyOf(result);
    }

    private boolean isAllowedForSender(
            @NotNull CommandSender sender,
            @NotNull String name,
            @NotNull CommandDefinition definition,
            @NotNull SubCommandMap subCommandMap
    ) {
        var subCommandName = SubCommandName.from(name);
        var invoker = subCommandMap.findBy(subCommandName);
        if (invoker == null) {
            return false;
        }

        var subMetadata = invoker.metadata();
        var rootMetadata = definition.metadata();
        var effectivePermission = permissionResolver.resolve(subMetadata, rootMetadata);

        return permissionChecker.isAllowed(sender, effectivePermission);
    }

    @NotNull
    private List<String> getCustomSuggestions(
            @NotNull CommandContext context,
            @NotNull RegisteredCommand registeredCommand
    ) {
        var handler = registeredCommand.handler();
        if (!(handler instanceof CommandSuggestionsProvider suggestionsProvider)) {
            return List.of();
        }

        List<String> suggestions = suggestionsProvider.suggest(context);
        if (suggestions == null) {
            return List.of();
        }

        return suggestions;
    }
}

