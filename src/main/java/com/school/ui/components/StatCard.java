package com.school.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Carte de statistiques pour le tableau de bord.
 */
public class StatCard extends JPanel {

    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private final JLabel iconLabel;
    private final Color accentColor;

    public StatCard(String icon, String title, String value, Color accentColor) {
        this.accentColor = accentColor;

        setLayout(new BorderLayout(10, 5));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Icône + valeur
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(AppTheme.TEXT_PRIMARY);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(valueLabel, BorderLayout.EAST);

        // Titre
        titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_LABEL);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);

        add(topPanel, BorderLayout.CENTER);
        add(titleLabel, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fond blanc avec bord coloré gauche
        g2.setColor(AppTheme.BG_CARD);
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);

        // Barre colorée gauche
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, 5, getHeight() - 2, 4, 4);

        // Ombre légère
        g2.setColor(new Color(0, 0, 0, 10));
        g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 110);
    }
}
