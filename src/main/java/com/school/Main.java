package com.school;

import com.school.config.DatabaseManager;
import com.school.ui.LoginWindow;
import com.school.ui.components.AppTheme;

import javax.swing.*;

/**
 * Point d'entrée principal de l'application EduGest.
 */
public class Main {

    public static void main(String[] args) {
        // Appliquer le thème global
        AppTheme.applyGlobalTheme();

        // Initialiser la base de données au démarrage
        try {
            DatabaseManager.getInstance();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Erreur lors de l'initialisation de la base de données:\n" + e.getMessage(),
                "Erreur critique", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Lancer l'interface graphique dans le thread EDT
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}
