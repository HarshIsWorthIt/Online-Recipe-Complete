package com.recipely.model;

import java.util.List;

public final class Recipe extends BaseEntity implements Moderatable {
    private long contributorId;
    private String contributorName;
    private String title;
    private String description;
    private List<String> ingredients;
    private List<String> instructions;
    private String category;
    private String cuisine;
    private String nation;
    private String taste;
    private int prepMinutes;
    private int cookMinutes;
    private RecipeStatus status = RecipeStatus.PENDING;

    public long getContributorId() { return contributorId; }
    public void setContributorId(long contributorId) { this.contributorId = contributorId; }
    public String getContributorName() { return contributorName; }
    public void setContributorName(String contributorName) { this.contributorName = contributorName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public String getNation() { return nation; }
    public void setNation(String nation) { this.nation = nation; }
    public String getTaste() { return taste; }
    public void setTaste(String taste) { this.taste = taste; }
    public int getPrepMinutes() { return prepMinutes; }
    public void setPrepMinutes(int prepMinutes) { this.prepMinutes = prepMinutes; }
    public int getCookMinutes() { return cookMinutes; }
    public void setCookMinutes(int cookMinutes) { this.cookMinutes = cookMinutes; }
    @Override public RecipeStatus getStatus() { return status; }
    @Override public void changeStatus(RecipeStatus status) { this.status = status; }
    public int getTotalMinutes() { return prepMinutes + cookMinutes; }
}
