package com.school.ui.panels;

import com.school.model.SchoolClass;
import com.school.service.ClassService;
import com.school.ui.components.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClassPanel extends JPanel {

    private final ClassService classService = new ClassService();

    private JTable table;
    private DefaultTableModel tableModel;
    private List<SchoolClass> currentClasses;

    public ClassPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        buildUI();
        loadClasses();
    }

    private void buildUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(AppTheme.BG_MAIN);

        JLabel title = AppTheme.createSectionTitle("🏫  Gestion des Classes");
        panel.add(title, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setBackground(AppTheme.BG_MAIN);

        JButton addBtn = AppTheme.createPrimaryButton("＋  Nouvelle classe");
        addBtn.addActionListener(e -> showClassDialog(null));

        JButton editBtn = AppTheme.createSecondaryButton("✎  Modifier");
        editBtn.addActionListener(e -> editSelected());

        JButton deleteBtn = AppTheme.createDangerButton("✕  Supprimer");
        deleteBtn.addActionListener(e -> deleteSelected());

        toolbar.add(deleteBtn);
        toolbar.add(editBtn);
        toolbar.add(addBtn);
        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(14, 0, 0, 0));

        String[] cols = {"Nom", "Niveau", "Enseignant", "Élèves", "Capacité", "Places libres", "Taux d'occupation"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        AppTheme.styleTable(table);

        // Coloriser le taux d'occupation
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected && value != null) {
                    String v = value.toString().replace("%", "").trim();
                    try {
                        int pct = Integer.parseInt(v);
                        if (pct >= 90) setForeground(AppTheme.DANGER);
                        else if (pct >= 70) setForeground(AppTheme.WARNING);
                        else setForeground(AppTheme.SUCCESS);
                    } catch (Exception ex) { setForeground(AppTheme.TEXT_PRIMARY); }
                }
                setFont(AppTheme.FONT_BOLD);
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        int[] widths = {80, 120, 180, 70, 80, 90, 130};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void loadClasses() {
        currentClasses = classService.getAllClasses();
        tableModel.setRowCount(0);
        for (SchoolClass c : currentClasses) {
            int pct = c.getCapacity() > 0 ? (int)(c.getCurrentCount() * 100.0 / c.getCapacity()) : 0;
            tableModel.addRow(new Object[]{
                c.getName(), c.getLevel(), c.getTeacherName() != null ? c.getTeacherName() : "—",
                c.getCurrentCount(), c.getCapacity(), c.getAvailableSpots(), pct + "%"
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
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une classe."); return; }
        showClassDialog(currentClasses.get(row));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une classe."); return; }
        SchoolClass cls = currentClasses.get(row);
        if (cls.getCurrentCount() > 0) {
            JOptionPane.showMessageDialog(this,
                "Impossible de supprimer une classe avec des élèves inscrits.",
                "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer la classe " + cls.getName() + " ?", "Confirmation",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            classService.deleteClass(cls.getId());
            loadClasses();
        }
    }

    private void showClassDialog(SchoolClass existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Nouvelle classe" : "Modifier la classe", true);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppTheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel hdrLbl = new JLabel(existing == null ? "➕  Nouvelle classe" : "✎  Modifier la classe");
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

        JTextField nameField    = AppTheme.createTextField("Ex: CP1");
        JTextField levelField   = AppTheme.createTextField("Ex: Primaire");
        JTextField teacherField = AppTheme.createTextField("Nom de l'enseignant");
        JSpinner capSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 100, 1));
        capSpinner.setFont(AppTheme.FONT_LABEL);

        if (existing != null) {
            nameField.setText(existing.getName());
            if (existing.getId() > 0) nameField.setEditable(false);
            levelField.setText(existing.getLevel());
            if (existing.getTeacherName() != null) teacherField.setText(existing.getTeacherName());
            capSpinner.setValue(existing.getCapacity());
        }

        Object[][] rows = {
            {"Nom de la classe *", nameField},
            {"Niveau *", levelField},
            {"Enseignant", teacherField},
            {"Capacité *", capSpinner}
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
                SchoolClass cls = existing != null ? existing : new SchoolClass();
                cls.setName(nameField.getText().trim());
                cls.setLevel(levelField.getText().trim());
                cls.setTeacherName(teacherField.getText().trim());
                cls.setCapacity((int) capSpinner.getValue());
                classService.saveClass(cls);
                loadClasses();
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
