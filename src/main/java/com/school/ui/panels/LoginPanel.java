package com.school.ui.panels;

import com.school.service.AuthService;
import com.school.ui.components.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginPanel extends JPanel {

    public interface LoginListener {
        void onLoginSuccess();
    }

    private final AuthService authService = new AuthService();
    private final LoginListener loginListener;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginPanel(LoginListener loginListener) {
        this.loginListener = loginListener;
        setLayout(new BorderLayout());
        setBackground(AppTheme.PRIMARY);
        buildUI();
    }

    private void buildUI() {
        // Panneau gauche décoratif
        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        // Panneau de connexion
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Dégradé
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY_DARK,
                    0, getHeight(), AppTheme.PRIMARY_LIGHT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Cercles décoratifs
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-60, -60, 300, 300);
                g2.fillOval(getWidth() - 100, getHeight() - 150, 250, 250);
                g2.fillOval(50, getHeight() / 2 - 80, 160, 160);

                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(380, 0));
        panel.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel emojiLabel = new JLabel("🏫");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("EduGest");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Système de Gestion Scolaire");
        subtitleLabel.setFont(AppTheme.FONT_LABEL);
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(Box.createVerticalStrut(20));
        content.add(emojiLabel);
        content.add(Box.createVerticalStrut(16));
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(subtitleLabel);
        content.add(Box.createVerticalStrut(40));

        // Features list
        String[] features = {
            "✅  Gestion des élèves et classes",
            "✅  Suivi des paiements",
            "✅  Rapports et statistiques",
            "✅  Accès sécurisé par rôle"
        };
        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setFont(AppTheme.FONT_LABEL);
            fl.setForeground(new Color(255, 255, 255, 200));
            fl.setAlignmentX(Component.LEFT_ALIGNMENT);
            fl.setBorder(new EmptyBorder(4, 0, 4, 0));
            content.add(fl);
        }

        panel.add(content);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(AppTheme.BG_MAIN);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 48, 40, 48));

        // Titre
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Entrez vos identifiants pour accéder");
        subtitleLabel.setFont(AppTheme.FONT_LABEL);
        subtitleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Champ nom d'utilisateur
        JLabel userLabel = new JLabel("Nom d'utilisateur");
        userLabel.setFont(AppTheme.FONT_BOLD);
        userLabel.setForeground(AppTheme.TEXT_PRIMARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = AppTheme.createTextField("Entrez votre identifiant");
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Champ mot de passe
        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(AppTheme.FONT_BOLD);
        passLabel.setForeground(AppTheme.TEXT_PRIMARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(AppTheme.FONT_LABEL);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        // Erreur
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(AppTheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bouton connexion
        JButton loginBtn = AppTheme.createPrimaryButton("  Se connecter  ");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.addActionListener(e -> attemptLogin());

        // Enter pour se connecter
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) attemptLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        // Comptes de démonstration
        JPanel demoPanel = createDemoPanel();
        demoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(32));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(18));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(28));
        card.add(demoPanel);

        // Arrondir la carte
        card.setPreferredSize(new Dimension(420, 560));

        outer.add(card);
        return outer;
    }

    private JPanel createDemoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0xF0F4F8));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT),
            new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel title = new JLabel("🔑  Comptes de démonstration");
        title.setFont(AppTheme.FONT_BOLD);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));

        String[][] accounts = {
            {"👨‍💼 Directeur", "admin", "admin123"},
            {"📝 Secrétaire", "secretaire", "secret123"},
            {"💰 Trésorier", "tresorier", "tresor123"}
        };

        for (String[] acc : accounts) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            row.setBackground(new Color(0xF0F4F8));
            JLabel roleLabel = new JLabel(acc[0] + ":");
            roleLabel.setFont(AppTheme.FONT_SMALL);
            roleLabel.setForeground(AppTheme.TEXT_SECONDARY);
            JButton fillBtn = new JButton(acc[1] + " / " + acc[2]);
            fillBtn.setFont(AppTheme.FONT_SMALL);
            fillBtn.setForeground(AppTheme.PRIMARY_LIGHT);
            fillBtn.setBorderPainted(false);
            fillBtn.setContentAreaFilled(false);
            fillBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final String u = acc[1], p = acc[2];
            fillBtn.addActionListener(e -> {
                usernameField.setText(u);
                passwordField.setText(p);
            });
            row.add(roleLabel);
            row.add(fillBtn);
            panel.add(row);
        }

        return panel;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("⚠  Veuillez remplir tous les champs.");
            return;
        }

        boolean success = authService.login(username, password);
        if (success) {
            loginListener.onLoginSuccess();
        } else {
            errorLabel.setText("✕  Identifiant ou mot de passe incorrect.");
            passwordField.setText("");
        }
    }
}
