package com.hanielcota.nexoapi.command.sub;

import com.hanielcota.nexoapi.command.CommandContext;
import com.hanielcota.nexoapi.command.CommandHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an invoker for a subcommand method.
 * Encapsulates the method and metadata needed to invoke a subcommand.
 *
 * @param metadata the subcommand metadata
 * @param method   the method to invoke
 * @since 1.0.0
 */
public record SubCommandInvoker(
        @NotNull SubCommandMetadata metadata,
        @NotNull Method method
) {
    private static final Set<Method> ACCESSIBLE_METHODS = ConcurrentHashMap.newKeySet();

    public SubCommandInvoker {
        Objects.requireNonNull(metadata, "Subcommand metadata cannot be null.");
        Objects.requireNonNull(method, "Subcommand method cannot be null.");
    }

    private void ensureMethodAccessible() {
        if (ACCESSIBLE_METHODS.contains(method)) {
            return;
        }
        method.setAccessible(true);
        ACCESSIBLE_METHODS.add(method);
    }

    /**
     * Invokes the subcommand method with the given context.
     *
     * @param handler the command handler instance
     * @param context the command context
     * @throws IllegalStateException if the method cannot be accessed or invoked
     */
    public void invoke(@NotNull CommandHandler handler, @NotNull CommandContext context) {
        Objects.requireNonNull(handler, "Handler cannot be null.");
        Objects.requireNonNull(context, "Context cannot be null.");

        ensureMethodAccessible();

        try {
            method.invoke(handler, context);
        } catch (IllegalAccessException exception) {
            String message = "Failed to access subcommand method: " + metadata.name().value();
            throw new IllegalStateException(message, exception);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause == null) {
                cause = exception;
            }
            String message = "Failed to invoke subcommand method: " + metadata.name().value();
            throw new IllegalStateException(message, cause);
        }
    }
}
