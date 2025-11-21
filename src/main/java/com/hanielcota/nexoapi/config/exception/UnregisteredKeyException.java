package com.hanielcota.nexoapi.config.exception;

public class UnregisteredKeyException extends NexoConfigException {
    public UnregisteredKeyException(String key) {
        super("A chave '" + key + "' não foi registrada no Schema.");
    }
}