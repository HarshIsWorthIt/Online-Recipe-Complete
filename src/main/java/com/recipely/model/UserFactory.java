package com.recipely.model;

/** Creates the correct polymorphic user subtype from the persisted role. */
public final class UserFactory {
    private UserFactory() { }

    public static User create(String name, String email, String hash, Role role) {
        return switch (role) {
            case ADMIN -> new Administrator(name, email, hash);
            case CONTRIBUTOR -> new Contributor(name, email, hash);
            case EXPLORER -> new Explorer(name, email, hash);
            case USER -> new GeneralUser(name, email, hash);
        };
    }
}
