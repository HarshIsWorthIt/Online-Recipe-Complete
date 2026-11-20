package com.recipely.model;

public final class Administrator extends User {
    public Administrator(String name, String email, String passwordHash) {
        super(name, email, passwordHash, Role.ADMIN);
    }
    @Override public boolean canSubmitRecipes() { return true; }
    @Override public boolean canModerateRecipes() { return true; }
}
