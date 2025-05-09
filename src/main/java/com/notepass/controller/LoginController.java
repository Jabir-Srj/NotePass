package com.notepass.controller;

import com.notepass.util.DataStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password");
            return;
        }

        DataStorage.User user = DataStorage.getInstance()
                .findUser(username)
                .orElse(null);

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            openHomePage(user);
        } else {
            messageLabel.setText("Invalid username or password");
        }
    }

    @FXML
    public void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password");
            return;
        }

        if (DataStorage.getInstance().findUser(username).isPresent()) {
            messageLabel.setText("Username already exists");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        DataStorage.User newUser = new DataStorage.User(username, hashedPassword);
        DataStorage.getInstance().addUser(newUser);
        messageLabel.setText("Registration successful! Please login.");
    }

    private void openHomePage(DataStorage.User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent root = loader.load();
            
            HomeController controller = loader.getController();
            controller.setUser(user);
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("NotePass - Home");
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            messageLabel.setText("Error loading home page: " + e.getMessage());
        }
    }
}