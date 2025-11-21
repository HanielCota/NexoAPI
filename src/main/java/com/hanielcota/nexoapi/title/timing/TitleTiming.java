package com.hanielcota.nexoapi.title.timing;

import com.hanielcota.nexoapi.title.time.TickDuration;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

public record TitleAnimation(@NotNull Title.Times times) {

    public static TitleAnimation smooth(long fadeIn, long stay, long fadeOut) {
        final var adventureTimes = Title.Times.times(
                TickDuration.of(fadeIn).toJavaDuration(),
                TickDuration.of(stay).toJavaDuration(),
                TickDuration.of(fadeOut).toJavaDuration()
        );

        return new TitleAnimation(adventureTimes);
    }
}