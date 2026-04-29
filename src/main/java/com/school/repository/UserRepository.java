package com.school.repository;

import com.school.config.DatabaseManager;
import com.school.model.Role;
import com.school.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public Optional<User> findByUsernameAndPassword(String username, String passwordHash) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ? AND active = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'authentification", e);
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY full_name";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs", e);
        }
        return users;
    }

    public void save(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, role, active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole().name());
            ps.setInt(5, user.isActive() ? 1 : 0);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) user.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de l'utilisateur", e);
        }
    }

    public void update(User user) {
        String sql = "UPDATE users SET full_name = ?, role = ?, active = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getRole().name());
            ps.setInt(3, user.isActive() ? 1 : 0);
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur", e);
        }
    }

    public void updatePassword(int userId, String newHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du mot de passe", e);
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de vérification du nom d'utilisateur", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setActive(rs.getInt("active") == 1);
        return user;
    }
}
