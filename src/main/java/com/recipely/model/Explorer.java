package com.recipely.model;

public final class Explorer extends User {
    public Explorer(String name, String email, String passwordHash) {
        super(name, email, passwordHash, Role.EXPLORER);
    }
    @Override public boolean canSubmitRecipes() { return false; }
    @Override public boolean canModerateRecipes() { return false; }
}
