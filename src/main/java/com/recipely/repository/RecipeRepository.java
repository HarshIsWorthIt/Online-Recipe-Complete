package com.recipely.repository;

import com.recipely.exception.DatabaseException;
import com.recipely.model.Recipe;
import com.recipely.model.RecipeStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class RecipeRepository implements Repository<Recipe> {
    private final DatabaseManager database;

    public RecipeRepository(DatabaseManager database) { this.database = database; }

    @Override public List<Recipe> findAll() { return findByStatus(null); }

    public List<Recipe> findApproved(String search, String nation, String taste, int maxMinutes) {
        StringBuilder sql = new StringBuilder("""
            SELECT r.*, u.name AS contributor_name FROM recipes r JOIN users u ON u.id = r.contributor_id
            WHERE r.status = 'APPROVED'
            """);
        List<Object> values = new ArrayList<>();
        if (search != null && !search.isBlank()) { sql.append(" AND (r.title LIKE ? OR r.ingredients LIKE ?)"); values.add("%" + search.trim() + "%"); values.add("%" + search.trim() + "%"); }
        if (nation != null && !nation.isBlank()) { sql.append(" AND r.nation = ?"); values.add(nation.trim()); }
        if (taste != null && !taste.isBlank()) { sql.append(" AND r.taste = ?"); values.add(taste.trim()); }
        if (maxMinutes > 0) { sql.append(" AND r.prep_minutes + r.cook_minutes <= ?"); values.add(maxMinutes); }
        sql.append(" ORDER BY r.created_at DESC");
        return query(sql.toString(), values);
    }

    public List<Recipe> findByStatus(RecipeStatus status) {
        String sql = "SELECT r.*, u.name AS contributor_name FROM recipes r JOIN users u ON u.id = r.contributor_id";
        List<Object> values = new ArrayList<>();
        if (status != null) { sql += " WHERE r.status = ?"; values.add(status.name()); }
        return query(sql + " ORDER BY r.created_at DESC", values);
    }

    public long countByStatus(RecipeStatus status) {
        try (Connection connection = database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM recipes WHERE status = ?")) {
            statement.setString(1, status.name());
            try (ResultSet row = statement.executeQuery()) { return row.getLong(1); }
        } catch (SQLException exception) { throw new DatabaseException("Could not count recipes.", exception); }
    }

    @Override public Optional<Recipe> findById(long id) {
        List<Recipe> recipes = query("SELECT r.*, u.name AS contributor_name FROM recipes r JOIN users u ON u.id = r.contributor_id WHERE r.id = ?", List.<Object>of(id));
        return recipes.stream().findFirst();
    }

    @Override public Recipe save(Recipe recipe) {
        try (Connection connection = database.getConnection(); PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO recipes(contributor_id,title,description,ingredients,instructions,category,cuisine,nation,taste,prep_minutes,cook_minutes,status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, recipe.getContributorId());
            statement.setString(2, recipe.getTitle());
            statement.setString(3, recipe.getDescription());
            statement.setString(4, String.join("|", recipe.getIngredients()));
            statement.setString(5, String.join("|", recipe.getInstructions()));
            statement.setString(6, recipe.getCategory());
            statement.setString(7, recipe.getCuisine());
            statement.setString(8, recipe.getNation());
            statement.setString(9, recipe.getTaste());
            statement.setInt(10, recipe.getPrepMinutes());
            statement.setInt(11, recipe.getCookMinutes());
            statement.setString(12, recipe.getStatus().name());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (keys.next()) recipe.setId(keys.getLong(1)); }
            return recipe;
        } catch (SQLException exception) { throw new DatabaseException("Could not save recipe.", exception); }
    }

    public void updateStatus(long recipeId, RecipeStatus status) {
        try (Connection connection = database.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE recipes SET status = ? WHERE id = ?")) {
            statement.setString(1, status.name());
            statement.setLong(2, recipeId);
            statement.executeUpdate();
        } catch (SQLException exception) { throw new DatabaseException("Could not update moderation status.", exception); }
    }

    private List<Recipe> query(String sql, List<Object> values) {
        List<Recipe> recipes = new ArrayList<>();
        try (Connection connection = database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) statement.setObject(i + 1, values.get(i));
            try (ResultSet rows = statement.executeQuery()) { while (rows.next()) recipes.add(map(rows)); }
            return recipes;
        } catch (SQLException exception) { throw new DatabaseException("Could not load recipes.", exception); }
    }

    private Recipe map(ResultSet row) throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setId(row.getLong("id"));
        recipe.setContributorId(row.getLong("contributor_id"));
        recipe.setContributorName(row.getString("contributor_name"));
        recipe.setTitle(row.getString("title"));
        recipe.setDescription(row.getString("description"));
        recipe.setIngredients(split(row.getString("ingredients")));
        recipe.setInstructions(split(row.getString("instructions")));
        recipe.setCategory(row.getString("category"));
        recipe.setCuisine(row.getString("cuisine"));
        recipe.setNation(row.getString("nation"));
        recipe.setTaste(row.getString("taste"));
        recipe.setPrepMinutes(row.getInt("prep_minutes"));
        recipe.setCookMinutes(row.getInt("cook_minutes"));
        recipe.changeStatus(RecipeStatus.valueOf(row.getString("status")));
        return recipe;
    }

    private List<String> split(String value) { return Arrays.stream(value.split("\\|")).filter(item -> !item.isBlank()).toList(); }
}
