package com.hanielcota.nexoapi.item;

import com.hanielcota.nexoapi.item.amount.ItemAmount;
import com.hanielcota.nexoapi.item.lore.ItemLore;
import com.hanielcota.nexoapi.text.MiniMessageText;
import net.kyori.adventure.text.Component;
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
 *
 * @param stack the underlying ItemStack being built
 * @since 1.0.0
 */
public record NexoItem(@NotNull ItemStack stack) {

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
        stack.setAmount(amount.amount());
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
            Component parsed = parseName(name);
            Component cleaned = stripItalic(parsed);
            meta.displayName(cleaned);
        });
    }

    private Component parseName(@Nullable String name) {
        MiniMessageText text = MiniMessageText.of(name);
        return text.toComponent();
    }

    private Component stripItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Sets the lore of the item.
     * Each line supports MiniMessage format and will have italic decoration removed.
     *
     * @param lines the lore lines, which may be null or empty (will clear the lore)
     * @return this NexoItem instance for method chaining
     */
    public NexoItem withLore(@Nullable List<String> lines) {
        var lore = ItemLore.from(lines);

        return applyMeta(meta -> {
            if (!lore.hasContent()) {
                return;
            }

            meta.lore(lore.components());
        });
    }

    /**
     * Builds and returns the final ItemStack.
     *
     * @return the configured ItemStack
     */
    public ItemStack build() {
        return stack;
    }

    private NexoItem applyMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) {
            return this;
        }

        consumer.accept(meta);
        stack.setItemMeta(meta);
        return this;
    }
}
