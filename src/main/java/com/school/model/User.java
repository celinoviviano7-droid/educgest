package com.school.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;

    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String passwordHash, String fullName, Role role) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return fullName + " (" + role.getLabel() + ")"; }
}
