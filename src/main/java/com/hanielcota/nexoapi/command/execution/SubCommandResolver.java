package com.hanielcota.nexoapi.command.execution;

import com.hanielcota.nexoapi.command.sub.SubCommandInvoker;
import com.hanielcota.nexoapi.command.sub.SubCommandMap;
import com.hanielcota.nexoapi.command.sub.SubCommandName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Service responsible for resolving subcommands from tokens.
 *
 * @since 1.0.0
 */
public final class SubCommandResolver {
    private SubCommandResolver() {
    }

    /**
     * Creates a new SubCommandResolver instance.
     *
     * @return a new SubCommandResolver instance
     */
    public static SubCommandResolver create() {
        return new SubCommandResolver();
    }

    /**
     * Resolves a subcommand from a token string.
     *
     * @param subCommandMap the subcommand map
     * @param token         the subcommand token
     * @return the subcommand invoker, or null if not found
     * @throws NullPointerException if subCommandMap is null
     */
    @Nullable
    public SubCommandInvoker resolve(@NotNull SubCommandMap subCommandMap, @NotNull String token) {
        Objects.requireNonNull(subCommandMap, "SubCommandMap cannot be null.");
        Objects.requireNonNull(token, "Token cannot be null.");

        if (subCommandMap.isEmpty()) {
            return null;
        }

        var subCommandName = SubCommandName.from(token);
        return subCommandMap.findBy(subCommandName);
    }
}

