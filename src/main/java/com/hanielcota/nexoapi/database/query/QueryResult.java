package com.hanielcota.nexoapi.database.query;

import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Represents the result of a database query.
 * Provides convenient methods to access query results.
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @since 1.0.0
 */
public final class QueryResult {

    private final List<Map<String, Object>> rows;
    private final List<String> columnNames;
    private final int rowCount;

    private QueryResult(List<Map<String, Object>> rows, List<String> columnNames) {
        this.rows = List.copyOf(rows);
        this.columnNames = List.copyOf(columnNames);
        this.rowCount = rows.size();
    }

    /**
     * Creates an empty query result.
     *
     * @return an empty QueryResult
     */
    public static QueryResult empty() {
        return new QueryResult(List.of(), List.of());
    }

    /**
     * Creates a query result from a ResultSet.
     * The ResultSet is fully consumed and closed after this method.
     *
     * @param resultSet the result set to convert
     * @return a new QueryResult containing all rows from the result set
     * @throws SQLException if a database access error occurs
     */
    public static QueryResult from(@NonNull ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        List<String> columnNames = extractColumnNames(resultSet);

        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (String columnName : columnNames) {
                row.put(columnName, resultSet.getObject(columnName));
            }
            rows.add(row);
        }

        return new QueryResult(rows, columnNames);
    }

    private static List<String> extractColumnNames(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> columnNames = new ArrayList<>(columnCount);

        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        return columnNames;
    }

    /**
     * Gets all rows in the result.
     *
     * @return an immutable list of rows
     */
    public List<Map<String, Object>> getRows() {
        return rows;
    }

    /**
     * Gets the first row in the result, if available.
     *
     * @return an Optional containing the first row, or empty if no rows exist
     */
    public Optional<Map<String, Object>> getFirstRow() {
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    /**
     * Gets a specific column value from the first row.
     *
     * @param columnName the column name
     * @param type       the expected type
     * @param <T>        the type parameter
     * @return an Optional containing the value, or empty if not found
     */
    public <T> Optional<T> getFirstValue(@NonNull String columnName, @NonNull Class<T> type) {
        return getFirstRow()
                .map(row -> row.get(columnName))
                .map(type::cast);
    }

    /**
     * Gets a specific column value from the first row as a String.
     *
     * @param columnName the column name
     * @return an Optional containing the string value, or empty if not found
     */
    public Optional<String> getFirstString(@NonNull String columnName) {
        return getFirstValue(columnName, String.class);
    }

    /**
     * Gets a specific column value from the first row as an Integer.
     *
     * @param columnName the column name
     * @return an Optional containing the integer value, or empty if not found
     */
    public Optional<Integer> getFirstInt(@NonNull String columnName) {
        return getFirstRow()
                .map(row -> row.get(columnName))
                .map(value -> {
                    if (value instanceof Number) {
                        return ((Number) value).intValue();
                    }
                    return null;
                });
    }

    /**
     * Gets a specific column value from the first row as a Long.
     *
     * @param columnName the column name
     * @return an Optional containing the long value, or empty if not found
     */
    public Optional<Long> getFirstLong(@NonNull String columnName) {
        return getFirstRow()
                .map(row -> row.get(columnName))
                .map(value -> {
                    if (value instanceof Number) {
                        return ((Number) value).longValue();
                    }
                    return null;
                });
    }

    /**
     * Gets a specific column value from the first row as a Double.
     *
     * @param columnName the column name
     * @return an Optional containing the double value, or empty if not found
     */
    public Optional<Double> getFirstDouble(@NonNull String columnName) {
        return getFirstRow()
                .map(row -> row.get(columnName))
                .map(value -> {
                    if (value instanceof Number) {
                        return ((Number) value).doubleValue();
                    }
                    return null;
                });
    }

    /**
     * Gets a specific column value from the first row as a Boolean.
     *
     * @param columnName the column name
     * @return an Optional containing the boolean value, or empty if not found
     */
    public Optional<Boolean> getFirstBoolean(@NonNull String columnName) {
        return getFirstValue(columnName, Boolean.class);
    }

    /**
     * Gets all column names in the result.
     *
     * @return an immutable list of column names
     */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Gets the number of rows in the result.
     *
     * @return the row count
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Checks if the result is empty (no rows).
     *
     * @return true if the result has no rows, false otherwise
     */
    public boolean isEmpty() {
        return rowCount == 0;
    }

    /**
     * Checks if the result has rows.
     *
     * @return true if the result has at least one row, false otherwise
     */
    public boolean hasRows() {
        return rowCount > 0;
    }

    @Override
    public String toString() {
        return String.format("QueryResult{rowCount=%d, columns=%s}", rowCount, columnNames);
    }
}

