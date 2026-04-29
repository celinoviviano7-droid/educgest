package com.school.ui.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Thème visuel global de l'application.
 * Toutes les couleurs et styles sont centralisés ici.
 */
public class AppTheme {

    // Palette de couleurs
    public static final Color PRIMARY        = new Color(0x1A3A5C);
    public static final Color PRIMARY_LIGHT  = new Color(0x2E5F8C);
    public static final Color PRIMARY_DARK   = new Color(0x0D1F33);
    public static final Color ACCENT         = new Color(0xE8A020);
    public static final Color ACCENT_LIGHT   = new Color(0xF5C060);
    public static final Color SUCCESS        = new Color(0x2E8B57);
    public static final Color DANGER         = new Color(0xC0392B);
    public static final Color WARNING        = new Color(0xE67E22);
    public static final Color BG_MAIN        = new Color(0xF0F4F8);
    public static final Color BG_CARD        = Color.WHITE;
    public static final Color BG_SIDEBAR     = new Color(0x1A3A5C);
    public static final Color TEXT_PRIMARY   = new Color(0x1A2940);
    public static final Color TEXT_SECONDARY = new Color(0x6B7C93);
    public static final Color TEXT_WHITE     = Color.WHITE;
    public static final Color BORDER_LIGHT   = new Color(0xDDE3EC);
    public static final Color TABLE_HEADER   = new Color(0xEBF0F7);
    public static final Color TABLE_ALT_ROW  = new Color(0xF8FAFC);
    public static final Color TABLE_SELECT   = new Color(0xD0E3F5);

    // Polices
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 12);

    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background", BG_MAIN);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageFont", FONT_LABEL);
        UIManager.put("Button.font", FONT_LABEL);
        UIManager.put("Label.font", FONT_LABEL);
        UIManager.put("TextField.font", FONT_LABEL);
        UIManager.put("ComboBox.font", FONT_LABEL);
        UIManager.put("Table.font", FONT_LABEL);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("ScrollBar.width", 8);
    }

    /**
     * Crée un bouton primaire stylisé.
     */
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY, TEXT_WHITE);
    }

    public static JButton createAccentButton(String text) {
        return createButton(text, ACCENT, TEXT_PRIMARY);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, TEXT_WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, TEXT_WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, new Color(0xE8EDF5), TEXT_PRIMARY);
    }

    private static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 36));
        return btn;
    }

    /**
     * Crée un champ de texte stylisé avec placeholder.
     */
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_SECONDARY);
                    g2.setFont(FONT_LABEL.deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 8, getHeight() / 2 + 5);
                }
            }
        };
        field.setFont(FONT_LABEL);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        field.setBackground(BG_CARD);
        return field;
    }

    public static JComboBox<String> createComboBox(String... items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_LABEL);
        combo.setBackground(BG_CARD);
        combo.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        return combo;
    }

    /**
     * Crée un panneau carte avec ombre légère.
     */
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Ombre
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 12, 12);
                // Fond
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        return card;
    }

    /**
     * Crée un label titre de section.
     */
    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Style une JTable avec le thème de l'application.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_LABEL);
        table.setRowHeight(36);
        table.setGridColor(BORDER_LIGHT);
        table.setSelectionBackground(TABLE_SELECT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Header
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_LIGHT));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
    }
}
