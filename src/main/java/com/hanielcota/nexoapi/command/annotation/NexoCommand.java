package com.hanielcota.nexoapi.command.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a class as a root command handler.
 * The class must implement {@link com.hanielcota.nexoapi.command.CommandHandler}.
 *
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface NexoCommand {
    /**
     * The command name (e.g., "home", "warps").
     */
    String name();

    /**
     * The command description.
     */
    String description() default "";

    /**
     * The permission required to use this command.
     * Empty string means no permission required.
     */
    String permission() default "";

    /**
     * Command aliases (alternative names).
     */
    String[] aliases() default {};

    /**
     * If true, the command is executed asynchronously.
     * Default is false (synchronous execution).
     */
    boolean async() default false;
}

