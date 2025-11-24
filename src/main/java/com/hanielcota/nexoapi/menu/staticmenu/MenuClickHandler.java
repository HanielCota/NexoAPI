package com.hanielcota.nexoapi.menu.staticmenu;

import com.hanielcota.nexoapi.menu.MenuClickContext;
import org.jetbrains.annotations.NotNull;

/**
 * Functional interface for handling menu item clicks.
 * Used to define click behavior for items in static menus.
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface MenuClickHandler {

    /**
     * Handles a click on a menu item.
     *
     * @param context the click context containing all relevant information
     */
    void handle(@NotNull MenuClickContext context);
}

