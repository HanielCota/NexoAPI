package com.hanielcota.nexoapi.database.logging;

import com.hanielcota.nexoapi.database.query.PreparedQuery;
import lombok.NonNull;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Utilitário para logging de operações de banco de dados.
 * Fornece métodos comuns de logging com validação integrada.
 * <p>
 * Esta classe centraliza a lógica de logging que era duplicada entre
 * QueryLogger e TransactionLogger, reduzindo duplicação e garantindo
 * consistência no formato de logs.
 * </p>
 *
 * @since 1.0.0
 */
public final class DatabaseLogger {
    
    private DatabaseLogger() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Loga falha na execução de uma query com contexto.
     *
     * @param logger    o logger a usar
     * @param context   contexto da operação (ex: "query", "transaction")
     * @param query     a query que falhou
     * @param exception a exceção que ocorreu
     */
    public static void logQueryFailure(
        @NonNull Logger logger,
        @NonNull String context,
        @NonNull PreparedQuery query,
        @NonNull SQLException exception
    ) {
        Objects.requireNonNull(logger, "Logger cannot be null");
        Objects.requireNonNull(context, "Context cannot be null");
        Objects.requireNonNull(query, "Query cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");
        
        logger.error("Failed to execute query in {}: {}", context, query.sql(), exception);
    }
    
    /**
     * Loga falha na execução de um update com contexto.
     *
     * @param logger    o logger a usar
     * @param context   contexto da operação
     * @param query     a query que falhou
     * @param exception a exceção que ocorreu
     */
    public static void logUpdateFailure(
        @NonNull Logger logger,
        @NonNull String context,
        @NonNull PreparedQuery query,
        @NonNull SQLException exception
    ) {
        Objects.requireNonNull(logger, "Logger cannot be null");
        Objects.requireNonNull(context, "Context cannot be null");
        Objects.requireNonNull(query, "Query cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");
        
        logger.error("Failed to execute update in {}: {}", context, query.sql(), exception);
    }
    
    /**
     * Loga sucesso na execução de um update com contexto.
     *
     * @param logger       o logger a usar
     * @param context      contexto da operação
     * @param affectedRows número de linhas afetadas
     */
    public static void logUpdateSuccess(
        @NonNull Logger logger,
        @NonNull String context,
        int affectedRows
    ) {
        Objects.requireNonNull(logger, "Logger cannot be null");
        Objects.requireNonNull(context, "Context cannot be null");
        
        logger.debug("Update executed in {}, affected rows: {}", context, affectedRows);
    }
    
    /**
     * Loga falha na execução de SQL genérico com contexto.
     *
     * @param logger    o logger a usar
     * @param context   contexto da operação
     * @param sql       o SQL que falhou
     * @param exception a exceção que ocorreu
     */
    public static void logExecuteFailure(
        @NonNull Logger logger,
        @NonNull String context,
        @NonNull String sql,
        @NonNull SQLException exception
    ) {
        Objects.requireNonNull(logger, "Logger cannot be null");
        Objects.requireNonNull(context, "Context cannot be null");
        Objects.requireNonNull(sql, "SQL cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");
        
        logger.error("Failed to execute SQL in {}: {}", context, sql, exception);
    }
    
    /**
     * Loga sucesso na execução de SQL genérico com contexto.
     *
     * @param logger  o logger a usar
     * @param context contexto da operação
     * @param sql     o SQL que foi executado
     */
    public static void logExecuteSuccess(
        @NonNull Logger logger,
        @NonNull String context,
        @NonNull String sql
    ) {
        Objects.requireNonNull(logger, "Logger cannot be null");
        Objects.requireNonNull(context, "Context cannot be null");
        Objects.requireNonNull(sql, "SQL cannot be null");
        
        logger.debug("SQL executed successfully in {}: {}", context, sql);
    }
}

