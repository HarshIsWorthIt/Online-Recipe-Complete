package com.recipely.repository;

import com.recipely.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Creates the normalized JDBC schema and safe demo content on first run. */
public final class DatabaseInitializer {
    private final DatabaseManager database;

    public DatabaseInitializer(DatabaseManager database) { this.database = database; }

    public void initialize() {
        try (Connection connection = database.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL,
                  email TEXT NOT NULL UNIQUE,
                  password_hash TEXT NOT NULL,
                  role TEXT NOT NULL,
                  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS recipes (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  contributor_id INTEGER NOT NULL,
                  title TEXT NOT NULL,
                  description TEXT NOT NULL,
                  ingredients TEXT NOT NULL,
                  instructions TEXT NOT NULL,
                  category TEXT NOT NULL,
                  cuisine TEXT NOT NULL,
                  nation TEXT NOT NULL,
                  taste TEXT NOT NULL,
                  prep_minutes INTEGER NOT NULL,
                  cook_minutes INTEGER NOT NULL,
                  status TEXT NOT NULL,
                  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY(contributor_id) REFERENCES users(id)
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reviews (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  recipe_id INTEGER NOT NULL,
                  user_id INTEGER NOT NULL,
                  rating INTEGER NOT NULL CHECK(rating BETWEEN 1 AND 5),
                  comment TEXT NOT NULL,
                  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  UNIQUE(recipe_id, user_id),
                  FOREIGN KEY(recipe_id) REFERENCES recipes(id),
                  FOREIGN KEY(user_id) REFERENCES users(id)
                )
                """);
            seed(connection);
        } catch (SQLException exception) {
            throw new com.recipely.exception.DatabaseException("Could not initialize database tables.", exception);
        }
    }

    private void seed(Connection connection) throws SQLException {
        insertUserIfAbsent(connection, "Avery Admin", "admin@recipely.local", "Admin@123", "ADMIN");
        insertUserIfAbsent(connection, "Maya Patel", "maya@recipely.local", "Chef@123", "CONTRIBUTOR");
        insertUserIfAbsent(connection, "Jordan Lee", "jordan@recipely.local", "Explore@123", "EXPLORER");

        long mayaId;
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery("SELECT id FROM users WHERE email = 'maya@recipely.local'")) {
            if (!result.next()) throw new SQLException("Could not find seeded contributor.");
            mayaId = result.getLong(1);
        }
        insertRecipeIfAbsent(connection, mayaId, "Masala Chickpea Skillet",
            "Tomato-rich chickpeas with warming spices and lime.",
            "2 cans chickpeas|1 onion|2 tomatoes|Garam masala|Lime",
            "Cook onion and spices|Add tomatoes and chickpeas|Finish with lime",
            "Dinner", "Indian", "India", "Bold & spicy", 10, 25);
        insertRecipeIfAbsent(connection, mayaId, "Miso Mushroom Ramen",
            "A savoury noodle bowl with mushrooms and greens.",
            "Ramen noodles|Miso paste|Mushrooms|Spinach|Spring onion",
            "Build miso broth|Saute mushrooms|Cook noodles and add greens",
            "Dinner", "Japanese", "Japan", "Deeply savoury", 10, 18);
        insertRecipeIfAbsent(connection, mayaId, "Greek Lemon Chickpea Salad",
            "A bright salad with herbs, olives and lemon.",
            "Chickpeas|Cucumber|Tomatoes|Olives|Feta|Lemon",
            "Chop vegetables|Whisk lemon dressing|Toss and serve",
            "Lunch", "Greek", "Greece", "Bright & tangy", 15, 0);
    }

    private void insertUserIfAbsent(Connection connection, String name, String email, String password, String role) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT OR IGNORE INTO users(name, email, password_hash, role) VALUES (?, ?, ?, ?)
                """)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, PasswordUtil.hash(password));
            statement.setString(4, role);
            statement.executeUpdate();
        }
    }

    private void insertRecipeIfAbsent(Connection connection, long contributorId, String title, String description,
                                      String ingredients, String instructions, String category, String cuisine,
                                      String nation, String taste, int prep, int cook) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO recipes(contributor_id,title,description,ingredients,instructions,category,cuisine,nation,taste,prep_minutes,cook_minutes,status)
                SELECT ?,?,?,?,?,?,?,?,?,?,?, 'APPROVED'
                WHERE NOT EXISTS (SELECT 1 FROM recipes WHERE title = ?)
                """)) {
            statement.setLong(1, contributorId);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setString(4, ingredients);
            statement.setString(5, instructions);
            statement.setString(6, category);
            statement.setString(7, cuisine);
            statement.setString(8, nation);
            statement.setString(9, taste);
            statement.setInt(10, prep);
            statement.setInt(11, cook);
            statement.setString(12, title);
            statement.executeUpdate();
        }
    }
}
