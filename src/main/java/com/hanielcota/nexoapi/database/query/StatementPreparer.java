package com.hanielcota.nexoapi.database.query;

import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Prepares SQL statements following Object Calisthenics principles.
 * Single responsibility: statement preparation logic.
 * <p>
 * This class encapsulates all statement preparation behavior.
 * </p>
 *
 * @since 1.0.0
 */
final class StatementPreparer {

    private StatementPreparer() {
        throw new UnsupportedOperationException("Utility class");
    }

    static PreparedStatement prepare(@NonNull Connection connection, @NonNull PreparedQuery query) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(query, "Query cannot be null");

        PreparedStatement statement = connection.prepareStatement(query.sql());
        bindParameters(statement, query.parameters());
        return statement;
    }

    static PreparedStatement prepareWithGeneratedKeys(@NonNull Connection connection, @NonNull PreparedQuery query) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(query, "Query cannot be null");

        PreparedStatement statement = connection.prepareStatement(query.sql(), Statement.RETURN_GENERATED_KEYS);
        bindParameters(statement, query.parameters());
        return statement;
    }

    private static void bindParameters(PreparedStatement statement, Object[] parameters) throws SQLException {
        for (int index = 0; index < parameters.length; index++) {
            bindParameter(statement, index, parameters[index]);
        }
    }

    private static void bindParameter(PreparedStatement statement, int index, Object value) throws SQLException {
        int parameterIndex = index + 1; // JDBC uses 1-based indexing
        statement.setObject(parameterIndex, value);
    }
}

