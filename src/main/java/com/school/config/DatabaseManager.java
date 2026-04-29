package com.school.config;

import java.sql.*;
import java.util.logging.Logger;

/**
 * Gestionnaire de connexion à la base de données H2.
 * Implémente le pattern Singleton pour une connexion unique.
 */
public class DatabaseManager {

    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static final String DB_URL = "jdbc:h2:./school_management;DB_CLOSE_DELAY=-1";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, "sa", "");
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.severe("Erreur de connexion DB: " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
        return connection;
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(DB_URL, "sa", "");
            createTables();
            insertDefaultData();
            logger.info("Base de données initialisée avec succès.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver H2 introuvable", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la DB: " + e.getMessage(), e);
        }
    }

    private void createTables() throws SQLException {
        String[] statements = {
            // Table utilisateurs
            """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(100) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                full_name VARCHAR(200) NOT NULL,
                role VARCHAR(50) NOT NULL,
                active INT DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            // Table classes
            """
            CREATE TABLE IF NOT EXISTS classes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(50) UNIQUE NOT NULL,
                level VARCHAR(100) NOT NULL,
                teacher_name VARCHAR(200),
                capacity INT DEFAULT 30
            )
            """,
            // Table élèves
            """
            CREATE TABLE IF NOT EXISTS students (
                id INT AUTO_INCREMENT PRIMARY KEY,
                matricule VARCHAR(50) UNIQUE NOT NULL,
                first_name VARCHAR(100) NOT NULL,
                last_name VARCHAR(100) NOT NULL,
                birth_date VARCHAR(20),
                gender VARCHAR(20),
                class_name VARCHAR(50),
                parent_name VARCHAR(200),
                parent_phone VARCHAR(50),
                address VARCHAR(500),
                enrollment_date VARCHAR(20),
                active INT DEFAULT 1
            )
            """,
            // Table paiements
            """
            CREATE TABLE IF NOT EXISTS payments (
                id INT AUTO_INCREMENT PRIMARY KEY,
                student_id INT NOT NULL,
                amount DOUBLE NOT NULL,
                type VARCHAR(50) NOT NULL,
                status VARCHAR(30) DEFAULT 'PAYE',
                payment_date VARCHAR(20) NOT NULL,
                description VARCHAR(500),
                receipt_number VARCHAR(50) UNIQUE,
                recorded_by VARCHAR(200)
            )
            """
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : statements) {
                stmt.execute(sql);
            }
        }
    }

    private void insertDefaultData() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }

        String sql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            Object[][] defaultUsers = {
                {"admin",      hashPassword("admin123"),  "Directeur Principal", "ADMIN"},
                {"secretaire", hashPassword("secret123"), "Marie Dupont",        "SECRETAIRE"},
                {"tresorier",  hashPassword("tresor123"), "Jean Martin",         "TRESORIER"}
            };
            for (Object[] u : defaultUsers) {
                ps.setString(1, (String) u[0]);
                ps.setString(2, (String) u[1]);
                ps.setString(3, (String) u[2]);
                ps.setString(4, (String) u[3]);
                ps.executeUpdate();
            }
        }

        String classSql = "MERGE INTO classes (name, level, teacher_name, capacity) KEY(name) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(classSql)) {
            Object[][] classes = {
                {"CP1", "Primaire", "Prof. Rakoto",         35},
                {"CP2", "Primaire", "Prof. Rabe",           35},
                {"CE1", "Primaire", "Prof. Razafy",         30},
                {"CE2", "Primaire", "Prof. Rasoamahandry",  30},
                {"CM1", "Primaire", "Prof. Randria",        30},
                {"CM2", "Primaire", "Prof. Ratsimba",       30}
            };
            for (Object[] c : classes) {
                ps.setString(1, (String) c[0]);
                ps.setString(2, (String) c[1]);
                ps.setString(3, (String) c[2]);
                ps.setInt(4,    (int)    c[3]);
                ps.executeUpdate();
            }
        }
    }

    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur de hachage", e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            logger.warning("Erreur fermeture: " + e.getMessage());
        }
    }
}
