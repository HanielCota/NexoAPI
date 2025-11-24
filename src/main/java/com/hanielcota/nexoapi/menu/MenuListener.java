package com.hanielcota.nexoapi.menu;

import com.hanielcota.nexoapi.menu.property.MenuSlot;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Event listener for menu interactions.
 * Handles click and close events for NexoMenu instances.
 * <p>
 * Register this listener in your plugin's onEnable:
 * <pre>{@code
 * getServer().getPluginManager().registerEvents(new MenuListener(), this);
 * }</pre>
 * </p>
 *
 * @since 1.0.0
 */
@RequiredArgsConstructor
public final class MenuListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory == null) {
            return;
        }

        InventoryHolder holder = clickedInventory.getHolder();

        if (!(holder instanceof NexoMenuHolder nexoMenuHolder)) {
            return;
        }

        HumanEntity clicker = event.getWhoClicked();

        if (!(clicker instanceof Player player)) {
            return;
        }

        event.setCancelled(true);

        int slotIndex = event.getSlot();

        if (slotIndex < 0) {
            return;
        }

        MenuSlot slot = MenuSlot.ofIndex(slotIndex);

        MenuClickContext context = new MenuClickContext(
                player,
                clickedInventory,
                slot,
                event.getClick(),
                event
        );

        NexoMenu menu = nexoMenuHolder.menu();
        menu.handleClick(context);
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof NexoMenuHolder nexoMenuHolder)) {
            return;
        }

        HumanEntity closer = event.getPlayer();

        if (!(closer instanceof Player player)) {
            return;
        }

        MenuCloseContext context = new MenuCloseContext(
                player,
                inventory,
                event
        );

        NexoMenu menu = nexoMenuHolder.menu();
        menu.handleClose(context);
    }
}

