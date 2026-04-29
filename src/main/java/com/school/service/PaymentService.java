package com.school.service;

import com.school.model.Payment;
import com.school.model.Student;
import com.school.repository.PaymentRepository;
import com.school.repository.StudentRepository;

import java.time.LocalDate;
import java.util.List;

public class PaymentService {

    private final PaymentRepository paymentRepository = new PaymentRepository();
    private final StudentRepository studentRepository = new StudentRepository();
    private final SessionManager session = SessionManager.getInstance();

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByStudent(int studentId) {
        return paymentRepository.findByStudent(studentId);
    }

    public List<Payment> getPaymentsByDateRange(LocalDate from, LocalDate to) {
        return paymentRepository.findByDateRange(from, to);
    }

    public void recordPayment(Payment payment) {
        validatePayment(payment);
        payment.setReceiptNumber(paymentRepository.generateReceiptNumber());
        payment.setRecordedBy(session.getCurrentUser().getFullName());
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        paymentRepository.save(payment);
    }

    public double getMonthlyTotal(int year, int month) {
        return paymentRepository.getTotalByMonth(year, month);
    }

    public double getYearlyTotal(int year) {
        return paymentRepository.getTotalByYear(year);
    }

    public double getCurrentMonthTotal() {
        LocalDate now = LocalDate.now();
        return paymentRepository.getTotalByMonth(now.getYear(), now.getMonthValue());
    }

    public double getCurrentYearTotal() {
        return paymentRepository.getTotalByYear(LocalDate.now().getYear());
    }

    private void validatePayment(Payment payment) {
        if (payment.getStudentId() <= 0) {
            throw new IllegalArgumentException("Élève invalide.");
        }
        if (payment.getAmount() <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }
        if (payment.getType() == null) {
            throw new IllegalArgumentException("Le type de paiement est obligatoire.");
        }
    }
}
