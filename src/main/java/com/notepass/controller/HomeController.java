package com.notepass.controller;

import com.notepass.util.DataStorage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeController {
    private DataStorage.User user;
    private boolean isPasswordVisible = false;
    private boolean isDarkMode = false;
    
    @FXML private TextField noteTitleField;
    @FXML private TextArea noteContentArea;
    @FXML private ListView<String> notesListView;
    
    @FXML private TextField passwordTitleField;
    @FXML private TextField passwordUsernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private TextArea passwordNotesArea;
    @FXML private ListView<String> passwordsListView;
    @FXML private Button showPasswordButton;
    @FXML private Button darkModeButton;
    @FXML private VBox rootContainer;

    public void initialize() {
        // Initially hide the visible password field
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);
        
        // Set initial dark mode button text
        darkModeButton.setText("🌙 Dark Mode");
    }

    public void setUser(DataStorage.User user) {
        this.user = user;
        loadNotes();
        loadPasswords();
        setupListViews();
    }

    private void setupListViews() {
        // Setup notes list view
        notesListView.setOnMouseClicked(this::handleNoteSelection);
        
        // Setup passwords list view
        passwordsListView.setOnMouseClicked(this::handlePasswordSelection);
    }

    @FXML
    private void handleShowPassword() {
        isPasswordVisible = !isPasswordVisible;
        
        if (isPasswordVisible) {
            // Show password in text field
            visiblePasswordField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            showPasswordButton.setText("Hide Password");
        } else {
            // Hide password in password field
            passwordField.setText(visiblePasswordField.getText());
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            showPasswordButton.setText("Show Password");
        }
    }

    @FXML
    private void handleDarkMode() {
        isDarkMode = !isDarkMode;
        
        if (isDarkMode) {
            // Apply dark mode styles
            rootContainer.setStyle("-fx-background-color: #2b2b2b;");
            darkModeButton.setText("☀️ Light Mode");
            
            // Style for all text fields and areas
            String darkStyle = "-fx-background-color: #3c3f41; -fx-text-fill: white; -fx-prompt-text-fill: #a9a9a9;";
            noteTitleField.setStyle(darkStyle);
            noteContentArea.setStyle(darkStyle);
            passwordTitleField.setStyle(darkStyle);
            passwordUsernameField.setStyle(darkStyle);
            passwordField.setStyle(darkStyle);
            visiblePasswordField.setStyle(darkStyle);
            passwordNotesArea.setStyle(darkStyle);
            
            // Style for list views
            String listViewDarkStyle = "-fx-background-color: #3c3f41; -fx-text-fill: white;";
            notesListView.setStyle(listViewDarkStyle);
            passwordsListView.setStyle(listViewDarkStyle);
            
            // Style for buttons
            String buttonDarkStyle = "-fx-background-color: #4c5052; -fx-text-fill: white;";
            showPasswordButton.setStyle(buttonDarkStyle);
            darkModeButton.setStyle(buttonDarkStyle);
            
            // Style for labels
            String labelDarkStyle = "-fx-text-fill: white;";
            rootContainer.lookupAll(".label").forEach(node -> node.setStyle(labelDarkStyle));
        } else {
            // Reset to light mode
            rootContainer.setStyle("");
            darkModeButton.setText("🌙 Dark Mode");
            
            // Reset all styles
            noteTitleField.setStyle("");
            noteContentArea.setStyle("");
            passwordTitleField.setStyle("");
            passwordUsernameField.setStyle("");
            passwordField.setStyle("");
            visiblePasswordField.setStyle("");
            passwordNotesArea.setStyle("");
            notesListView.setStyle("");
            passwordsListView.setStyle("");
            showPasswordButton.setStyle("");
            darkModeButton.setStyle("");
            
            // Reset label styles
            rootContainer.lookupAll(".label").forEach(node -> node.setStyle(""));
        }
    }

    private void handleNoteSelection(MouseEvent event) {
        String selectedTitle = notesListView.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            user.getNotes().stream()
                .filter(note -> note.getTitle().equals(selectedTitle))
                .findFirst()
                .ifPresent(note -> {
                    noteTitleField.setText(note.getTitle());
                    noteContentArea.setText(note.getContent());
                });
        }
    }

    private void handlePasswordSelection(MouseEvent event) {
        String selectedTitle = passwordsListView.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            user.getPasswords().stream()
                .filter(pwd -> pwd.getTitle().equals(selectedTitle))
                .findFirst()
                .ifPresent(pwd -> {
                    passwordTitleField.setText(pwd.getTitle());
                    passwordUsernameField.setText(pwd.getUsername());
                    passwordField.setText(pwd.getPassword());
                    visiblePasswordField.setText(pwd.getPassword());
                    passwordNotesArea.setText(pwd.getNotes());
                });
        }
    }

    @FXML
    public void handleAddNote() {
        String title = noteTitleField.getText();
        String content = noteContentArea.getText();

        if (title.isEmpty()) {
            showAlert("Error", "Please enter a note title");
            return;
        }

        // Check if note with same title exists
        boolean noteExists = user.getNotes().stream()
            .anyMatch(note -> note.getTitle().equals(title));

        if (noteExists) {
            // Update existing note
            user.getNotes().stream()
                .filter(note -> note.getTitle().equals(title))
                .findFirst()
                .ifPresent(note -> note.setContent(content));
        } else {
            // Add new note
            DataStorage.Note note = new DataStorage.Note(title, content);
            user.getNotes().add(note);
        }
        
        DataStorage.getInstance().saveData();
        noteTitleField.clear();
        noteContentArea.clear();
        loadNotes();
    }

    @FXML
    public void handleSavePassword() {
        String title = passwordTitleField.getText();
        String username = passwordUsernameField.getText();
        String password = isPasswordVisible ? visiblePasswordField.getText() : passwordField.getText();
        String notes = passwordNotesArea.getText();

        if (title.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter at least a title and password");
            return;
        }

        // Check if password with same title exists
        boolean passwordExists = user.getPasswords().stream()
            .anyMatch(pwd -> pwd.getTitle().equals(title));

        if (passwordExists) {
            // Update existing password
            user.getPasswords().stream()
                .filter(pwd -> pwd.getTitle().equals(title))
                .findFirst()
                .ifPresent(pwd -> {
                    pwd.setUsername(username);
                    pwd.setPassword(password);
                    pwd.setNotes(notes);
                });
        } else {
            // Add new password
            DataStorage.Password newPassword = new DataStorage.Password(title, username, password, notes);
            user.getPasswords().add(newPassword);
        }
        
        DataStorage.getInstance().saveData();
        passwordTitleField.clear();
        passwordUsernameField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
        passwordNotesArea.clear();
        loadPasswords();
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) noteTitleField.getScene().getWindow();
            stage.setTitle("NotePass - Login");
            stage.setScene(new Scene(root, 600, 400));
        } catch (Exception e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void loadNotes() {
        ObservableList<String> notes = FXCollections.observableArrayList();
        for (DataStorage.Note note : user.getNotes()) {
            notes.add(note.getTitle());
        }
        notesListView.setItems(notes);
    }

    private void loadPasswords() {
        ObservableList<String> passwords = FXCollections.observableArrayList();
        for (DataStorage.Password password : user.getPasswords()) {
            passwords.add(password.getTitle());
        }
        passwordsListView.setItems(passwords);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 