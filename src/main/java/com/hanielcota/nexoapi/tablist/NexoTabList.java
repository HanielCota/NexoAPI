package com.hanielcota.nexoapi.tablist;

import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a tab list header and footer that can be displayed to players.
 * Tab list headers and footers appear above and below the player list.
 * <p>
 * Both header and footer support MiniMessage format for colors and formatting.
 * </p>
 *
 * @param header the header text
 * @param footer the footer text
 * @since 1.0.0
 */
public record NexoTabList(
        MiniMessageText header,
        MiniMessageText footer
) {

    private static final NexoTabList EMPTY = new NexoTabList(
            MiniMessageText.of(null),
            MiniMessageText.of(null)
    );

    public NexoTabList {
        if (header == null) {
            throw new IllegalArgumentException("header cannot be null");
        }

        if (footer == null) {
            throw new IllegalArgumentException("footer cannot be null");
        }
    }

    /**
     * Creates a new NexoTabList with the specified header and footer.
     *
     * @param header the header text, which may be null (will be treated as empty)
     * @param footer the footer text, which may be null (will be treated as empty)
     * @return a new NexoTabList instance
     */
    public static NexoTabList of(@Nullable String header, @Nullable String footer) {
        return new NexoTabList(
                MiniMessageText.of(header),
                MiniMessageText.of(footer)
        );
    }

    /**
     * Creates a new NexoTabList with only a header.
     *
     * @param header the header text, which may be null (will be treated as empty)
     * @return a new NexoTabList instance with empty footer
     */
    public static NexoTabList ofHeader(@Nullable String header) {
        return of(header, null);
    }

    /**
     * Creates a new NexoTabList with only a footer.
     *
     * @param footer the footer text, which may be null (will be treated as empty)
     * @return a new NexoTabList instance with empty header
     */
    public static NexoTabList ofFooter(@Nullable String footer) {
        return of(null, footer);
    }

    /**
     * Creates an empty NexoTabList with both header and footer empty.
     *
     * @return an empty NexoTabList instance
     */
    public static NexoTabList empty() {
        return EMPTY;
    }

    /**
     * Clears the tab list header and footer for the specified audience.
     * This is equivalent to sending an empty tab list.
     *
     * @param audience the audience to clear the tab list for
     */
    public static void clear(@Nullable Audience audience) {
        if (audience == null) {
            return;
        }

        EMPTY.sendTo(audience);
    }

    /**
     * Sends this tab list to the specified audience.
     * If the audience is null, this method does nothing.
     *
     * @param audience the audience to send the tab list to (e.g., a Player)
     */
    public void sendTo(@Nullable Audience audience) {
        if (audience == null) {
            return;
        }

        audience.sendPlayerListHeaderAndFooter(
                header.toComponent(),
                footer.toComponent()
        );
    }
}
