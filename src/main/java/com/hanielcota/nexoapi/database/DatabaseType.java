package com.hanielcota.nexoapi.database;

import lombok.Getter;

/**
 * Represents supported database types.
 * Each type includes its JDBC driver class name for automatic loading.
 * <p>
 * This enum provides information about common database systems and their JDBC drivers.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
public enum DatabaseType {

    /**
     * MySQL database.
     * Driver: com.mysql.cj.jdbc.Driver
     */
    MYSQL("com.mysql.cj.jdbc.Driver", "MySQL"),

    /**
     * MariaDB database.
     * Driver: org.mariadb.jdbc.Driver
     */
    MARIADB("org.mariadb.jdbc.Driver", "MariaDB"),

    /**
     * PostgreSQL database.
     * Driver: org.postgresql.Driver
     */
    POSTGRESQL("org.postgresql.Driver", "PostgreSQL"),

    /**
     * SQLite database (embedded).
     * Driver: org.sqlite.JDBC
     */
    SQLITE("org.sqlite.JDBC", "SQLite"),

    /**
     * H2 database (embedded/in-memory).
     * Driver: org.h2.Driver
     */
    H2("org.h2.Driver", "H2");

    private final String driverClassName;
    private final String displayName;

    DatabaseType(String driverClassName, String displayName) {
        this.driverClassName = driverClassName;
        this.displayName = displayName;
    }

    /**
     * Checks if the JDBC driver for this database type is available.
     *
     * @return true if the driver is available, false otherwise
     */
    public boolean isDriverAvailable() {
        try {
            Class.forName(driverClassName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Detects the database type from a JDBC URL.
     *
     * @param jdbcUrl the JDBC URL
     * @return the detected DatabaseType, or null if not recognized
     */
    public static DatabaseType fromUrl(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return null;
        }

        String lowerUrl = jdbcUrl.toLowerCase();

        if (lowerUrl.startsWith("jdbc:mysql:")) {
            return MYSQL;
        } else if (lowerUrl.startsWith("jdbc:mariadb:")) {
            return MARIADB;
        } else if (lowerUrl.startsWith("jdbc:postgresql:")) {
            return POSTGRESQL;
        } else if (lowerUrl.startsWith("jdbc:sqlite:")) {
            return SQLITE;
        } else if (lowerUrl.startsWith("jdbc:h2:")) {
            return H2;
        }

        return null;
    }
}

