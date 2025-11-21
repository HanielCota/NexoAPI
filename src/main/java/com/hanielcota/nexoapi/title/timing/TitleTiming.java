package com.hanielcota.nexoapi.title.timing;

import com.hanielcota.nexoapi.title.time.TickDuration;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the timing configuration for a title display.
 * Controls how long the title takes to fade in, how long it stays visible, and how long it takes to fade out.
 *
 * @param times the Adventure Title.Times instance
 * @since 1.0.0
 */
public record TitleTiming(@NotNull Title.Times times) {

    /**
     * Creates a new TitleTiming from tick values.
     * All values must be non-negative. If any value is negative, an IllegalArgumentException will be thrown.
     *
     * @param fadeInTicks  the number of ticks for the title to fade in (must be &gt;= 0)
     * @param stayTicks    the number of ticks the title stays visible (must be &gt;= 0)
     * @param fadeOutTicks the number of ticks for the title to fade out (must be &gt;= 0)
     * @return a new TitleTiming instance
     * @throws IllegalArgumentException if any tick value is negative
     */
    public static TitleTiming ofTicks(long fadeInTicks, long stayTicks, long fadeOutTicks) {
        var times = Title.Times.times(
                TickDuration.of(fadeInTicks).toDuration(),
                TickDuration.of(stayTicks).toDuration(),
                TickDuration.of(fadeOutTicks).toDuration()
        );

        return new TitleTiming(times);
    }
}
