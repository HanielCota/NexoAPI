package com.hanielcota.nexoapi.config.exception;

public class InvalidKeyFormatException extends NexoConfigException {
    public InvalidKeyFormatException(String invalidKey) {
        super("A chave de configuração '" + invalidKey + "' possui formato inválido.");
    }
}