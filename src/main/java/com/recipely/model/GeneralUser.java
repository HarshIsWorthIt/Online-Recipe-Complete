package com.recipely.model;

public final class GeneralUser extends User {
    public GeneralUser(String name, String email, String passwordHash) {
        super(name, email, passwordHash, Role.USER);
    }
    @Override public boolean canSubmitRecipes() { return true; }
    @Override public boolean canModerateRecipes() { return false; }
}
