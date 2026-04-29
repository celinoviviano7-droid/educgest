package com.school.service;

import com.school.config.DatabaseManager;
import com.school.model.User;
import com.school.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();
    private final SessionManager session = SessionManager.getInstance();

    public boolean login(String username, String password) {
        String hash = DatabaseManager.hashPassword(password);
        Optional<User> user = userRepository.findByUsernameAndPassword(username, hash);
        if (user.isPresent()) {
            session.setCurrentUser(user.get());
            return true;
        }
        return false;
    }

    public void logout() {
        session.logout();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createUser(User user) {
        if (userRepository.usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà.");
        }
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.update(user);
    }

    public void changePassword(int userId, String oldPassword, String newPassword) {
        User current = session.getCurrentUser();
        String oldHash = DatabaseManager.hashPassword(oldPassword);
        if (!current.getPasswordHash().equals(oldHash)) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect.");
        }
        String newHash = DatabaseManager.hashPassword(newPassword);
        userRepository.updatePassword(userId, newHash);
        current.setPasswordHash(newHash);
    }

    public void resetPassword(int userId, String newPassword) {
        String hash = DatabaseManager.hashPassword(newPassword);
        userRepository.updatePassword(userId, hash);
    }
}
