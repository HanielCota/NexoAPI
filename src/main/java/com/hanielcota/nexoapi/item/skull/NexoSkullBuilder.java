package com.hanielcota.nexoapi.item.skull;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.hanielcota.nexoapi.item.NexoItem;
import com.hanielcota.nexoapi.item.skull.cache.SkullProfileCache;
import com.hanielcota.nexoapi.item.skull.value.SkullOwner;
import com.hanielcota.nexoapi.item.skull.value.SkullProfile;
import com.hanielcota.nexoapi.item.skull.value.SkullTexture;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
     *
     * @param base64 the base64 texture string
     * @return a new NexoSkullBuilder instance with the texture set
     */
    public NexoSkullBuilder withTexture(String base64) {
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
     *
     * @param textureUrlOrHash the Minecraft texture URL or hash
     * @return a new NexoSkullBuilder instance with the texture set
     * @throws IllegalArgumentException if textureUrlOrHash is blank or invalid
     */
    public NexoSkullBuilder withTextureUrl(@NotNull String textureUrlOrHash) {
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
        NexoItem result = item;
        
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
