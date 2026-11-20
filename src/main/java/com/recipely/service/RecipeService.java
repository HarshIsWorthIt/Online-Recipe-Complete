package com.recipely.service;

import com.recipely.exception.ValidationException;
import com.recipely.model.Recipe;
import com.recipely.model.RecipeStatus;
import com.recipely.model.User;
import com.recipely.repository.RecipeRepository;
import java.util.Arrays;
import java.util.List;

public final class RecipeService {
    private final RecipeRepository recipes;

    public RecipeService(RecipeRepository recipes) { this.recipes = recipes; }

    public List<Recipe> discover(RecipeFilter filter) {
        return recipes.findApproved(filter.search(), filter.nation(), filter.taste(), filter.maxMinutes());
    }

    public Recipe submit(User contributor, String title, String description, String ingredients, String instructions,
                         String category, String cuisine, String nation, String taste, int prepMinutes, int cookMinutes) {
        if (!contributor.canSubmitRecipes()) throw new ValidationException("Your role cannot submit recipes.");
        if (title == null || title.trim().length() < 3) throw new ValidationException("Recipe title needs at least 3 characters.");
        List<String> ingredientList = splitLines(ingredients);
        List<String> instructionList = splitLines(instructions);
        if (ingredientList.isEmpty() || instructionList.isEmpty()) throw new ValidationException("Add at least one ingredient and one instruction.");
        if (prepMinutes < 0 || cookMinutes < 0) throw new ValidationException("Cooking time cannot be negative.");

        Recipe recipe = new Recipe();
        recipe.setContributorId(contributor.getId());
        recipe.setTitle(title.trim());
        recipe.setDescription(clean(description, 500));
        recipe.setIngredients(ingredientList);
        recipe.setInstructions(instructionList);
        recipe.setCategory(clean(category, 40));
        recipe.setCuisine(clean(cuisine, 40));
        recipe.setNation(clean(nation, 40));
        recipe.setTaste(clean(taste, 40));
        recipe.setPrepMinutes(prepMinutes);
        recipe.setCookMinutes(cookMinutes);
        recipe.changeStatus(contributor.canModerateRecipes() ? RecipeStatus.APPROVED : RecipeStatus.PENDING);
        return recipes.save(recipe);
    }

    public void moderate(User administrator, long recipeId, RecipeStatus status) {
        if (!administrator.canModerateRecipes()) throw new ValidationException("Administrator access is required.");
        recipes.updateStatus(recipeId, status);
    }

    public List<Recipe> pendingRecipes() { return recipes.findByStatus(RecipeStatus.PENDING); }

    private List<String> splitLines(String raw) {
        return Arrays.stream((raw == null ? "" : raw).split("\\R"))
            .map(String::trim).filter(value -> !value.isEmpty()).limit(40).toList();
    }

    private String clean(String value, int limit) { return value == null ? "" : value.trim().substring(0, Math.min(value.trim().length(), limit)); }
}
