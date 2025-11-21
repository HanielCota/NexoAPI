package com.hanielcota.nexoapi.item;

import com.hanielcota.nexoapi.item.property.ItemAmount;
import com.hanielcota.nexoapi.item.property.ItemLore;
import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Builder-style API for creating and customizing ItemStacks.
 * Supports MiniMessage format for item names and lore.
 * <p>
 * This class provides a fluent interface for building items with proper formatting.
 * </p>
 *
 * @param itemStack the underlying ItemStack being built
 * @since 1.0.0
 */
public record NexoItem(@NotNull ItemStack itemStack) {

    /**
     * Creates a new NexoItem from a material.
     *
     * @param material the material for the item
     * @return a new NexoItem instance
     */
    public static NexoItem from(@NotNull Material material) {
        return new NexoItem(new ItemStack(material));
    }

    /**
     * Creates a new NexoItem by editing an existing ItemStack.
     * The original ItemStack is cloned, so modifications won't affect the original.
     *
     * @param original the original ItemStack to edit
     * @return a new NexoItem instance with a cloned ItemStack
     */
    public static NexoItem edit(@NotNull ItemStack original) {
        return new NexoItem(original.clone());
    }

    /**
     * Sets the amount of items in the stack.
     *
     * @param amount the amount (1-64)
     * @return this NexoItem instance for method chaining
     * @throws IllegalArgumentException if amount is less than 1 or greater than 64
     */
    public NexoItem withAmount(int amount) {
        return withAmount(ItemAmount.of(amount));
    }

    /**
     * Sets the amount of items in the stack using an ItemAmount.
     *
     * @param amount the ItemAmount instance
     * @return this NexoItem instance for method chaining
     */
    public NexoItem withAmount(@NotNull ItemAmount amount) {
        itemStack.setAmount(amount.value());
        return this;
    }

    /**
     * Sets the display name of the item.
     * The name supports MiniMessage format and will have italic decoration removed.
     *
     * @param name the display name, which may be null (will clear the name)
     * @return this NexoItem instance for method chaining
     */
    public NexoItem withName(@Nullable String name) {
        return applyMeta(meta -> {
            var component = MiniMessageText.of(name).toComponent();
            var cleanComponent = component.decoration(TextDecoration.ITALIC, false);

            meta.displayName(cleanComponent);
        });
    }

    /**
     * Sets the lore of the item.
     * Each line supports MiniMessage format and will have italic decoration removed.
     *
     * @param lines the lore lines, which may be null or empty (will clear the lore)
     * @return this NexoItem instance for method chaining
     */
    public NexoItem withLore(@Nullable List<String> lines) {
        final var lore = ItemLore.from(lines);

        return applyMeta(meta -> {
            if (!lore.hasContent()) {
                return;
            }
            meta.lore(lore.lines());
        });
    }

    /**
     * Builds and returns the final ItemStack.
     *
     * @return the configured ItemStack
     */
    public ItemStack build() {
        return itemStack;
    }

    private NexoItem applyMeta(Consumer<ItemMeta> consumer) {
        final var meta = itemStack.getItemMeta();

        if (meta == null) {
            return this;
        }

        consumer.accept(meta);
        itemStack.setItemMeta(meta);
        return this;
    }
}