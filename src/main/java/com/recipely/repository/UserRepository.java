package com.recipely.repository;

import com.recipely.exception.DatabaseException;
import com.recipely.model.Role;
import com.recipely.model.User;
import com.recipely.model.UserFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UserRepository implements Repository<User> {
    private final DatabaseManager database;

    public UserRepository(DatabaseManager database) { this.database = database; }

    @Override public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users ORDER BY name"); ResultSet rows = statement.executeQuery()) {
            while (rows.next()) users.add(map(rows));
            return users;
        } catch (SQLException exception) { throw new DatabaseException("Could not load users.", exception); }
    }

    @Override public Optional<User> findById(long id) {
        return findOne("SELECT * FROM users WHERE id = ?", id);
    }

    public Optional<User> findByEmail(String email) {
        return findOne("SELECT * FROM users WHERE lower(email) = lower(?)", email);
    }

    private Optional<User> findOne(String sql, Object value) {
        try (Connection connection = database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(map(rows)) : Optional.empty();
            }
        } catch (SQLException exception) { throw new DatabaseException("Could not load user.", exception); }
    }

    @Override public User save(User user) {
        try (Connection connection = database.getConnection(); PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users(name,email,password_hash,role) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (keys.next()) user.setId(keys.getLong(1)); }
            return user;
        } catch (SQLException exception) { throw new DatabaseException("Could not save user.", exception); }
    }

    private User map(ResultSet row) throws SQLException {
        User user = UserFactory.create(row.getString("name"), row.getString("email"), row.getString("password_hash"), Role.valueOf(row.getString("role")));
        user.setId(row.getLong("id"));
        return user;
    }
}
