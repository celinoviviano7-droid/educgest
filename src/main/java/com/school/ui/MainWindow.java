package com.school.ui;

import com.school.model.Role;
import com.school.model.User;
import com.school.service.AuthService;
import com.school.service.SessionManager;
import com.school.ui.components.AppTheme;
import com.school.ui.components.Sidebar;
import com.school.ui.panels.*;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale de l'application.
 * Gère la navigation entre les différents panneaux selon le rôle.
 */
public class MainWindow extends JFrame {

    private final AuthService authService = new AuthService();
    private final User currentUser;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Sidebar sidebar;

    public MainWindow(User currentUser) {
        this.currentUser = currentUser;
        setTitle("EduGest - Système de Gestion Scolaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        buildUI();
        setVisible(true);

        // Afficher le tableau de bord par défaut
        showPanel("DASHBOARD");
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppTheme.BG_MAIN);

        // Sidebar
        sidebar = new Sidebar(currentUser, this::handleNavigation);
        add(sidebar, BorderLayout.WEST);

        // Zone de contenu principal
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppTheme.BG_MAIN);

        // Ajouter les panneaux selon le rôle
        contentPanel.add(new DashboardPanel(currentUser), "DASHBOARD");

        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.SECRETAIRE) {
            contentPanel.add(new StudentPanel(), "STUDENTS");
            contentPanel.add(new ClassPanel(), "CLASSES");
        }

        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.TRESORIER) {
            contentPanel.add(new PaymentPanel(), "PAYMENTS");
            contentPanel.add(new ReportsPanel(), "REPORTS");
        }

        if (currentUser.getRole() == Role.ADMIN) {
            contentPanel.add(new UsersPanel(), "USERS");
            contentPanel.add(new SettingsPanel(currentUser), "SETTINGS");
        }

        // Panneau "accès refusé" pour les accès non autorisés
        contentPanel.add(createAccessDeniedPanel(), "ACCESS_DENIED");

        add(contentPanel, BorderLayout.CENTER);
    }

    private void handleNavigation(String panelName) {
        if ("LOGOUT".equals(panelName)) {
            confirmLogout();
            return;
        }

        // Vérification des accès
        if (!hasAccess(panelName)) {
            showPanel("ACCESS_DENIED");
            return;
        }

        showPanel(panelName);
    }

    private boolean hasAccess(String panelName) {
        Role role = currentUser.getRole();
        return switch (panelName) {
            case "DASHBOARD" -> true;
            case "STUDENTS", "CLASSES" -> role == Role.ADMIN || role == Role.SECRETAIRE;
            case "PAYMENTS", "REPORTS" -> role == Role.ADMIN || role == Role.TRESORIER;
            case "USERS", "SETTINGS" -> role == Role.ADMIN;
            default -> false;
        };
    }

    private void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        sidebar.selectPanel(panelName);
    }

    private void confirmLogout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir vous déconnecter ?",
            "Déconnexion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginWindow());
        }
    }

    private JPanel createAccessDeniedPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.BG_MAIN);

        JPanel card = AppTheme.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel icon = new JLabel("🔒");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Accès refusé");
        title.setFont(AppTheme.FONT_HEADING);
        title.setForeground(AppTheme.DANGER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Vous n'avez pas les permissions pour accéder à cette section.");
        msg.setFont(AppTheme.FONT_LABEL);
        msg.setForeground(AppTheme.TEXT_SECONDARY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(16));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(msg);

        panel.add(card);
        return panel;
    }
}
