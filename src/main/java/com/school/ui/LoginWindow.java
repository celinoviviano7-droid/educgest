package com.school.ui;

import com.school.service.SessionManager;
import com.school.ui.components.AppTheme;
import com.school.ui.panels.LoginPanel;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    public LoginWindow() {
        setTitle("EduGest - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 580);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setResizable(false);

        LoginPanel loginPanel = new LoginPanel(this::onLoginSuccess);
        add(loginPanel);
        setVisible(true);
    }

    private void onLoginSuccess() {
        dispose();
        SwingUtilities.invokeLater(() -> new MainWindow(SessionManager.getInstance().getCurrentUser()));
    }
}
