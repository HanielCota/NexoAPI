package com.hanielcota.nexoapi.config.exception;

public class ConfigurationInitializationException extends NexoConfigException {
    public ConfigurationInitializationException(String reason) {
        super("Falha ao inicializar configuração: " + reason);
    }
}