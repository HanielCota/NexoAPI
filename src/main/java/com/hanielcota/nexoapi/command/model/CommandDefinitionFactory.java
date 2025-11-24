package com.hanielcota.nexoapi.command.model;

import com.hanielcota.nexoapi.command.CommandContext;
import com.hanielcota.nexoapi.command.annotation.NexoCommand;
import com.hanielcota.nexoapi.command.annotation.NexoSubCommand;
import com.hanielcota.nexoapi.command.sub.SubCommandInvoker;
import com.hanielcota.nexoapi.command.sub.SubCommandMap;
import com.hanielcota.nexoapi.command.sub.SubCommandMetadata;
import com.hanielcota.nexoapi.command.sub.SubCommandName;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Factory for creating CommandDefinition from annotations and handler classes.
 *
 * @since 1.0.0
 */
public final class CommandDefinitionFactory {
    private CommandDefinitionFactory() {
        throw new UnsupportedOperationException("Utility class.");
    }

    /**
     * Creates a CommandDefinition from a @NexoCommand annotation and handler class.
     *
     * @param annotation   the @NexoCommand annotation
     * @param handlerClass the handler class
     * @return a new CommandDefinition instance
     */
    public static CommandDefinition from(@NotNull NexoCommand annotation, @NotNull Class<?> handlerClass) {
        Objects.requireNonNull(annotation, "@NexoCommand cannot be null.");
        Objects.requireNonNull(handlerClass, "Handler class cannot be null.");

        var commandMetadata = CommandMetadataFactory.from(annotation);
        var subCommandMap = scanSubCommands(handlerClass);

        return new CommandDefinition(commandMetadata, subCommandMap);
    }

    private static SubCommandMap scanSubCommands(@NotNull Class<?> handlerClass) {
        Method[] declaredMethods = handlerClass.getDeclaredMethods();
        if (declaredMethods.length == 0) {
            return SubCommandMap.empty();
        }

        Map<SubCommandName, SubCommandInvoker> map = new HashMap<>();
        for (Method method : declaredMethods) {
            addSubCommandIfAnnotated(map, method);
        }

        return new SubCommandMap(map);
    }

    private static void addSubCommandIfAnnotated(
            @NotNull Map<SubCommandName, SubCommandInvoker> map,
            @NotNull Method method
    ) {
        NexoSubCommand annotation = method.getAnnotation(NexoSubCommand.class);
        if (annotation == null) {
            return;
        }

        validateSubCommandMethod(method);

        var subCommandName = SubCommandName.from(annotation.value());
        var subCommandDescription = CommandDescription.from(annotation.description());
        var subCommandPermission = CommandPermission.from(annotation.permission());

        var metadata = new SubCommandMetadata(
                subCommandName,
                subCommandDescription,
                subCommandPermission
        );

        var invoker = new SubCommandInvoker(metadata, method);

        if (map.containsKey(subCommandName)) {
            String message = "Duplicated subcommand name: " + subCommandName.value();
            throw new IllegalStateException(message);
        }

        map.put(subCommandName, invoker);
    }

    private static void validateSubCommandMethod(@NotNull Method method) {
        validateParameterCount(method);
        validateParameterType(method);
        validateReturnType(method);
    }

    private static void validateParameterCount(@NotNull Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            String message = "Subcommand method must receive exactly one parameter of type CommandContext. " +
                    "Method: " + method.getName();
            throw new IllegalStateException(message);
        }
    }

    private static void validateParameterType(@NotNull Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> parameterType = parameterTypes[0];
        if (!parameterType.equals(CommandContext.class)) {
            String message = "Subcommand method parameter must be CommandContext. " +
                    "Method: " + method.getName();
            throw new IllegalStateException(message);
        }
    }

    private static void validateReturnType(@NotNull Method method) {
        Class<?> returnType = method.getReturnType();
        if (!void.class.equals(returnType)) {
            String message = "Subcommand method must return void. " +
                    "Method: " + method.getName();
            throw new IllegalStateException(message);
        }
    }
}
