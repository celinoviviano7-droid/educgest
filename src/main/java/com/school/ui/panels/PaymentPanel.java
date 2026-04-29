package com.school.ui.panels;

import com.school.model.Payment;
import com.school.model.Payment.PaymentType;
import com.school.model.Student;
import com.school.service.PaymentService;
import com.school.service.StudentService;
import com.school.ui.components.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PaymentPanel extends JPanel {

    private final PaymentService paymentService = new PaymentService();
    private final StudentService studentService = new StudentService();

    private JTable table;
    private DefaultTableModel tableModel;
    private List<Payment> currentPayments;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat NUM_FMT = NumberFormat.getNumberInstance(Locale.FRANCE);

    public PaymentPanel() {
        NUM_FMT.setMaximumFractionDigits(0);
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        buildUI();
        loadPayments();
    }

    private void buildUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(AppTheme.BG_MAIN);

        JLabel title = AppTheme.createSectionTitle("💰  Gestion des Paiements");
        panel.add(title, BorderLayout.NORTH);

        // Résumé financier
        JPanel summaryPanel = createSummaryPanel();
        panel.add(summaryPanel, BorderLayout.CENTER);

        // Barre d'outils
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setBackground(AppTheme.BG_MAIN);

        JButton addBtn = AppTheme.createAccentButton("＋  Enregistrer un paiement");
        addBtn.addActionListener(e -> showPaymentDialog());

        JButton refreshBtn = AppTheme.createSecondaryButton("↻  Actualiser");
        refreshBtn.addActionListener(e -> loadPayments());

        toolbar.add(refreshBtn);
        toolbar.add(addBtn);

        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(AppTheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 0, 12, 0));

        panel.add(createSummaryCard("📅 Ce mois",
            NUM_FMT.format(paymentService.getCurrentMonthTotal()) + " Ar", AppTheme.PRIMARY_LIGHT));
        panel.add(createSummaryCard("📆 Cette année",
            NUM_FMT.format(paymentService.getCurrentYearTotal()) + " Ar", AppTheme.SUCCESS));
        panel.add(createSummaryCard("📋 Total enregistré",
            String.valueOf(paymentService.getAllPayments().size()) + " paiements", AppTheme.ACCENT));

        return panel;
    }

    private JPanel createSummaryCard(String label, String value, Color color) {
        JPanel card = AppTheme.createCard();
        card.setLayout(new GridLayout(2, 1, 0, 4));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 16));
        val.setForeground(color);

        card.add(lbl);
        card.add(val);
        return card;
    }

    private JPanel createTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_MAIN);

        String[] columns = {"Reçu N°", "Élève", "Matricule", "Classe", "Type", "Montant (Ar)", "Date", "Statut", "Enregistré par"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        AppTheme.styleTable(table);

        // Colorisation de la colonne statut
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    String s = value.toString();
                    if (s.equals("Payé")) setForeground(AppTheme.SUCCESS);
                    else if (s.equals("En attente")) setForeground(AppTheme.WARNING);
                    else setForeground(AppTheme.DANGER);
                }
                setFont(AppTheme.FONT_BOLD);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        // Montant aligné à droite
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(5).setCellRenderer(rightAlign);

        int[] widths = {90, 160, 100, 70, 110, 110, 100, 90, 130};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void loadPayments() {
        currentPayments = paymentService.getAllPayments();
        tableModel.setRowCount(0);
        for (Payment p : currentPayments) {
            tableModel.addRow(new Object[]{
                p.getReceiptNumber(),
                p.getStudentName(),
                p.getStudentMatricule(),
                p.getClassName(),
                p.getType().getLabel(),
                NUM_FMT.format(p.getAmount()),
                p.getPaymentDate() != null ? p.getPaymentDate().format(DATE_FMT) : "—",
                p.getStatus().getLabel(),
                p.getRecordedBy()
            });
        }

        // Alternance lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : AppTheme.TABLE_ALT_ROW);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    private void showPaymentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Enregistrer un paiement", true);
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // En-tête
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppTheme.ACCENT);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel titleLbl = new JLabel("💰  Nouveau Paiement");
        titleLbl.setFont(AppTheme.FONT_HEADING);
        titleLbl.setForeground(AppTheme.TEXT_PRIMARY);
        header.add(titleLbl);
        panel.add(header, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 6, 8, 6);

        // Recherche élève
        JComboBox<String> studentCombo = new JComboBox<>();
        List<Student> students = studentService.getAllStudents();
        studentCombo.addItem("— Sélectionner un élève —");
        for (Student s : students) {
            studentCombo.addItem(s.getMatricule() + " - " + s.getFullName() + " (" + s.getClassName() + ")");
        }
        studentCombo.setFont(AppTheme.FONT_LABEL);

        // Type
        JComboBox<PaymentType> typeCombo = new JComboBox<>(PaymentType.values());
        typeCombo.setFont(AppTheme.FONT_LABEL);

        // Montant
        JTextField amountField = AppTheme.createTextField("Montant en Ariary");

        // Date
        JTextField dateField = AppTheme.createTextField("JJ/MM/AAAA");
        dateField.setText(LocalDate.now().format(DATE_FMT));

        // Description
        JTextField descField = AppTheme.createTextField("Description (optionnel)");

        Object[][] rows = {
            {"Élève *", studentCombo},
            {"Type de paiement *", typeCombo},
            {"Montant (Ar) *", amountField},
            {"Date *", dateField},
            {"Description", descField}
        };

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.35;
            JLabel lbl = new JLabel((String) rows[i][0]);
            lbl.setFont(AppTheme.FONT_BOLD);
            lbl.setForeground(AppTheme.TEXT_PRIMARY);
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.65;
            form.add((Component) rows[i][1], gbc);
        }

        panel.add(form, BorderLayout.CENTER);

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnPanel.setBackground(AppTheme.BG_MAIN);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER_LIGHT));

        JButton cancelBtn = AppTheme.createSecondaryButton("Annuler");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = AppTheme.createAccentButton("💾  Enregistrer");
        saveBtn.addActionListener(e -> {
            try {
                int idx = studentCombo.getSelectedIndex();
                if (idx <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez sélectionner un élève.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Student s = students.get(idx - 1);

                String amtStr = amountField.getText().trim().replace(" ", "").replace(",", ".");
                double amount = Double.parseDouble(amtStr);

                LocalDate date = LocalDate.parse(dateField.getText().trim(), DATE_FMT);

                Payment payment = new Payment();
                payment.setStudentId(s.getId());
                payment.setAmount(amount);
                payment.setType((PaymentType) typeCombo.getSelectedItem());
                payment.setPaymentDate(date);
                payment.setDescription(descField.getText().trim());
                payment.setStatus(Payment.PaymentStatus.PAYE);

                paymentService.recordPayment(payment);
                loadPayments();
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                    "✅  Paiement enregistré avec succès!\nReçu N°: " + payment.getReceiptNumber(),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Montant invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "❌  " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
