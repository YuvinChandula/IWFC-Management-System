package com.iwfc.model;

/**
 * Entity: User
 * 
 * Purpose:
 * Encapsulates the state and behavior of any actor in the system.
 * 
 * OOP Principles Demonstrated:
 * - Encapsulation: Fields are private and final, accessible only via public getters.
 */
public class User {
    private final String id;
    private final String name;
    private final String email;
    private final UserRole role;

    /**
     * Parameterized Constructor to initialize an immutable User instance.
     */
    public User(String id, String name, String email, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // --- Getter Methods (Encapsulation) ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Role: %s", id, name, email, role);
    }
}