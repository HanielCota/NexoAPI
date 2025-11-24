package com.hanielcota.nexoapi.command;

import java.util.List;

/**
 * Interface for providing tab completion suggestions for commands.
 * Implement this interface in your {@link CommandHandler} to provide custom suggestions.
 *
 * @since 1.0.0
 */
public interface CommandSuggestionsProvider {
    /**
     * Provides tab completion suggestions for the given command context.
     *
     * @param context the command context
     * @return a list of suggestion strings, or an empty list if no suggestions
     */
    List<String> suggest(CommandContext context);
}

