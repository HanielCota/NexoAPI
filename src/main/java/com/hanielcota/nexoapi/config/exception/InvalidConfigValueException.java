package com.hanielcota.nexoapi.config.exception;

import lombok.Getter;

@Getter
public class InvalidConfigValueException extends NexoConfigException {
    private final String key;
    private final Object invalidValue;

    public InvalidConfigValueException(String key, Object value, String reason) {
        super(String.format("Valor inválido para '%s': %s. Motivo: %s", key, value, reason));
        this.key = key;
        this.invalidValue = value;
    }
}