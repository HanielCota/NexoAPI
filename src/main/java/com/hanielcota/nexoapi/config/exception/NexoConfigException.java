package com.hanielcota.nexoapi.config.exception;

public abstract class NexoConfigException extends RuntimeException {
    protected NexoConfigException(String message) {
        super(message);
    }

    protected NexoConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}