package com.school.ui.panels;

import com.school.model.User;
import com.school.service.AuthService;
import com.school.ui.components.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final AuthService authService = new AuthService();
    private final User currentUser;

    public SettingsPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        buildUI();
    }

    private void buildUI() {
        JLabel title = AppTheme.createSectionTitle("⚙️  Paramètres");
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_MAIN);

        content.add(createProfileCard());
        content.add(Box.createVerticalStrut(20));
        content.add(createPasswordCard());
        content.add(Box.createVerticalStrut(20));
        content.add(createAboutCard());

        add(new JScrollPane(content) {{
            setBorder(null);
            getViewport().setBackground(AppTheme.BG_MAIN);
        }}, BorderLayout.CENTER);
    }

    private JPanel createProfileCard() {
        JPanel card = AppTheme.createCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setMaximumSize(new Dimension(600, 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cardTitle = new JLabel("👤  Mon profil");
        cardTitle.setFont(AppTheme.FONT_HEADING);
        cardTitle.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(cardTitle, BorderLayout.NORTH);

        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 10, 8));
        infoGrid.setOpaque(false);

        addInfoRow(infoGrid, "Nom complet:", currentUser.getFullName());
        addInfoRow(infoGrid, "Nom d'utilisateur:", currentUser.getUsername());
        addInfoRow(infoGrid, "Rôle:", currentUser.getRole().getLabel());

        card.add(infoGrid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPasswordCard() {
        JPanel card = AppTheme.createCard();
        card.setLayout(new BorderLayout(0, 14));
        card.setMaximumSize(new Dimension(600, 220));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cardTitle = new JLabel("🔑  Changer le mot de passe");
        cardTitle.setFont(AppTheme.FONT_HEADING);
        cardTitle.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(cardTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JPasswordField oldPwd = new JPasswordField();
        JPasswordField newPwd = new JPasswordField();
        JPasswordField confirmPwd = new JPasswordField();

        stylePasswordField(oldPwd);
        stylePasswordField(newPwd);
        stylePasswordField(confirmPwd);

        addFormRow(form, gbc, 0, "Mot de passe actuel:", oldPwd);
        addFormRow(form, gbc, 1, "Nouveau mot de passe:", newPwd);
        addFormRow(form, gbc, 2, "Confirmer:", confirmPwd);

        card.add(form, BorderLayout.CENTER);

        JButton changeBtn = AppTheme.createPrimaryButton("🔒  Changer le mot de passe");
        changeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        changeBtn.addActionListener(e -> {
            String old = new String(oldPwd.getPassword());
            String nw = new String(newPwd.getPassword());
            String cf = new String(confirmPwd.getPassword());

            if (old.isEmpty() || nw.isEmpty() || cf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!nw.equals(cf)) {
                JOptionPane.showMessageDialog(this, "Les nouveaux mots de passe ne correspondent pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (nw.length() < 6) {
                JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 6 caractères.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                authService.changePassword(currentUser.getId(), old, nw);
                JOptionPane.showMessageDialog(this, "✅  Mot de passe changé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                oldPwd.setText(""); newPwd.setText(""); confirmPwd.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌  " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(changeBtn, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAboutCard() {
        JPanel card = AppTheme.createCard();
        card.setLayout(new GridLayout(5, 1, 0, 6));
        card.setMaximumSize(new Dimension(600, 160));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cardTitle = new JLabel("ℹ️  À propos d'EduGest");
        cardTitle.setFont(AppTheme.FONT_HEADING);
        cardTitle.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(cardTitle);

        addLabelRow(card, "Version:", "1.0.0");
        addLabelRow(card, "Description:", "Système de Gestion Scolaire");
        addLabelRow(card, "Base de données:", "SQLite (school_management.db)");
        addLabelRow(card, "Technologie:", "Java Swing");
        return card;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_BOLD);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(AppTheme.FONT_LABEL);
        val.setForeground(AppTheme.TEXT_PRIMARY);

        panel.add(lbl);
        panel.add(val);
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_BOLD);
        lbl.setForeground(AppTheme.TEXT_PRIMARY);
        form.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(field, gbc);
    }

    private void addLabelRow(JPanel card, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_BOLD);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        JLabel val = new JLabel(value);
        val.setFont(AppTheme.FONT_LABEL);
        val.setForeground(AppTheme.TEXT_PRIMARY);
        row.add(lbl); row.add(val);
        card.add(row);
    }

    private void stylePasswordField(JPasswordField f) {
        f.setFont(AppTheme.FONT_LABEL);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }
}
