package com.hanielcota.nexoapi.database.query;

import lombok.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Extracts generated keys from insert operations following Object Calisthenics.
 * Single responsibility: generated key extraction.
 * <p>
 * This class encapsulates the behavior of extracting auto-generated IDs.
 * </p>
 *
 * @since 1.0.0
 */
final class GeneratedKeyExtractor {

    private static final long NO_KEY_GENERATED = -1L;

    private GeneratedKeyExtractor() {
        throw new UnsupportedOperationException("Utility class");
    }

    static long extractFrom(@NonNull PreparedStatement statement) throws SQLException {
        Objects.requireNonNull(statement, "Statement cannot be null");

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            return extractKeyFromResultSet(generatedKeys);
        }
    }

    private static long extractKeyFromResultSet(ResultSet generatedKeys) throws SQLException {
        if (hasGeneratedKey(generatedKeys)) {
            return extractFirstKey(generatedKeys);
        }
        
        return NO_KEY_GENERATED;
    }

    private static boolean hasGeneratedKey(ResultSet generatedKeys) throws SQLException {
        return generatedKeys.next();
    }

    private static long extractFirstKey(ResultSet generatedKeys) throws SQLException {
        return generatedKeys.getLong(1);
    }
}

