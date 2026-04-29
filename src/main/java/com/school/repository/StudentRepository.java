package com.school.repository;

import com.school.config.DatabaseManager;
import com.school.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepository {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE active = 1 ORDER BY last_name, first_name";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) students.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des élèves", e);
        }
        return students;
    }

    public List<Student> findByClass(String className) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE class_name = ? AND active = 1 ORDER BY last_name";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, className);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) students.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de récupération par classe", e);
        }
        return students;
    }

    public List<Student> search(String query) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE active = 1 AND " +
                     "(lower(first_name) LIKE lower(?) OR lower(last_name) LIKE lower(?) OR matricule LIKE ?) " +
                     "ORDER BY last_name, first_name";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) students.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de recherche", e);
        }
        return students;
    }

    public Optional<Student> findById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de recherche par ID", e);
        }
        return Optional.empty();
    }

    public Optional<Student> findByMatricule(String matricule) {
        String sql = "SELECT * FROM students WHERE matricule = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, matricule);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de recherche par matricule", e);
        }
        return Optional.empty();
    }

    public void save(Student student) {
        String sql = """
            INSERT INTO students (matricule, first_name, last_name, birth_date, gender,
            class_name, parent_name, parent_phone, address, enrollment_date, active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getMatricule());
            ps.setString(2, student.getFirstName());
            ps.setString(3, student.getLastName());
            ps.setString(4, student.getBirthDate() != null ? student.getBirthDate().toString() : null);
            ps.setString(5, student.getGender());
            ps.setString(6, student.getClassName());
            ps.setString(7, student.getParentName());
            ps.setString(8, student.getParentPhone());
            ps.setString(9, student.getAddress());
            ps.setString(10, student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : LocalDate.now().toString());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) student.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de l'élève", e);
        }
    }

    public void update(Student student) {
        String sql = """
            UPDATE students SET first_name = ?, last_name = ?, birth_date = ?,
            gender = ?, class_name = ?, parent_name = ?, parent_phone = ?, address = ?
            WHERE id = ?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setString(3, student.getBirthDate() != null ? student.getBirthDate().toString() : null);
            ps.setString(4, student.getGender());
            ps.setString(5, student.getClassName());
            ps.setString(6, student.getParentName());
            ps.setString(7, student.getParentPhone());
            ps.setString(8, student.getAddress());
            ps.setInt(9, student.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'élève", e);
        }
    }

    public void deactivate(int id) {
        String sql = "UPDATE students SET active = 0 WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la désactivation", e);
        }
    }

    public int countAll() {
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students WHERE active = 1")) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public int countByClass(String className) {
        String sql = "SELECT COUNT(*) FROM students WHERE class_name = ? AND active = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, className);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public String generateMatricule() {
        int year = LocalDate.now().getYear() % 100;
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students")) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("EL%02d%04d", year, count);
            }
            return String.format("EL%02d%04d", year, (int)(Math.random() * 9999));
        } catch (SQLException e) {
            return String.format("EL%02d%04d", year, (int)(Math.random() * 9999));
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setMatricule(rs.getString("matricule"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        String bd = rs.getString("birth_date");
        if (bd != null && !bd.isEmpty()) s.setBirthDate(LocalDate.parse(bd));
        s.setGender(rs.getString("gender"));
        s.setClassName(rs.getString("class_name"));
        s.setParentName(rs.getString("parent_name"));
        s.setParentPhone(rs.getString("parent_phone"));
        s.setAddress(rs.getString("address"));
        String ed = rs.getString("enrollment_date");
        if (ed != null && !ed.isEmpty()) s.setEnrollmentDate(LocalDate.parse(ed));
        s.setActive(rs.getInt("active") == 1);
        return s;
    }
}
