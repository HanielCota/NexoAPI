package com.hanielcota.nexoapi.command.execution;

import com.hanielcota.nexoapi.command.model.CommandMetadata;
import com.hanielcota.nexoapi.command.model.CommandPermission;
import com.hanielcota.nexoapi.command.sub.SubCommandMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Service responsible for resolving effective permissions for subcommands.
 *
 * @since 1.0.0
 */
public final class PermissionResolver {
    private PermissionResolver() {
        throw new UnsupportedOperationException("Utility class.");
    }

    /**
     * Creates a new PermissionResolver instance.
     *
     * @return a new PermissionResolver instance
     */
    public static PermissionResolver create() {
        return new PermissionResolver();
    }

    /**
     * Resolves the effective permission for a subcommand.
     * If the subcommand has its own permission, it's used.
     * Otherwise, the root command's permission is used.
     *
     * @param subCommandMetadata the subcommand metadata
     * @param rootMetadata        the root command metadata
     * @return the effective permission
     * @throws NullPointerException if any parameter is null
     */
    @NotNull
    public CommandPermission resolve(
            @NotNull SubCommandMetadata subCommandMetadata,
            @NotNull CommandMetadata rootMetadata
    ) {
        Objects.requireNonNull(subCommandMetadata, "SubCommand metadata cannot be null.");
        Objects.requireNonNull(rootMetadata, "Root metadata cannot be null.");

        var subPermission = subCommandMetadata.permission();
        if (subPermission.isRequired()) {
            return subPermission;
        }

        return rootMetadata.permission();
    }
}

