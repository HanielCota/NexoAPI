package com.hanielcota.nexoapi.command.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a method as a subcommand handler.
 * The method must receive exactly one parameter of type {@link com.hanielcota.nexoapi.command.CommandContext}
 * and return void.
 *
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface NexoSubCommand {
    /**
     * The subcommand name (e.g., "set", "delete", "list").
     */
    String value();

    /**
     * The subcommand description.
     */
    String description() default "";

    /**
     * Permission specific to this subcommand.
     * If empty, inherits the permission from the root command.
     */
    String permission() default "";
}

