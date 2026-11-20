package com.recipely.model;

/** Contract used by content that moves through the moderation workflow. */
public interface Moderatable {
    RecipeStatus getStatus();
    void changeStatus(RecipeStatus status);
}
