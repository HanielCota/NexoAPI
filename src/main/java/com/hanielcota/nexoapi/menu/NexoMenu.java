package com.hanielcota.nexoapi.menu;

import com.hanielcota.nexoapi.menu.property.MenuSize;
import com.hanielcota.nexoapi.menu.property.MenuTitle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Abstract base class for all menus in the NexoAPI system.
 * Provides the foundation for creating and managing menu inventories.
 * <p>
 * Subclasses must implement {@link #populate(MenuView)} to define the menu's content.
 * </p>
 *
 * @since 1.0.0
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NexoMenu {

    protected final MenuTitle title;
    protected final MenuSize size;

    /**
     * Opens this menu for the specified player.
     *
     * @param player the player to open the menu for
     * @throws NullPointerException if player is null
     */
    public void openFor(@NotNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        NexoMenuHolder holder = new NexoMenuHolder(this);

        MenuSize menuSize = size;
        int slots = menuSize.slots();

        MenuTitle menuTitle = title;
        Component titleComponent = menuTitle.value();

        Inventory inventory = Bukkit.createInventory(holder, slots, titleComponent);
        holder.bindInventory(inventory);

        MenuView view = new MenuView(player, inventory);
        populate(view);

        player.openInventory(inventory);
    }

    /**
     * Populates the menu with items.
     * This method is called when the menu is opened.
     *
     * @param view the menu view containing the player and inventory
     */
    protected abstract void populate(@NotNull MenuView view);

    /**
     * Handles a click event in the menu.
     * Default implementation does nothing.
     * Subclasses can override to handle clicks.
     *
     * @param context the click context
     */
    public void handleClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");
    }

    /**
     * Handles a close event for the menu.
     * Default implementation does nothing.
     * Subclasses can override to handle close events.
     *
     * @param context the close context
     */
    public void handleClose(@NotNull MenuCloseContext context) {
        Objects.requireNonNull(context, "context cannot be null");
    }
}

