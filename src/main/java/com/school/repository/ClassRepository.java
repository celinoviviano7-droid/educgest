package com.school.repository;

import com.school.config.DatabaseManager;
import com.school.model.SchoolClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassRepository {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<SchoolClass> findAll() {
        List<SchoolClass> classes = new ArrayList<>();
        String sql = """
            SELECT c.*, COUNT(s.id) AS current_count
            FROM classes c
            LEFT JOIN students s ON c.name = s.class_name AND s.active = 1
            GROUP BY c.id ORDER BY c.level, c.name
        """;
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) classes.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de récupération des classes", e);
        }
        return classes;
    }

    public Optional<SchoolClass> findByName(String name) {
        String sql = "SELECT c.*, COUNT(s.id) AS current_count FROM classes c " +
                     "LEFT JOIN students s ON c.name = s.class_name AND s.active = 1 " +
                     "WHERE c.name = ? GROUP BY c.id";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de recherche de classe", e);
        }
        return Optional.empty();
    }

    public void save(SchoolClass schoolClass) {
        String sql = "INSERT INTO classes (name, level, teacher_name, capacity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, schoolClass.getName());
            ps.setString(2, schoolClass.getLevel());
            ps.setString(3, schoolClass.getTeacherName());
            ps.setInt(4, schoolClass.getCapacity());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) schoolClass.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la classe", e);
        }
    }

    public void update(SchoolClass schoolClass) {
        String sql = "UPDATE classes SET level = ?, teacher_name = ?, capacity = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, schoolClass.getLevel());
            ps.setString(2, schoolClass.getTeacherName());
            ps.setInt(3, schoolClass.getCapacity());
            ps.setInt(4, schoolClass.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la classe", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM classes WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la classe", e);
        }
    }

    public List<String> findAllNames() {
        List<String> names = new ArrayList<>();
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM classes ORDER BY name")) {
            while (rs.next()) names.add(rs.getString("name"));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de récupération des noms de classes", e);
        }
        return names;
    }

    private SchoolClass mapRow(ResultSet rs) throws SQLException {
        SchoolClass c = new SchoolClass();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setLevel(rs.getString("level"));
        c.setTeacherName(rs.getString("teacher_name"));
        c.setCapacity(rs.getInt("capacity"));
        c.setCurrentCount(rs.getInt("current_count"));
        return c;
    }
}
