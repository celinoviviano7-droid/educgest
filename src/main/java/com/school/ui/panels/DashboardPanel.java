package com.school.ui.panels;

import com.school.model.User;
import com.school.model.SchoolClass;
import com.school.service.ClassService;
import com.school.service.PaymentService;
import com.school.service.StudentService;
import com.school.ui.components.AppTheme;
import com.school.ui.components.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DashboardPanel extends JPanel {

    private final User currentUser;
    private final StudentService studentService = new StudentService();
    private final PaymentService paymentService = new PaymentService();
    private final ClassService classService = new ClassService();

    private StatCard studentCard;
    private StatCard classCard;
    private StatCard monthCard;
    private StatCard yearCard;

    public DashboardPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        buildUI();
        refreshData();
    }

    private void buildUI() {
        // En-tête
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Contenu principal
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_MAIN);

        content.add(Box.createVerticalStrut(20));
        content.add(createStatsSection());
        content.add(Box.createVerticalStrut(24));
        content.add(createClassesSection());

        add(new JScrollPane(content) {{
            setBorder(null);
            setBackground(AppTheme.BG_MAIN);
            getViewport().setBackground(AppTheme.BG_MAIN);
        }}, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_MAIN);

        String greeting = getGreeting();
        JLabel greetLabel = new JLabel(greeting + ", " + currentUser.getFullName() + " 👋");
        greetLabel.setFont(AppTheme.FONT_TITLE);
        greetLabel.setForeground(AppTheme.TEXT_PRIMARY);

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH));
        date = Character.toUpperCase(date.charAt(0)) + date.substring(1);
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(AppTheme.FONT_LABEL);
        dateLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setBackground(AppTheme.BG_MAIN);
        textPanel.add(greetLabel);
        textPanel.add(dateLabel);

        JButton refreshBtn = AppTheme.createSecondaryButton("↻  Actualiser");
        refreshBtn.addActionListener(e -> refreshData());

        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(AppTheme.BG_MAIN);

        JLabel sectionTitle = AppTheme.createSectionTitle("📊  Statistiques générales");
        sectionTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        section.add(sectionTitle, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 4, 14, 0));
        grid.setBackground(AppTheme.BG_MAIN);

        studentCard = new StatCard("👨‍🎓", "Total Élèves", "...", AppTheme.PRIMARY_LIGHT);
        classCard   = new StatCard("🏫", "Classes actives", "...", AppTheme.SUCCESS);
        monthCard   = new StatCard("📅", "Recettes ce mois", "...", AppTheme.ACCENT);
        yearCard    = new StatCard("💰", "Recettes cette année", "...", AppTheme.DANGER);

        grid.add(studentCard);
        grid.add(classCard);
        grid.add(monthCard);
        grid.add(yearCard);

        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JPanel createClassesSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(AppTheme.BG_MAIN);

        JLabel sectionTitle = AppTheme.createSectionTitle("🏫  Vue d'ensemble des classes");
        sectionTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        section.add(sectionTitle, BorderLayout.NORTH);

        List<SchoolClass> classes = classService.getAllClasses();

        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setBackground(AppTheme.BG_MAIN);

        for (SchoolClass cls : classes) {
            grid.add(createClassCard(cls));
        }

        if (classes.isEmpty()) {
            JLabel empty = new JLabel("Aucune classe configurée");
            empty.setForeground(AppTheme.TEXT_SECONDARY);
            empty.setFont(AppTheme.FONT_LABEL);
            grid.add(empty);
        }

        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JPanel createClassCard(SchoolClass cls) {
        JPanel card = AppTheme.createCard();
        card.setLayout(new BorderLayout(10, 8));

        JLabel nameLabel = new JLabel(cls.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(AppTheme.PRIMARY);

        JLabel levelLabel = new JLabel(cls.getLevel());
        levelLabel.setFont(AppTheme.FONT_SMALL);
        levelLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel namePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        namePanel.setOpaque(false);
        namePanel.add(nameLabel);
        namePanel.add(levelLabel);

        // Barre de remplissage
        int pct = cls.getCapacity() > 0 ? (int) (cls.getCurrentCount() * 100.0 / cls.getCapacity()) : 0;
        Color barColor = pct >= 90 ? AppTheme.DANGER : pct >= 70 ? AppTheme.WARNING : AppTheme.SUCCESS;

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(pct);
        bar.setStringPainted(false);
        bar.setBackground(AppTheme.BORDER_LIGHT);
        bar.setForeground(barColor);
        bar.setBorder(null);
        bar.setPreferredSize(new Dimension(0, 6));

        JLabel countLabel = new JLabel(cls.getCurrentCount() + " / " + cls.getCapacity() + " élèves");
        countLabel.setFont(AppTheme.FONT_SMALL);
        countLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel teacherLabel = new JLabel("👤  " + (cls.getTeacherName() != null ? cls.getTeacherName() : "—"));
        teacherLabel.setFont(AppTheme.FONT_SMALL);
        teacherLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        bottomPanel.setOpaque(false);
        bottomPanel.add(bar);
        bottomPanel.add(countLabel);
        bottomPanel.add(teacherLabel);

        card.add(namePanel, BorderLayout.NORTH);
        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            int totalStudents = studentService.getTotalStudentsCount();
            int totalClasses = classService.getAllClasses().size();
            double monthTotal = paymentService.getCurrentMonthTotal();
            double yearTotal = paymentService.getCurrentYearTotal();

            NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRANCE);
            nf.setMaximumFractionDigits(0);

            studentCard.setValue(String.valueOf(totalStudents));
            classCard.setValue(String.valueOf(totalClasses));
            monthCard.setValue(nf.format(monthTotal) + " Ar");
            yearCard.setValue(nf.format(yearTotal) + " Ar");

            repaint();
        });
    }

    private String getGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Bonjour";
        if (hour < 18) return "Bon après-midi";
        return "Bonsoir";
    }
}
