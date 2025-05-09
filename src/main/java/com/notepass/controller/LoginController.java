package com.notepass.controller;

import com.notepass.util.DataStorage;
import com.notepass.util.ThemeManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button darkModeButton;
    @FXML private VBox rootContainer;

    public void initialize() {
        // Set initial dark mode button text
        updateDarkModeButtonText();
        
        // Apply current theme
        ThemeManager.applyTheme(rootContainer);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password");
            return;
        }

        DataStorage.User user = DataStorage.getInstance().getUser(username);
        if (user != null) {
            System.out.println("DB password: [" + user.getPassword() + "]");
            System.out.println("Entered password: [" + password + "]");
        }
        if (user != null && user.getPassword().equals(password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
                Parent root = loader.load();
                
                HomeController homeController = loader.getController();
                homeController.setUser(user);
                
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setTitle("NotePass - Home");
                stage.setScene(new Scene(root, 800, 600));
            } catch (Exception e) {
                showAlert("Error", "Failed to load home screen: " + e.getMessage());
            }
        } else {
            showAlert("Error", "Invalid username or password");
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password");
            return;
        }

        if (DataStorage.getInstance().getUser(username) != null) {
            showAlert("Error", "Username already exists");
            return;
        }

        try {
            DataStorage.User newUser = new DataStorage.User(0, username, password);
            boolean success = DataStorage.getInstance().addUser(newUser);
            if (success) {
                showAlert("Success", "Registration successful! Please login.");
                usernameField.clear();
                passwordField.clear();
            } else {
                showAlert("Error", "Registration failed. Username may already exist or there is a database error.");
            }
        } catch (Exception e) {
            showAlert("Error", "Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleDarkMode() {
        ThemeManager.toggleDarkMode(rootContainer);
        updateDarkModeButtonText();
    }

    private void updateDarkModeButtonText() {
        darkModeButton.setText(ThemeManager.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}