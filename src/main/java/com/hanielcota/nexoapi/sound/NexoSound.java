package com.hanielcota.nexoapi.sound;

import com.hanielcota.nexoapi.sound.params.SoundPitch;
import com.hanielcota.nexoapi.sound.params.SoundVolume;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a sound that can be played to players.
 * Supports volume and pitch customization.
 *
 * @param sound the Adventure Sound instance
 * @since 1.0.0
 */
public record NexoSound(@NotNull Sound sound) {

    /**
     * Creates a NexoSound from a Bukkit Sound.
     *
     * @param bukkitSound the Bukkit Sound instance
     * @return a new NexoSound instance
     * @throws IllegalArgumentException if the sound is not registered
     */
    public static NexoSound from(@NotNull org.bukkit.Sound bukkitSound) {
        NamespacedKey namespacedKey = Registry.SOUNDS.getKey(bukkitSound);

        if (namespacedKey == null) {
            throw new IllegalArgumentException("Unregistered sound: " + bukkitSound);
        }

        return fromKey(namespacedKey);
    }

    /**
     * Creates a NexoSound from a NamespacedKey.
     *
     * @param key the NamespacedKey
     * @return a new NexoSound instance
     */
    public static NexoSound fromKey(@NotNull NamespacedKey key) {
        Key adventureKey = convertToAdventureKey(key);
        return create(adventureKey, SoundVolume.NORMAL, SoundPitch.NORMAL);
    }

    private static Key convertToAdventureKey(@NotNull NamespacedKey key) {
        return Key.key(key.getNamespace(), key.getKey());
    }

    /**
     * Creates a NexoSound from a namespaced key string (e.g., "minecraft:entity.player.levelup").
     *
     * @param namespacedKey the namespaced key string
     * @return a new NexoSound instance
     * @throws IllegalArgumentException if the key is blank or invalid
     */
    public static NexoSound fromKey(@NotNull String namespacedKey) {
        if (namespacedKey.isBlank()) {
            throw new IllegalArgumentException("Namespaced key cannot be blank");
        }

        Key key = parseNamespacedKey(namespacedKey);
        return create(key, SoundVolume.NORMAL, SoundPitch.NORMAL);
    }

    private static Key parseNamespacedKey(@NotNull String namespacedKey) {
        var parts = namespacedKey.split(":", 2);

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid namespaced key: " + namespacedKey);
        }

        return Key.key(parts[0], parts[1]);
    }

    private static NexoSound create(Key key,
                                    SoundVolume volume,
                                    SoundPitch pitch) {

        Sound adventureSound = Sound.sound(
                key,
                Sound.Source.MASTER,
                volume.volume(),
                pitch.pitch()
        );

        return new NexoSound(adventureSound);
    }

    /**
     * Creates a new NexoSound with the specified volume.
     *
     * @param volume the volume to use
     * @return a new NexoSound instance with the updated volume
     */
    public NexoSound withVolume(@NotNull SoundVolume volume) {
        Sound updated = buildSoundWithVolume(volume);
        return new NexoSound(updated);
    }

    private Sound buildSoundWithVolume(SoundVolume volume) {
        return Sound.sound(
                sound.name(),
                sound.source(),
                volume.volume(),
                sound.pitch()
        );
    }

    /**
     * Creates a new NexoSound with the specified pitch.
     *
     * @param pitch the pitch to use
     * @return a new NexoSound instance with the updated pitch
     */
    public NexoSound withPitch(@NotNull SoundPitch pitch) {
        Sound updated = buildSoundWithPitch(pitch);
        return new NexoSound(updated);
    }

    private Sound buildSoundWithPitch(SoundPitch pitch) {
        return Sound.sound(
                sound.name(),
                sound.source(),
                sound.volume(),
                pitch.pitch()
        );
    }

    /**
     * Creates a new NexoSound with the specified volume value.
     *
     * @param volume the volume value (0.0 to 2.0)
     * @return a new NexoSound instance with the updated volume
     */
    public NexoSound withVolume(float volume) {
        return withVolume(SoundVolume.of(volume));
    }

    /**
     * Creates a new NexoSound with the specified pitch value.
     *
     * @param pitch the pitch value (0.5 to 2.0)
     * @return a new NexoSound instance with the updated pitch
     */
    public NexoSound withPitch(float pitch) {
        return withPitch(SoundPitch.of(pitch));
    }

    /**
     * Plays this sound to the specified audience.
     * If the audience is null, this method does nothing.
     *
     * @param audience the audience to play the sound to
     */
    public void playTo(@Nullable Audience audience) {
        if (audience == null) {
            return;
        }

        audience.playSound(sound);
    }

    /**
     * Stops this sound for the specified audience.
     * If the audience is null, this method does nothing.
     *
     * @param audience the audience to stop the sound for
     */
    public void stopFor(@Nullable Audience audience) {
        if (audience == null) {
            return;
        }

        audience.stopSound(sound);
    }
}
