package com.hanielcota.nexoapi.item.skull;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.hanielcota.nexoapi.item.NexoItem;
import com.hanielcota.nexoapi.item.skull.cache.SkullProfileCache;
import com.hanielcota.nexoapi.item.skull.value.SkullOwner;
import com.hanielcota.nexoapi.item.skull.value.SkullProfile;
import com.hanielcota.nexoapi.item.skull.value.SkullTexture;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Builder for creating skull items with various sources (texture, owner).
 * Supports both synchronous and asynchronous building.
 *
 * @param profile the skull profile (may be null for empty skull)
 * @param name the display name (supports MiniMessage), may be null
 * @param lore the lore lines (supports MiniMessage), may be null
 * @since 1.0.0
 */
public record NexoSkullBuilder(
        @Nullable SkullProfile profile,
        @Nullable String name,
        @Nullable List<String> lore
) {

    /**
     * Creates a new empty NexoSkullBuilder.
     *
     * @return a new NexoSkullBuilder instance
     */
    public static NexoSkullBuilder create() {
        return new NexoSkullBuilder(null, null, null);
    }

    /**
     * Sets the texture using a base64 string.
     * If the base64 string is null or blank, the texture will not be set and this builder will be returned unchanged.
     *
     * @param base64 the base64 texture string (may be null or blank)
     * @return a new NexoSkullBuilder instance with the texture set, or unchanged if base64 is null or blank
     */
    public NexoSkullBuilder withTexture(@Nullable String base64) {
        if (base64 == null || base64.isBlank()) {
            return this;
        }
        return new NexoSkullBuilder(new SkullProfile.Texture(SkullTexture.of(base64)), name, lore);
    }

    /**
     * Sets the texture using a SkullTexture.
     *
     * @param texture the skull texture
     * @return a new NexoSkullBuilder instance with the texture set
     */
    public NexoSkullBuilder withTexture(SkullTexture texture) {
        return new NexoSkullBuilder(new SkullProfile.Texture(texture), name, lore);
    }

    /**
     * Sets the texture using a Minecraft texture URL or hash.
     * Accepts either:
     * - Full URL: http://textures.minecraft.net/texture/&lt;hash&gt;
     * - Just the hash: &lt;hash&gt;
     * If the textureUrlOrHash is null or blank, the texture will not be set and this builder will be returned unchanged.
     *
     * @param textureUrlOrHash the Minecraft texture URL or hash (may be null or blank)
     * @return a new NexoSkullBuilder instance with the texture set, or unchanged if textureUrlOrHash is null or blank
     */
    public NexoSkullBuilder withTextureUrl(@Nullable String textureUrlOrHash) {
        if (textureUrlOrHash == null || textureUrlOrHash.isBlank()) {
            return this;
        }
        return new NexoSkullBuilder(new SkullProfile.Texture(SkullTexture.fromUrl(textureUrlOrHash)), name, lore);
    }

    /**
     * Sets the owner using a SkullOwner.
     *
     * @param owner the skull owner
     * @return a new NexoSkullBuilder instance with the owner set
     */
    public NexoSkullBuilder withOwner(SkullOwner owner) {
        return new NexoSkullBuilder(new SkullProfile.Owner(owner), name, lore);
    }

    /**
     * Sets the display name of the skull item.
     * The name supports MiniMessage format and will have italic decoration removed.
     *
     * @param name the display name, which may be null (will clear the name)
     * @return a new NexoSkullBuilder instance with the name set
     */
    public NexoSkullBuilder withName(@Nullable String name) {
        return new NexoSkullBuilder(profile, name, lore);
    }

    /**
     * Sets the lore of the skull item using varargs.
     * Each line supports MiniMessage format and will have italic decoration removed.
     *
     * @param lines the lore lines (varargs)
     * @return a new NexoSkullBuilder instance with the lore set
     */
    public NexoSkullBuilder withLore(@NotNull String... lines) {
        if (lines == null || lines.length == 0) {
            return new NexoSkullBuilder(profile, name, null);
        }
        return new NexoSkullBuilder(profile, name, List.of(lines));
    }

    /**
     * Sets the lore of the skull item.
     * Each line supports MiniMessage format and will have italic decoration removed.
     *
     * @param lines the lore lines, which may be null or empty (will clear the lore)
     * @return a new NexoSkullBuilder instance with the lore set
     */
    public NexoSkullBuilder withLore(@Nullable List<String> lines) {
        return new NexoSkullBuilder(profile, name, lines);
    }

    /**
     * Builds the skull item synchronously.
     * Only works for texture-based skulls. Owner-based skulls require buildAsync().
     *
     * @return the built NexoItem
     * @throws IllegalStateException if the profile is owner-based
     */
    public NexoItem buildSync() {
        if (profile == null) {
            return createEmptySkull();
        }

        return switch (profile) {
            case SkullProfile.Texture(SkullTexture texture) -> buildFromTexture(texture);
            case SkullProfile.Owner ignored -> throw new IllegalStateException(
                    "Owner-based skulls require async build via buildAsync()."
            );
        };
    }

    /**
     * Builds the skull item synchronously and returns the ItemStack directly.
     * Only works for texture-based skulls. Owner-based skulls require buildAsyncItem().
     * This is a convenience method to avoid calling build() twice.
     *
     * @return the built ItemStack
     * @throws IllegalStateException if the profile is owner-based
     */
    public ItemStack buildSyncItem() {
        return buildSync().build();
    }

    private NexoItem createEmptySkull() {
        return applyNameAndLore(NexoItem.from(Material.PLAYER_HEAD));
    }

    private NexoItem buildFromTexture(SkullTexture texture) {
        PlayerProfile cached = getCachedProfile(texture);
        return makeItem(cached);
    }

    private PlayerProfile getCachedProfile(SkullTexture texture) {
        return SkullProfileCache.getOrCreate(texture);
    }

    /**
     * Builds the skull item asynchronously.
     * Works for both texture and owner-based skulls.
     *
     * @return a CompletableFuture that completes with the built NexoItem
     */
    public CompletableFuture<NexoItem> buildAsync() {
        if (profile == null) {
            return createEmptySkullFuture();
        }

        return resolveAndBuild(profile);
    }

    /**
     * Builds the skull item asynchronously and returns the ItemStack directly.
     * Works for both texture and owner-based skulls.
     * This is a convenience method to avoid calling build() twice.
     *
     * @return a CompletableFuture that completes with the built ItemStack
     */
    public CompletableFuture<ItemStack> buildAsyncItem() {
        return buildAsync().thenApply(NexoItem::build);
    }

    /**
     * Builds the skull item asynchronously and executes the consumer on the main thread when ready.
     * Works for both texture and owner-based skulls.
     * This method ensures the consumer runs on the main thread, preventing blocking.
     *
     * @param plugin the plugin instance (used for scheduling)
     * @param consumer the consumer to execute with the built NexoItem (runs on main thread)
     * @throws NullPointerException if plugin or consumer is null
     */
    public void buildAsync(@NotNull Plugin plugin, @NotNull Consumer<NexoItem> consumer) {
        Objects.requireNonNull(plugin, "Plugin cannot be null.");
        Objects.requireNonNull(consumer, "Consumer cannot be null.");

        buildAsync()
                .thenAccept(item -> {
                    if (!plugin.isEnabled()) {
                        return;
                    }
                    plugin.getServer().getScheduler().runTask(plugin, () -> consumer.accept(item));
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().severe("Failed to build skull: " + throwable.getMessage());
                    throwable.printStackTrace();
                    return null;
                });
    }

    /**
     * Builds the skull item asynchronously and executes the consumer on the main thread when ready.
     * Works for both texture and owner-based skulls.
     * This method ensures the consumer runs on the main thread, preventing blocking.
     * This is a convenience method that provides the ItemStack directly.
     *
     * @param plugin the plugin instance (used for scheduling)
     * @param consumer the consumer to execute with the built ItemStack (runs on main thread)
     * @throws NullPointerException if plugin or consumer is null
     */
    public void buildAsyncItem(@NotNull Plugin plugin, @NotNull Consumer<ItemStack> consumer) {
        Objects.requireNonNull(plugin, "Plugin cannot be null.");
        Objects.requireNonNull(consumer, "Consumer cannot be null.");

        buildAsyncItem()
                .thenAccept(item -> {
                    if (!plugin.isEnabled()) {
                        return;
                    }
                    plugin.getServer().getScheduler().runTask(plugin, () -> consumer.accept(item));
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().severe("Failed to build skull item: " + throwable.getMessage());
                    throwable.printStackTrace();
                    return null;
                });
    }

    private CompletableFuture<NexoItem> createEmptySkullFuture() {
        NexoItem item = applyNameAndLore(NexoItem.from(Material.PLAYER_HEAD));
        return CompletableFuture.completedFuture(item);
    }

    private CompletableFuture<NexoItem> resolveAndBuild(@NotNull SkullProfile targetProfile) {
        return targetProfile.resolve().thenApply(this::makeItem);
    }

    private NexoItem makeItem(PlayerProfile resolved) {
        ItemStack stack = createPlayerHead();
        SkullMeta meta = getSkullMeta(stack);

        if (meta == null) {
            return applyNameAndLore(NexoItem.edit(stack));
        }

        applyProfile(meta, resolved);
        stack.setItemMeta(meta);

        return applyNameAndLore(NexoItem.edit(stack));
    }

    private NexoItem applyNameAndLore(NexoItem item) {
        var result = item;
        
        if (name != null) {
            result = result.withName(name);
        }
        
        if (lore != null) {
            result = result.withLore(lore);
        }
        
        return result;
    }

    private ItemStack createPlayerHead() {
        return new ItemStack(Material.PLAYER_HEAD);
    }

    private SkullMeta getSkullMeta(ItemStack stack) {
        return (SkullMeta) stack.getItemMeta();
    }

    private void applyProfile(SkullMeta meta, PlayerProfile profile) {
        meta.setPlayerProfile(profile);
    }
}
