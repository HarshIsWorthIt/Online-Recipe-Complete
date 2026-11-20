package com.recipely.model;

public final class Contributor extends User {
    public Contributor(String name, String email, String passwordHash) {
        super(name, email, passwordHash, Role.CONTRIBUTOR);
    }
    @Override public boolean canSubmitRecipes() { return true; }
    @Override public boolean canModerateRecipes() { return false; }
}
