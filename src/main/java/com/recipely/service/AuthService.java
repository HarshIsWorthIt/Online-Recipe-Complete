package com.recipely.service;

import com.recipely.exception.ValidationException;
import com.recipely.model.Role;
import com.recipely.model.User;
import com.recipely.model.UserFactory;
import com.recipely.repository.UserRepository;
import com.recipely.util.PasswordUtil;
import java.util.Optional;

public final class AuthService {
    private final UserRepository users;

    public AuthService(UserRepository users) { this.users = users; }

    public User register(String name, String email, String password, Role role) {
        if (name == null || name.trim().length() < 2) throw new ValidationException("Please enter your name.");
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new ValidationException("Please enter a valid email.");
        if (password == null || password.length() < 8) throw new ValidationException("Password must contain at least 8 characters.");
        if (users.findByEmail(email.trim()).isPresent()) throw new ValidationException("An account with this email already exists.");
        Role safeRole = role == Role.ADMIN ? Role.USER : role;
        return users.save(UserFactory.create(name.trim(), email.trim().toLowerCase(), PasswordUtil.hash(password), safeRole));
    }

    public Optional<User> login(String email, String password) {
        if (email == null || password == null) return Optional.empty();
        return users.findByEmail(email.trim()).filter(user -> user.getPasswordHash().equals(PasswordUtil.hash(password)));
    }
}
