package com.hanielcota.nexoapi.command.execution;

import com.hanielcota.nexoapi.command.model.CommandCooldown;
import com.hanielcota.nexoapi.command.model.CommandName;
import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Service responsible for checking command cooldowns.
 *
 * @since 1.0.0
 */
public final class CooldownChecker {
    private final CooldownService cooldownService;

    private CooldownChecker(@NotNull CooldownService cooldownService) {
        this.cooldownService = Objects.requireNonNull(cooldownService, "CooldownService cannot be null.");
    }

    /**
     * Creates a new CooldownChecker instance using the shared CooldownService.
     *
     * @return a new CooldownChecker instance
     */
    @NotNull
    public static CooldownChecker create() {
        return new CooldownChecker(CooldownService.getInstance());
    }

    /**
     * Creates a new CooldownChecker instance with a custom CooldownService.
     *
     * @param cooldownService the cooldown service to use
     * @return a new CooldownChecker instance
     */
    @NotNull
    public static CooldownChecker create(@NotNull CooldownService cooldownService) {
        return new CooldownChecker(cooldownService);
    }

    /**
     * Checks if a sender is allowed to use a command (not in cooldown).
     * Console senders are always allowed (cooldown only applies to players).
     *
     * @param sender    the command sender
     * @param commandName the command name
     * @param cooldown  the command cooldown configuration
     * @return true if allowed, false if in cooldown
     * @throws NullPointerException if any parameter is null
     */
    public boolean isAllowed(
            @NotNull CommandSender sender,
            @NotNull CommandName commandName,
            @NotNull CommandCooldown cooldown
    ) {
        Objects.requireNonNull(sender, "Sender cannot be null.");
        Objects.requireNonNull(commandName, "Command name cannot be null.");
        Objects.requireNonNull(cooldown, "Command cooldown cannot be null.");

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!cooldown.isActive()) {
            return true;
        }

        String commandNameValue = commandName.value();
        return !cooldownService.isInCooldown(player, commandNameValue, cooldown.seconds());
    }

    /**
     * Sends a cooldown message to the sender.
     *
     * @param sender    the command sender
     * @param commandName the command name
     * @param cooldown  the command cooldown configuration
     * @throws NullPointerException if any parameter is null
     */
    public void sendCooldownMessage(
            @NotNull CommandSender sender,
            @NotNull CommandName commandName,
            @NotNull CommandCooldown cooldown
    ) {
        Objects.requireNonNull(sender, "Sender cannot be null.");
        Objects.requireNonNull(commandName, "Command name cannot be null.");
        Objects.requireNonNull(cooldown, "Command cooldown cannot be null.");

        if (!(sender instanceof Player player)) {
            return;
        }

        String commandNameValue = commandName.value();
        long remainingMillis = cooldownService.getRemainingCooldown(player, commandNameValue, cooldown.seconds());
        long remainingSeconds = (remainingMillis + 999) / 1000;

        Component message = COOLDOWN_MESSAGE.toComponent()
                .replaceText(builder -> builder
                        .matchLiteral("{seconds}")
                        .replacement(String.valueOf(remainingSeconds))
                        .build());

        sender.sendMessage(message);
    }

    /**
     * Records that a command was executed by a sender.
     * Only records for players, console executions are ignored.
     *
     * @param sender    the command sender
     * @param commandName the command name
     * @throws NullPointerException if any parameter is null
     */
    public void recordExecution(@NotNull CommandSender sender, @NotNull CommandName commandName) {
        Objects.requireNonNull(sender, "Sender cannot be null.");
        Objects.requireNonNull(commandName, "Command name cannot be null.");

        if (!(sender instanceof Player player)) {
            return;
        }

        String commandNameValue = commandName.value();
        cooldownService.recordExecution(player, commandNameValue);
    }

    private static final MiniMessageText COOLDOWN_MESSAGE =
            MiniMessageText.of("<red>Você está em cooldown! Aguarde <yellow>{seconds}<red> segundo(s) antes de usar este comando novamente.");
}

