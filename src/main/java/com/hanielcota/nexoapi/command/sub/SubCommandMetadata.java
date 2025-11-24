package com.hanielcota.nexoapi.command.sub;

import com.hanielcota.nexoapi.command.model.CommandDescription;
import com.hanielcota.nexoapi.command.model.CommandPermission;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents the metadata of a subcommand.
 * Contains all information about a subcommand definition.
 *
 * @param name        the subcommand name
 * @param description the subcommand description
 * @param permission  the subcommand permission
 * @since 1.0.0
 */
public record SubCommandMetadata(
        @NotNull SubCommandName name,
        @NotNull CommandDescription description,
        @NotNull CommandPermission permission
) {
    public SubCommandMetadata {
        Objects.requireNonNull(name, "Subcommand name cannot be null.");
        Objects.requireNonNull(description, "Subcommand description cannot be null.");
        Objects.requireNonNull(permission, "Subcommand permission cannot be null.");
    }
}

