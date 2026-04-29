package com.school.model;

public enum Role {
    ADMIN("Directeur"),
    SECRETAIRE("Secrétaire"),
    TRESORIER("Trésorier");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
