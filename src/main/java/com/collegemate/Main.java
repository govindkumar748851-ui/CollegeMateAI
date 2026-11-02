package com.collegemate;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.collegemate.api.ApiServer;
import com.collegemate.database.MongoDBConnection;
import com.collegemate.frontend.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {

    public static void main(String[] args) {

        try {

            // Step 1: Connect MongoDB
            MongoDBConnection.connect();

            // Step 2: Start REST API Server
            ApiServer apiServer = new ApiServer();
            apiServer.start();

            // Step 3: Setup UI Theme
            FlatLightLaf.setup();

            // Step 4: Launch Login Screen
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });

        } catch (Exception e) {

            e.printStackTrace();

            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(
                    null,
                    "Application startup failed. Check MongoDB or REST API configuration.",
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                )
            );

        }
    }
}