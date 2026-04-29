package com.school.ui.panels;

import com.school.model.Student;
import com.school.service.ClassService;
import com.school.service.StudentService;
import com.school.ui.components.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentPanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private final ClassService classService = new ClassService();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> classFilter;
    private List<Student> currentStudents;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public StudentPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        buildUI();
        loadStudents();
    }

    private void buildUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(AppTheme.BG_MAIN);

        // Titre
        JLabel title = AppTheme.createSectionTitle("👨‍🎓  Gestion des Élèves");
        panel.add(title, BorderLayout.NORTH);

        // Barre d'outils
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(AppTheme.BG_MAIN);

        // Recherche + filtre
        JPanel leftTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftTools.setBackground(AppTheme.BG_MAIN);

        searchField = AppTheme.createTextField("🔍  Rechercher par nom ou matricule...");
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterStudents(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterStudents(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterStudents(); }
        });

        classFilter = new JComboBox<>();
        classFilter.addItem("Toutes les classes");
        classService.getAllClassNames().forEach(classFilter::addItem);
        classFilter.setPreferredSize(new Dimension(180, 36));
        classFilter.setFont(AppTheme.FONT_LABEL);
        classFilter.addActionListener(e -> filterStudents());

        leftTools.add(searchField);
        leftTools.add(classFilter);

        // Boutons d'action
        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightTools.setBackground(AppTheme.BG_MAIN);

        JButton addBtn = AppTheme.createPrimaryButton("＋  Nouvel élève");
        addBtn.addActionListener(e -> showStudentDialog(null));

        JButton editBtn = AppTheme.createSecondaryButton("✎  Modifier");
        editBtn.addActionListener(e -> editSelectedStudent());

        JButton deleteBtn = AppTheme.createDangerButton("✕  Supprimer");
        deleteBtn.addActionListener(e -> deleteSelectedStudent());

        rightTools.add(deleteBtn);
        rightTools.add(editBtn);
        rightTools.add(addBtn);

        toolbar.add(leftTools, BorderLayout.CENTER);
        toolbar.add(rightTools, BorderLayout.EAST);

        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(14, 0, 0, 0));

        String[] columns = {"Matricule", "Nom complet", "Classe", "Genre", "Date naiss.", "Parent", "Téléphone", "Inscrit le"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        AppTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-clic pour modifier
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editSelectedStudent();
            }
        });

        // Largeurs colonnes
        int[] widths = {100, 180, 80, 70, 100, 150, 120, 110};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Barre de statut
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(AppTheme.TABLE_HEADER);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER_LIGHT));

        JLabel statusLabel = new JLabel("Chargement...");
        statusLabel.setFont(AppTheme.FONT_SMALL);
        statusLabel.setForeground(AppTheme.TEXT_SECONDARY);
        statusBar.add(statusLabel);

        table.getModel().addTableModelListener(e -> {
            statusLabel.setText(tableModel.getRowCount() + " élève(s) affiché(s)");
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);
        return panel;
    }

    private void loadStudents() {
        currentStudents = studentService.getAllStudents();
        populateTable(currentStudents);
    }

    private void filterStudents() {
        String search = searchField.getText().trim();
        String cls = (String) classFilter.getSelectedItem();

        List<Student> filtered;
        if (!search.isEmpty()) {
            filtered = studentService.searchStudents(search);
        } else {
            filtered = studentService.getAllStudents();
        }

        if (cls != null && !cls.equals("Toutes les classes")) {
            filtered = filtered.stream()
                .filter(s -> cls.equals(s.getClassName()))
                .toList();
        }
        currentStudents = filtered;
        populateTable(filtered);
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                s.getMatricule(),
                s.getFullName(),
                s.getClassName(),
                s.getGender(),
                s.getBirthDate() != null ? s.getBirthDate().format(DATE_FMT) : "—",
                s.getParentName() != null ? s.getParentName() : "—",
                s.getParentPhone() != null ? s.getParentPhone() : "—",
                s.getEnrollmentDate() != null ? s.getEnrollmentDate().format(DATE_FMT) : "—"
            });
        }

        // Alternance des lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : AppTheme.TABLE_ALT_ROW);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    private void editSelectedStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un élève.", "Info",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Student s = currentStudents.get(row);
        showStudentDialog(s);
    }

    private void deleteSelectedStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un élève.", "Info",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Student s = currentStudents.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer l'élève " + s.getFullName() + " ?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            studentService.deleteStudent(s.getId());
            loadStudents();
        }
    }

    private void showStudentDialog(Student existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Nouvel élève" : "Modifier l'élève", true);
        dialog.setSize(560, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = buildStudentForm(existing, dialog);
        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel buildStudentForm(Student existing, JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Titre du dialogue
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppTheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel(existing == null ? "➕  Ajouter un élève" : "✎  Modifier l'élève");
        title.setFont(AppTheme.FONT_HEADING);
        title.setForeground(Color.WHITE);
        header.add(title);
        panel.add(header, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Champs
        JTextField firstNameField = AppTheme.createTextField("Prénom");
        JTextField lastNameField  = AppTheme.createTextField("Nom");
        JTextField matriculeField = AppTheme.createTextField("Auto-généré");
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Masculin", "Féminin"});
        JTextField birthDateField = AppTheme.createTextField("JJ/MM/AAAA");
        JComboBox<String> classCombo = new JComboBox<>();
        classService.getAllClassNames().forEach(classCombo::addItem);
        JTextField parentNameField  = AppTheme.createTextField("Nom du parent/tuteur");
        JTextField parentPhoneField = AppTheme.createTextField("Téléphone");
        JTextField addressField     = AppTheme.createTextField("Adresse");

        if (existing != null) {
            firstNameField.setText(existing.getFirstName());
            lastNameField.setText(existing.getLastName());
            matriculeField.setText(existing.getMatricule());
            matriculeField.setEditable(false);
            if (existing.getGender() != null)
                genderCombo.setSelectedItem(existing.getGender());
            if (existing.getBirthDate() != null)
                birthDateField.setText(existing.getBirthDate().format(DATE_FMT));
            classCombo.setSelectedItem(existing.getClassName());
            if (existing.getParentName() != null) parentNameField.setText(existing.getParentName());
            if (existing.getParentPhone() != null) parentPhoneField.setText(existing.getParentPhone());
            if (existing.getAddress() != null) addressField.setText(existing.getAddress());
        }

        Object[][] fields = {
            {"Prénom *", firstNameField},
            {"Nom *", lastNameField},
            {"Matricule", matriculeField},
            {"Genre", genderCombo},
            {"Date de naissance", birthDateField},
            {"Classe *", classCombo},
            {"Nom du parent", parentNameField},
            {"Téléphone parent", parentPhoneField},
            {"Adresse", addressField}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            JLabel lbl = new JLabel((String) fields[i][0]);
            lbl.setFont(AppTheme.FONT_BOLD);
            lbl.setForeground(AppTheme.TEXT_PRIMARY);
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add((Component) fields[i][1], gbc);
        }

        panel.add(form, BorderLayout.CENTER);

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnPanel.setBackground(AppTheme.BG_MAIN);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER_LIGHT));

        JButton cancelBtn = AppTheme.createSecondaryButton("Annuler");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = AppTheme.createPrimaryButton("💾  Enregistrer");
        saveBtn.addActionListener(e -> {
            try {
                Student student = existing != null ? existing : new Student();
                student.setFirstName(firstNameField.getText().trim());
                student.setLastName(lastNameField.getText().trim());
                student.setGender((String) genderCombo.getSelectedItem());
                student.setClassName((String) classCombo.getSelectedItem());
                String bdText = birthDateField.getText().trim();
                if (!bdText.isEmpty()) {
                    student.setBirthDate(LocalDate.parse(bdText, DATE_FMT));
                }
                student.setParentName(parentNameField.getText().trim());
                student.setParentPhone(parentPhoneField.getText().trim());
                student.setAddress(addressField.getText().trim());

                studentService.saveStudent(student);
                loadStudents();
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                    "✅  Élève enregistré avec succès.\nMatricule: " + student.getMatricule(),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "❌  " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }
}
