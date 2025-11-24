package com.hanielcota.nexoapi.menu.staticmenu;

import com.hanielcota.nexoapi.menu.MenuClickContext;
import com.hanielcota.nexoapi.menu.MenuView;
import com.hanielcota.nexoapi.menu.NexoMenu;
import com.hanielcota.nexoapi.menu.property.MenuSize;
import com.hanielcota.nexoapi.menu.property.MenuSlot;
import com.hanielcota.nexoapi.menu.property.MenuTitle;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base class for static menus with fixed layouts.
 * Static menus have predefined items in fixed positions.
 * <p>
 * Subclasses should define their menu layout in the constructor and pass it to the super constructor.
 * </p>
 *
 * @since 1.0.0
 */
public abstract class StaticMenu extends NexoMenu {

    private final MenuLayout layout;

    /**
     * Creates a new StaticMenu with the specified title, size, and layout.
     *
     * @param title  the menu title
     * @param size   the menu size
     * @param layout the menu layout defining items and their positions
     * @throws NullPointerException if any parameter is null
     */
    protected StaticMenu(@NotNull MenuTitle title,
                         @NotNull MenuSize size,
                         @NotNull MenuLayout layout) {
        super(title, size);

        Objects.requireNonNull(layout, "layout cannot be null");
        this.layout = layout;
    }

    @Override
    protected void populate(@NotNull MenuView view) {
        Objects.requireNonNull(view, "view cannot be null");

        Inventory inventory = view.inventory();
        layout.applyToInventory(inventory);
    }

    @Override
    public void handleClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        MenuSlot slot = context.slot();
        Optional<MenuItemDefinition> optionalDefinition = layout.findBySlot(slot);

        if (optionalDefinition.isEmpty()) {
            return;
        }

        MenuItemDefinition definition = optionalDefinition.orElseThrow();
        MenuClickHandler clickHandler = definition.clickHandler();
        clickHandler.handle(context);
    }
}

