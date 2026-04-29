package com.school.repository;

import com.school.config.DatabaseManager;
import com.school.model.Payment;
import com.school.model.Payment.PaymentType;
import com.school.model.Payment.PaymentStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT p.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.matricule AS student_matricule, s.class_name
            FROM payments p
            JOIN students s ON p.student_id = s.id
            ORDER BY p.payment_date DESC
        """;
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) payments.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de récupération des paiements", e);
        }
        return payments;
    }

    public List<Payment> findByStudent(int studentId) {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT p.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.matricule AS student_matricule, s.class_name
            FROM payments p
            JOIN students s ON p.student_id = s.id
            WHERE p.student_id = ?
            ORDER BY p.payment_date DESC
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) payments.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de récupération des paiements de l'élève", e);
        }
        return payments;
    }

    public List<Payment> findByDateRange(LocalDate from, LocalDate to) {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT p.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.matricule AS student_matricule, s.class_name
            FROM payments p
            JOIN students s ON p.student_id = s.id
            WHERE p.payment_date BETWEEN ? AND ?
            ORDER BY p.payment_date DESC
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) payments.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de récupération par période", e);
        }
        return payments;
    }

    public void save(Payment payment) {
        String sql = """
            INSERT INTO payments (student_id, amount, type, status, payment_date,
            description, receipt_number, recorded_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getStudentId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getType().name());
            ps.setString(4, payment.getStatus().name());
            ps.setString(5, payment.getPaymentDate().toString());
            ps.setString(6, payment.getDescription());
            ps.setString(7, payment.getReceiptNumber());
            ps.setString(8, payment.getRecordedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) payment.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du paiement", e);
        }
    }

    public double getTotalByMonth(int year, int month) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE " +
                     "YEAR(PARSEDATETIME(payment_date, 'yyyy-MM-dd')) = ? AND " +
                     "MONTH(PARSEDATETIME(payment_date, 'yyyy-MM-dd')) = ? AND status = 'PAYE'";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
            return 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public double getTotalByYear(int year) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE " +
                     "YEAR(PARSEDATETIME(payment_date, 'yyyy-MM-dd')) = ? AND status = 'PAYE'";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
            return 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public String generateReceiptNumber() {
        int year = LocalDate.now().getYear() % 100;
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM payments")) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("REC%02d%05d", year, count);
            }
            return String.format("REC%02d%05d", year, (int)(Math.random() * 99999));
        } catch (SQLException e) {
            return String.format("REC%02d%05d", year, (int)(Math.random() * 99999));
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id"));
        p.setStudentId(rs.getInt("student_id"));
        p.setStudentName(rs.getString("student_name"));
        p.setStudentMatricule(rs.getString("student_matricule"));
        p.setClassName(rs.getString("class_name"));
        p.setAmount(rs.getDouble("amount"));
        p.setType(PaymentType.valueOf(rs.getString("type")));
        p.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        String date = rs.getString("payment_date");
        if (date != null) p.setPaymentDate(LocalDate.parse(date));
        p.setDescription(rs.getString("description"));
        p.setReceiptNumber(rs.getString("receipt_number"));
        p.setRecordedBy(rs.getString("recorded_by"));
        return p;
    }
}
