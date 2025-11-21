package com.hanielcota.nexoapi.actionbar;

import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an action bar message that can be sent to players.
 * Action bars appear above the hotbar and are useful for displaying temporary information.
 * <p>
 * The text supports MiniMessage format for colors and formatting.
 * </p>
 *
 * @param content The wrapped text content of the action bar.
 * @since 1.0.0
 */
public record NexoActionBar(MiniMessageText content) {

    /**
     * Creates a new NexoActionBar from a text string.
     * The text supports MiniMessage format (e.g., "<red>Hello" or "<#FF0000>Hello").
     *
     * @param text the action bar text, which may be null (will be treated as empty)
     * @return a new NexoActionBar instance
     */
    public static NexoActionBar of(@Nullable String text) {
        return new NexoActionBar(MiniMessageText.of(text));
    }

    /**
     * Sends this action bar to the specified audience.
     * If the audience is null, this method does nothing.
     *
     * @param audience the audience to send the action bar to (e.g., a Player)
     */
    public void sendTo(@Nullable Audience audience) {
        if (audience == null) {
            return;
        }

        audience.sendActionBar(content.toComponent());
    }
}