package com.school.ui.components;

import com.school.model.Role;
import com.school.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Barre de navigation latérale avec items selon le rôle.
 */
public class Sidebar extends JPanel {

    public interface NavigationListener {
        void onNavigate(String panelName);
    }

    private final List<SidebarItem> items = new ArrayList<>();
    private SidebarItem selectedItem;
    private NavigationListener listener;

    public Sidebar(User currentUser, NavigationListener listener) {
        this.listener = listener;

        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_SIDEBAR);
        setPreferredSize(new Dimension(220, 0));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(AppTheme.BG_SIDEBAR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Logo / titre école
        JPanel logoPanel = createLogoPanel();
        mainPanel.add(logoPanel);

        // Profil utilisateur
        JPanel profilePanel = createProfilePanel(currentUser);
        mainPanel.add(profilePanel);

        mainPanel.add(Box.createVerticalStrut(10));

        // Séparateur de section
        addSectionLabel(mainPanel, "NAVIGATION");

        // Items selon le rôle
        buildMenuItems(currentUser.getRole(), mainPanel);

        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);

        // Bouton déconnexion en bas
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Sélectionner le premier item
        if (!items.isEmpty()) {
            selectItem(items.get(0));
        }
    }

    private JPanel createLogoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(AppTheme.PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel logo = new JLabel("🏫  EduGest");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        panel.add(logo);

        return panel;
    }

    private JPanel createProfilePanel(User user) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(0x0D2A45));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel nameLabel = new JLabel(user.getFullName());
        nameLabel.setFont(AppTheme.FONT_BOLD);
        nameLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel(user.getRole().getLabel());
        roleLabel.setFont(AppTheme.FONT_SMALL);
        roleLabel.setForeground(AppTheme.ACCENT_LIGHT);

        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setBackground(new Color(0x0D2A45));
        textPanel.add(nameLabel);
        textPanel.add(roleLabel);

        panel.add(avatar, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private void addSectionLabel(JPanel parent, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(0x5A7A9A));
        label.setBorder(BorderFactory.createEmptyBorder(16, 16, 6, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(label);
    }

    private void buildMenuItems(Role role, JPanel parent) {
        // Items communs
        addMenuItem(parent, "🏠", "Tableau de bord", "DASHBOARD");

        if (role == Role.ADMIN || role == Role.SECRETAIRE) {
            addMenuItem(parent, "👨‍🎓", "Élèves", "STUDENTS");
            addMenuItem(parent, "🏫", "Classes", "CLASSES");
        }

        if (role == Role.ADMIN || role == Role.TRESORIER) {
            addMenuItem(parent, "💰", "Paiements", "PAYMENTS");
            addMenuItem(parent, "📊", "Rapports", "REPORTS");
        }

        if (role == Role.ADMIN) {
            addSectionLabel(parent, "ADMINISTRATION");
            addMenuItem(parent, "👥", "Utilisateurs", "USERS");
            addMenuItem(parent, "⚙️", "Paramètres", "SETTINGS");
        }
    }

    private void addMenuItem(JPanel parent, String icon, String text, String panelName) {
        SidebarItem item = new SidebarItem(icon, text, panelName);
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectItem(item);
                if (listener != null) listener.onNavigate(panelName);
            }
        });
        items.add(item);
        parent.add(item);
    }

    private void selectItem(SidebarItem item) {
        if (selectedItem != null) {
            selectedItem.setSelected(false);
        }
        selectedItem = item;
        item.setSelected(true);
    }

    public void selectPanel(String panelName) {
        items.stream()
            .filter(i -> i.getPanelName().equals(panelName))
            .findFirst()
            .ifPresent(this::selectItem);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(AppTheme.PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        SidebarItem logoutItem = new SidebarItem("🚪", "Déconnexion", "LOGOUT");
        logoutItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) listener.onNavigate("LOGOUT");
            }
        });
        panel.add(logoutItem);

        return panel;
    }

    // --- Composant item de menu ---
    private static class SidebarItem extends JPanel {
        private final String panelName;
        private boolean selected = false;
        private boolean hovered = false;

        public SidebarItem(String icon, String text, String panelName) {
            this.panelName = panelName;

            setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            iconLabel.setForeground(Color.WHITE);

            JLabel textLabel = new JLabel(text);
            textLabel.setFont(AppTheme.FONT_LABEL);
            textLabel.setForeground(Color.WHITE);

            add(iconLabel);
            add(textLabel);

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        public String getPanelName() { return panelName; }

        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (selected) {
                g2.setColor(AppTheme.ACCENT);
                g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
            } else if (hovered) {
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
