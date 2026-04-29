package com.school.ui.panels;

import com.school.config.DatabaseManager;
import com.school.model.Role;
import com.school.model.User;
import com.school.service.AuthService;
import com.school.ui.components.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsersPanel extends JPanel {

    private final AuthService authService = new AuthService();
    private JTable table;
    private DefaultTableModel tableModel;
    private List<User> currentUsers;

    public UsersPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        buildUI();
        loadUsers();
    }

    private void buildUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(AppTheme.BG_MAIN);

        JLabel title = AppTheme.createSectionTitle("👥  Gestion des Utilisateurs");
        panel.add(title, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setBackground(AppTheme.BG_MAIN);

        JButton addBtn = AppTheme.createPrimaryButton("＋  Nouvel utilisateur");
        addBtn.addActionListener(e -> showUserDialog(null));

        JButton editBtn = AppTheme.createSecondaryButton("✎  Modifier");
        editBtn.addActionListener(e -> editSelected());

        JButton resetPwdBtn = AppTheme.createAccentButton("🔑  Réinitialiser MDP");
        resetPwdBtn.addActionListener(e -> resetPassword());

        toolbar.add(resetPwdBtn);
        toolbar.add(editBtn);
        toolbar.add(addBtn);
        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(14, 0, 0, 0));

        String[] cols = {"Nom complet", "Nom d'utilisateur", "Rôle", "Statut"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        AppTheme.styleTable(table);

        // Coloriser le rôle
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    switch (value.toString()) {
                        case "Directeur" -> setForeground(AppTheme.PRIMARY);
                        case "Secrétaire" -> setForeground(AppTheme.SUCCESS);
                        case "Trésorier" -> setForeground(AppTheme.ACCENT);
                        default -> setForeground(AppTheme.TEXT_PRIMARY);
                    }
                }
                setFont(AppTheme.FONT_BOLD);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        // Coloriser statut
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    if (value.toString().equals("✅ Actif")) setForeground(AppTheme.SUCCESS);
                    else setForeground(AppTheme.DANGER);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void loadUsers() {
        currentUsers = authService.getAllUsers();
        tableModel.setRowCount(0);
        for (User u : currentUsers) {
            tableModel.addRow(new Object[]{
                u.getFullName(), u.getUsername(),
                u.getRole().getLabel(),
                u.isActive() ? "✅ Actif" : "❌ Inactif"
            });
        }
        // Alternance lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : AppTheme.TABLE_ALT_ROW);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur."); return; }
        showUserDialog(currentUsers.get(row));
    }

    private void resetPassword() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur."); return; }
        User user = currentUsers.get(row);

        String newPwd = JOptionPane.showInputDialog(this,
            "Nouveau mot de passe pour " + user.getFullName() + ":", "Réinitialisation", JOptionPane.PLAIN_MESSAGE);
        if (newPwd != null && !newPwd.trim().isEmpty()) {
            authService.resetPassword(user.getId(), newPwd.trim());
            JOptionPane.showMessageDialog(this, "✅  Mot de passe réinitialisé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showUserDialog(User existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Nouvel utilisateur" : "Modifier l'utilisateur", true);
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppTheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel hdrLbl = new JLabel(existing == null ? "➕  Nouvel utilisateur" : "✎  Modifier l'utilisateur");
        hdrLbl.setFont(AppTheme.FONT_HEADING);
        hdrLbl.setForeground(Color.WHITE);
        header.add(hdrLbl);
        panel.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 6, 8, 6);

        JTextField fullNameField = AppTheme.createTextField("Nom et prénom");
        JTextField usernameField = AppTheme.createTextField("Identifiant de connexion");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(AppTheme.FONT_LABEL);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1),
            new EmptyBorder(6, 10, 6, 10)));

        JComboBox<Role> roleCombo = new JComboBox<>(Role.values());
        roleCombo.setFont(AppTheme.FONT_LABEL);

        JCheckBox activeCheck = new JCheckBox("Compte actif");
        activeCheck.setFont(AppTheme.FONT_LABEL);
        activeCheck.setBackground(Color.WHITE);
        activeCheck.setSelected(true);

        if (existing != null) {
            fullNameField.setText(existing.getFullName());
            usernameField.setText(existing.getUsername());
            usernameField.setEditable(false);
            roleCombo.setSelectedItem(existing.getRole());
            activeCheck.setSelected(existing.isActive());
        }

        Object[][] rows = {
            {"Nom complet *", fullNameField},
            {"Nom d'utilisateur *", usernameField},
            existing == null ? new Object[]{"Mot de passe *", passwordField} : new Object[]{"(MDP inchangé)", new JLabel("Utiliser 'Réinitialiser MDP'")},
            {"Rôle *", roleCombo},
            {"", activeCheck}
        };

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.4;
            JLabel lbl = new JLabel((String) rows[i][0]);
            lbl.setFont(AppTheme.FONT_BOLD);
            lbl.setForeground(AppTheme.TEXT_PRIMARY);
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.6;
            form.add((Component) rows[i][1], gbc);
        }
        panel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnPanel.setBackground(AppTheme.BG_MAIN);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER_LIGHT));

        JButton cancelBtn = AppTheme.createSecondaryButton("Annuler");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = AppTheme.createPrimaryButton("💾  Enregistrer");
        saveBtn.addActionListener(e -> {
            try {
                if (existing == null) {
                    User u = new User();
                    u.setFullName(fullNameField.getText().trim());
                    u.setUsername(usernameField.getText().trim());
                    String pwd = new String(passwordField.getPassword());
                    if (pwd.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Mot de passe requis."); return; }
                    u.setPasswordHash(DatabaseManager.hashPassword(pwd));
                    u.setRole((Role) roleCombo.getSelectedItem());
                    u.setActive(activeCheck.isSelected());
                    authService.createUser(u);
                } else {
                    existing.setFullName(fullNameField.getText().trim());
                    existing.setRole((Role) roleCombo.getSelectedItem());
                    existing.setActive(activeCheck.isSelected());
                    authService.updateUser(existing);
                }
                loadUsers();
                dialog.dispose();
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
