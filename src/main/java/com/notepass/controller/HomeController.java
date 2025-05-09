package com.notepass.controller;

import java.util.List;

import com.notepass.util.DataStorage;
import com.notepass.util.ThemeManager;

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

    private List<DataStorage.Note> notes;
    private List<DataStorage.Password> passwords;

    public void initialize() {
        // Initially hide the visible password field
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);
        
        // Set initial dark mode button text
        updateDarkModeButtonText();
        
        // Apply current theme
        ThemeManager.applyTheme(rootContainer);
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
        ThemeManager.toggleDarkMode(rootContainer);
        updateDarkModeButtonText();
    }

    private void updateDarkModeButtonText() {
        darkModeButton.setText(ThemeManager.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode");
    }

    private void handleNoteSelection(MouseEvent event) {
        String selectedTitle = notesListView.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            notes.stream()
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
            passwords.stream()
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
        DataStorage.Note existing = notes.stream()
            .filter(note -> note.getTitle().equals(title))
            .findFirst().orElse(null);

        if (existing != null) {
            existing.setContent(content);
            DataStorage.getInstance().addOrUpdateNote(user.getId(), existing);
        } else {
            DataStorage.Note note = new DataStorage.Note(title, content);
            DataStorage.getInstance().addOrUpdateNote(user.getId(), note);
        }
        
        noteTitleField.clear();
        noteContentArea.clear();
        loadNotes();
    }

    @FXML
    public void handleSavePassword() {
        String title = passwordTitleField.getText();
        String username = passwordUsernameField.getText();
        String password = isPasswordVisible ? visiblePasswordField.getText() : passwordField.getText();
        String notesText = passwordNotesArea.getText();

        if (title.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter at least a title and password");
            return;
        }

        // Check if password with same title exists
        DataStorage.Password existing = passwords.stream()
            .filter(pwd -> pwd.getTitle().equals(title))
            .findFirst().orElse(null);

        if (existing != null) {
            existing.setUsername(username);
            existing.setPassword(password);
            existing.setNotes(notesText);
            DataStorage.getInstance().addOrUpdatePassword(user.getId(), existing);
        } else {
            DataStorage.Password newPassword = new DataStorage.Password(title, username, password, notesText);
            DataStorage.getInstance().addOrUpdatePassword(user.getId(), newPassword);
        }
        
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
        notes = DataStorage.getInstance().getNotes(user.getId());
        ObservableList<String> noteTitles = FXCollections.observableArrayList();
        for (DataStorage.Note note : notes) {
            noteTitles.add(note.getTitle());
        }
        notesListView.setItems(noteTitles);
    }

    private void loadPasswords() {
        passwords = DataStorage.getInstance().getPasswords(user.getId());
        ObservableList<String> passwordTitles = FXCollections.observableArrayList();
        for (DataStorage.Password password : passwords) {
            passwordTitles.add(password.getTitle());
        }
        passwordsListView.setItems(passwordTitles);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 