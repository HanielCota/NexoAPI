package com.hanielcota.nexoapi.command.model;

/**
 * Represents the execution type of a command.
 * Commands can be executed synchronously or asynchronously.
 *
 * @since 1.0.0
 */
public enum CommandExecutionType {
    /**
     * Synchronous execution (main thread).
     */
    SYNC,

    /**
     * Asynchronous execution (background thread).
     */
    ASYNC;

    /**
     * Checks if this execution type is asynchronous.
     *
     * @return true if async, false otherwise
     */
    public boolean isAsync() {
        return this == ASYNC;
    }
}
