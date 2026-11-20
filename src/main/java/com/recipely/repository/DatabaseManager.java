package com.recipely.repository;

import com.recipely.exception.DatabaseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Owns SQLite JDBC connections and ensures the local database folder exists. */
public final class DatabaseManager {
    private static final String URL = "jdbc:sqlite:data/recipely.db";

    public DatabaseManager() {
        try {
            Files.createDirectories(Path.of("data"));
            Class.forName("org.sqlite.JDBC");
        } catch (IOException | ClassNotFoundException exception) {
            throw new DatabaseException("Could not prepare the SQLite database.", exception);
        }
    }

    public Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException exception) {
            throw new DatabaseException("Could not connect to the SQLite database.", exception);
        }
    }
}
