package com.hanielcota.nexoapi.config.exception;

public class ConfigurationPersistenceException extends NexoConfigException {
    public ConfigurationPersistenceException(String fileName, Throwable cause) {
        super("Erro crítico de I/O no arquivo: " + fileName, cause);
    }
}