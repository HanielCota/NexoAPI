package com.hanielcota.nexoapi.database.transaction;

/**
 * Encapsulates transaction state following Object Calisthenics principles.
 * Single responsibility: manage transaction completion state.
 * <p>
 * This class ensures only two instance variables as per Object Calisthenics rule #8.
 * </p>
 *
 * @since 1.0.0
 */
final class TransactionState {

    private boolean completed;
    private boolean rolledBack;

    TransactionState() {
        this.completed = false;
        this.rolledBack = false;
    }

    boolean isCompleted() {
        return completed;
    }

    boolean isRolledBack() {
        return rolledBack;
    }

    boolean canExecute() {
        return !completed;
    }

    void markCompleted() {
        completed = true;
    }

    void markRolledBack() {
        rolledBack = true;
        completed = true;
    }

    void ensureNotCompleted() {
        if (completed) {
            throw new IllegalStateException("Transaction has already been completed");
        }
    }
}

