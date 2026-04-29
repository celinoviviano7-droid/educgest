package com.school.ui.panels;

import com.school.model.Payment;
import com.school.model.SchoolClass;
import com.school.service.ClassService;
import com.school.service.PaymentService;
import com.school.service.StudentService;
import com.school.ui.components.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReportsPanel extends JPanel {

    private final PaymentService paymentService = new PaymentService();
    private final StudentService studentService = new StudentService();
    private final ClassService classService = new ClassService();

    private static final NumberFormat NUM_FMT = NumberFormat.getNumberInstance(Locale.FRANCE);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReportsPanel() {
        NUM_FMT.setMaximumFractionDigits(0);
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        buildUI();
    }

    private void buildUI() {
        JLabel title = AppTheme.createSectionTitle("📊  Rapports & Statistiques");
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BOLD);
        tabs.setBackground(AppTheme.BG_MAIN);
        tabs.addTab("💰  Rapport financier", createFinancialTab());
        tabs.addTab("👨‍🎓  Rapport élèves", createStudentTab());
        tabs.addTab("📅  Par période", createPeriodTab());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createFinancialTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(AppTheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Résumé annuel
        int year = LocalDate.now().getYear();
        JPanel annualPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        annualPanel.setBackground(AppTheme.BG_MAIN);

        String[] months = {"Jan","Fév","Mar","Avr","Mai","Jun","Jul","Aoû","Sep","Oct","Nov","Déc"};
        double yearTotal = 0;
        double maxMonth = 0;
        int bestMonth = 0;

        Map<Integer, Double> monthlyData = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++) {
            double v = paymentService.getMonthlyTotal(year, m);
            monthlyData.put(m, v);
            yearTotal += v;
            if (v > maxMonth) { maxMonth = v; bestMonth = m; }
        }

        // Cards résumé
        JPanel summaryRow = new JPanel(new GridLayout(1, 3, 12, 0));
        summaryRow.setBackground(AppTheme.BG_MAIN);
        summaryRow.add(createInfoCard("💰 Total " + year, NUM_FMT.format(yearTotal) + " Ar", AppTheme.PRIMARY));
        summaryRow.add(createInfoCard("📈 Meilleur mois", bestMonth > 0 ? months[bestMonth-1] + " " + year : "N/A", AppTheme.SUCCESS));
        summaryRow.add(createInfoCard("📅 Mois en cours", NUM_FMT.format(paymentService.getCurrentMonthTotal()) + " Ar", AppTheme.ACCENT));
        panel.add(summaryRow, BorderLayout.NORTH);

        // Tableau mensuel
        String[] cols = {"Mois", "Recettes (Ar)", "Variation"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        double prev = 0;
        for (Map.Entry<Integer, Double> entry : monthlyData.entrySet()) {
            int m = entry.getKey();
            double v = entry.getValue();
            String variation = prev == 0 ? "—" : (v >= prev ? "▲ " : "▼ ") +
                NUM_FMT.format(Math.abs(v - prev)) + " Ar";
            model.addRow(new Object[]{months[m - 1] + " " + year, NUM_FMT.format(v) + " Ar", variation});
            prev = v;
        }

        // Totaux par type
        List<Payment> allPayments = paymentService.getAllPayments();
        Map<String, Double> byType = allPayments.stream()
            .collect(Collectors.groupingBy(p -> p.getType().getLabel(), Collectors.summingDouble(Payment::getAmount)));

        String[] typeCols = {"Type de paiement", "Total (Ar)", "Nombre"};
        DefaultTableModel typeModel = new DefaultTableModel(typeCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        byType.forEach((type, total) -> {
            long count = allPayments.stream().filter(p -> p.getType().getLabel().equals(type)).count();
            typeModel.addRow(new Object[]{type, NUM_FMT.format(total) + " Ar", count});
        });

        JTable monthTable = new JTable(model);
        AppTheme.styleTable(monthTable);
        JTable typeTable = new JTable(typeModel);
        AppTheme.styleTable(typeTable);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            wrapInScroll("Résumé mensuel", monthTable),
            wrapInScroll("Par type de paiement", typeTable));
        split.setDividerLocation(0.5);
        split.setResizeWeight(0.5);
        split.setBorder(null);
        split.setBackground(AppTheme.BG_MAIN);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(AppTheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        List<SchoolClass> classes = classService.getAllClasses();
        int totalStudents = studentService.getTotalStudentsCount();

        JPanel topCards = new JPanel(new GridLayout(1, 3, 12, 0));
        topCards.setBackground(AppTheme.BG_MAIN);
        topCards.add(createInfoCard("👨‍🎓 Total élèves", String.valueOf(totalStudents), AppTheme.PRIMARY));
        topCards.add(createInfoCard("🏫 Nombre de classes", String.valueOf(classes.size()), AppTheme.SUCCESS));
        int totalCapacity = classes.stream().mapToInt(SchoolClass::getCapacity).sum();
        int pct = totalCapacity > 0 ? totalStudents * 100 / totalCapacity : 0;
        topCards.add(createInfoCard("📊 Taux d'occupation", pct + "%", AppTheme.ACCENT));
        panel.add(topCards, BorderLayout.NORTH);

        // Tableau par classe
        String[] cols = {"Classe", "Niveau", "Enseignant", "Élèves", "Capacité", "Taux"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (SchoolClass c : classes) {
            int p = c.getCapacity() > 0 ? (int)(c.getCurrentCount() * 100.0 / c.getCapacity()) : 0;
            model.addRow(new Object[]{c.getName(), c.getLevel(),
                c.getTeacherName() != null ? c.getTeacherName() : "—",
                c.getCurrentCount(), c.getCapacity(), p + "%"});
        }

        JTable table = new JTable(model);
        AppTheme.styleTable(table);
        panel.add(wrapInScroll("Répartition par classe", table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPeriodTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(AppTheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Sélecteur de période
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBackground(AppTheme.BG_CARD);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT),
            new EmptyBorder(4, 8, 4, 8)));

        JLabel fromLbl = new JLabel("Du:");
        fromLbl.setFont(AppTheme.FONT_BOLD);
        JTextField fromField = AppTheme.createTextField("JJ/MM/AAAA");
        fromField.setPreferredSize(new Dimension(120, 34));
        fromField.setText(LocalDate.now().withDayOfMonth(1).format(DATE_FMT));

        JLabel toLbl = new JLabel("Au:");
        toLbl.setFont(AppTheme.FONT_BOLD);
        JTextField toField = AppTheme.createTextField("JJ/MM/AAAA");
        toField.setPreferredSize(new Dimension(120, 34));
        toField.setText(LocalDate.now().format(DATE_FMT));

        String[] cols = {"Reçu N°", "Élève", "Classe", "Type", "Montant (Ar)", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        AppTheme.styleTable(table);

        JLabel totalLbl = new JLabel("Total: 0 Ar");
        totalLbl.setFont(AppTheme.FONT_BOLD);
        totalLbl.setForeground(AppTheme.PRIMARY);

        JButton searchBtn = AppTheme.createPrimaryButton("🔍  Filtrer");
        searchBtn.addActionListener(e -> {
            try {
                LocalDate from = LocalDate.parse(fromField.getText().trim(), DATE_FMT);
                LocalDate to = LocalDate.parse(toField.getText().trim(), DATE_FMT);
                List<Payment> results = paymentService.getPaymentsByDateRange(from, to);
                model.setRowCount(0);
                double total = 0;
                for (Payment p : results) {
                    model.addRow(new Object[]{
                        p.getReceiptNumber(), p.getStudentName(), p.getClassName(),
                        p.getType().getLabel(), NUM_FMT.format(p.getAmount()) + " Ar",
                        p.getPaymentDate() != null ? p.getPaymentDate().format(DATE_FMT) : "—"
                    });
                    total += p.getAmount();
                }
                totalLbl.setText("Total: " + NUM_FMT.format(total) + " Ar  |  " + results.size() + " paiement(s)");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Dates invalides. Format: JJ/MM/AAAA", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        filterPanel.add(fromLbl); filterPanel.add(fromField);
        filterPanel.add(Box.createHorizontalStrut(8));
        filterPanel.add(toLbl); filterPanel.add(toField);
        filterPanel.add(searchBtn);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(totalLbl);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(wrapInScroll("Paiements de la période", table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInfoCard(String label, String value, Color color) {
        JPanel card = AppTheme.createCard();
        card.setLayout(new GridLayout(2, 1, 0, 6));
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        val.setForeground(color);
        card.add(lbl); card.add(val);
        return card;
    }

    private JPanel wrapInScroll(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(AppTheme.BG_MAIN);
        JLabel lbl = new JLabel(title);
        lbl.setFont(AppTheme.FONT_BOLD);
        lbl.setForeground(AppTheme.TEXT_PRIMARY);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT));
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
}
