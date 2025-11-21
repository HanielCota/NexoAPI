package com.hanielcota.nexoapi.config.persistence;

import com.hanielcota.nexoapi.config.exception.ConfigurationPersistenceException;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class YamlPersistence {

    private final File file;
    private final YamlConfiguration yaml;

    public YamlPersistence(@NonNull File file) {
        this.file = file;
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    public void applyDefaults(@NonNull Map<String, Object> defaults) {
        defaults.forEach(yaml::addDefault);
        yaml.options().copyDefaults(true);
        save();
    }

    public Object read(@NonNull String path) {
        return yaml.get(path);
    }

    public void write(@NonNull String path, @NonNull Object value) {
        yaml.set(path, value);
    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new ConfigurationPersistenceException(file.getName(), e);
        }
    }
}