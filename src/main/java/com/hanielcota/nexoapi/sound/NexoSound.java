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

public record NexoSound(@NotNull Sound sound) {

    public static NexoSound from(@NotNull org.bukkit.Sound bukkitSound) {
        NamespacedKey namespacedKey = Registry.SOUNDS.getKey(bukkitSound);

        if (namespacedKey == null) {
            throw new IllegalArgumentException("Unregistered sound: " + bukkitSound);
        }

        return fromKey(namespacedKey);
    }

    public static NexoSound fromKey(@NotNull NamespacedKey key) {
        Key adventureKey = Key.key(key.getNamespace(), key.getKey());
        return create(adventureKey, SoundVolume.NORMAL, SoundPitch.NORMAL);
    }

    public static NexoSound fromKey(@NotNull String namespacedKey) {
        if (namespacedKey.isBlank()) {
            throw new IllegalArgumentException("Namespaced key cannot be blank");
        }

        String[] parts = namespacedKey.split(":", 2);

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid namespaced key: " + namespacedKey);
        }

        Key key = Key.key(parts[0], parts[1]);
        return create(key, SoundVolume.NORMAL, SoundPitch.NORMAL);
    }

    public NexoSound withVolume(@NotNull SoundVolume volume) {
        Sound updated = Sound.sound(
                sound.name(),
                sound.source(),
                volume.volume(),
                sound.pitch()
        );

        return new NexoSound(updated);
    }

    public NexoSound withPitch(@NotNull SoundPitch pitch) {
        Sound updated = Sound.sound(
                sound.name(),
                sound.source(),
                sound.volume(),
                pitch.pitch()
        );

        return new NexoSound(updated);
    }

    public NexoSound withVolume(float volume) {
        return withVolume(SoundVolume.of(volume));
    }

    public NexoSound withPitch(float pitch) {
        return withPitch(SoundPitch.of(pitch));
    }

    public void playTo(@Nullable Audience audience) {
        if (audience == null) {
            return;
        }

        audience.playSound(sound);
    }

    public void stopFor(@Nullable Audience audience) {
        if (audience == null) {
            return;
        }

        audience.stopSound(sound);
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
}
