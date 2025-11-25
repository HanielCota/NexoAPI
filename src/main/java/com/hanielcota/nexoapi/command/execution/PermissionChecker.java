package com.hanielcota.nexoapi.command.execution;

import com.hanielcota.nexoapi.command.model.CommandPermission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Service responsible for checking command permissions.
 *
 * @since 1.0.0
 */
public final class PermissionChecker {
    private PermissionChecker() {
    }

    /**
     * Creates a new PermissionChecker instance.
     *
     * @return a new PermissionChecker instance
     */
    public static PermissionChecker create() {
        return new PermissionChecker();
    }

    /**
     * Checks if a sender is allowed to use a command with the given permission.
     *
     * @param sender     the command sender
     * @param permission the command permission
     * @return true if allowed, false otherwise
     * @throws NullPointerException if any parameter is null
     */
    public boolean isAllowed(@NotNull CommandSender sender, @NotNull CommandPermission permission) {
        Objects.requireNonNull(sender, "Sender cannot be null.");
        Objects.requireNonNull(permission, "Permission cannot be null.");

        if (!permission.isRequired()) {
            return true;
        }

        String permissionValue = permission.value();
        return sender.hasPermission(permissionValue);
    }

    /**
     * Sends a permission denied message to the sender.
     *
     * @param sender     the command sender
     * @param permission the command permission
     * @throws NullPointerException if any parameter is null
     */
    public void sendDeniedMessage(@NotNull CommandSender sender, @NotNull CommandPermission permission) {
        Objects.requireNonNull(sender, "Sender cannot be null.");
        Objects.requireNonNull(permission, "Permission cannot be null.");

        String permissionValue = permission.value();
        String message = "§cVocê não tem permissão para usar este comando. §7(" + permissionValue + ")";
        sender.sendMessage(message);
    }
}

