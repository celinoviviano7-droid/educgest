package com.school.model;

import java.time.LocalDate;
import java.util.Objects;

public class Payment {
    private int id;
    private int studentId;
    private String studentName;
    private String studentMatricule;
    private String className;
    private double amount;
    private PaymentType type;
    private PaymentStatus status;
    private LocalDate paymentDate;
    private String description;
    private String receiptNumber;
    private String recordedBy;

    public enum PaymentType {
        INSCRIPTION("Inscription"),
        MENSUALITE("Mensualité"),
        CANTINE("Cantine"),
        TRANSPORT("Transport"),
        ACTIVITE("Activité"),
        AUTRE("Autre");

        private final String label;
        PaymentType(String label) { this.label = label; }
        public String getLabel() { return label; }

        @Override
        public String toString() { return label; }
    }

    public enum PaymentStatus {
        PAYE("Payé"),
        EN_ATTENTE("En attente"),
        ANNULE("Annulé");

        private final String label;
        PaymentStatus(String label) { this.label = label; }
        public String getLabel() { return label; }

        @Override
        public String toString() { return label; }
    }

    public Payment() {
        this.paymentDate = LocalDate.now();
        this.status = PaymentStatus.PAYE;
    }

    public Payment(int studentId, double amount, PaymentType type, String description) {
        this();
        this.studentId = studentId;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentMatricule() { return studentMatricule; }
    public void setStudentMatricule(String studentMatricule) { this.studentMatricule = studentMatricule; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public PaymentType getType() { return type; }
    public void setType(PaymentType type) { this.type = type; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment p = (Payment) o;
        return id == p.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return receiptNumber + " - " + studentName + " - " + amount + " Ar"; }
}
