package com.hanielcota.nexoapi.title;

import com.hanielcota.nexoapi.text.MiniMessageText;
import com.hanielcota.nexoapi.title.timing.TitleTiming;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a title that can be displayed to players.
 * Titles appear in the center of the screen and can have a title, subtitle, and timing.
 * <p>
 * Both title and subtitle support MiniMessage format for colors and formatting.
 * </p>
 *
 * @param text   The text content (title and subtitle) wrapper.
 * @param timing The timing configuration for fade in, stay, and fade out.
 * @since 1.0.0
 */
public record NexoTitle(TitleText text, TitleTiming timing) {

    /**
     * Creates a new NexoTitle with the specified title, subtitle, and timing.
     *
     * @param title    the main title text, which may be null (will be treated as empty)
     * @param subtitle the subtitle text, which may be null (will be treated as empty)
     * @param timing   the timing configuration
     * @return a new NexoTitle instance
     */
    public static NexoTitle of(@Nullable String title, @Nullable String subtitle, TitleTiming timing) {
        return new NexoTitle(
                new TitleText(
                        MiniMessageText.of(title),
                        MiniMessageText.of(subtitle)
                ),
                timing
        );
    }

    /**
     * Creates a new NexoTitle with default timing (10 ticks fade in, 70 ticks stay, 20 ticks fade out).
     *
     * @param title    the main title text
     * @param subtitle the subtitle text
     * @return a new NexoTitle instance with default timing
     */
    public static NexoTitle of(@Nullable String title, @Nullable String subtitle) {
        return of(title, subtitle, TitleTiming.ofTicks(10, 70, 20));
    }

    /**
     * Sends this title to the specified audience.
     * If the audience is null, this method does nothing.
     *
     * @param audience the audience to send the title to (e.g., a Player)
     */
    public void sendTo(@Nullable Audience audience) {
        if (audience == null) {
            return;
        }

        final var adventureTitle = buildTitle();
        audience.showTitle(adventureTitle);
    }

    private Title buildTitle() {
        return Title.title(
                text.title().toComponent(),
                text.subtitle().toComponent(),
                timing.times()
        );
    }

    /**
     * Internal record that groups title and subtitle text together.
     *
     * @param title    the title text
     * @param subtitle the subtitle text
     */
    public record TitleText(MiniMessageText title, MiniMessageText subtitle) {
    }
}