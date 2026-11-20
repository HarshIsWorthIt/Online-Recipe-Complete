package com.recipely.model;

/**
 * Abstract user base class. Child classes implement role-specific behaviour,
 * demonstrating inheritance and polymorphism in the domain model.
 */
public abstract class User extends BaseEntity {
    private String name;
    private String email;
    private String passwordHash;
    private Role role;

    protected User(String name, String email, String passwordHash, Role role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public abstract boolean canSubmitRecipes();
    public abstract boolean canModerateRecipes();

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
