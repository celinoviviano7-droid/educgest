package com.school.service;

import com.school.model.User;

/**
 * Gestionnaire de session utilisateur (Singleton).
 * Stocke l'utilisateur connecté pour toute la durée de la session.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
