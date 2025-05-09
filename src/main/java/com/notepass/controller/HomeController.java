package com.notepass.controller;

import com.notepass.util.DataStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HomeController {
    private DataStorage.User user;
    
    @FXML private TextField noteTitleField;
    @FXML private TextArea noteContentArea;
    @FXML private ListView<String> notesListView;
    
    @FXML private TextField passwordTitleField;
    @FXML private TextField passwordUsernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextArea passwordNotesArea;
    @FXML private ListView<String> passwordsListView;

    public void setUser(DataStorage.User user) {
        this.user = user;
        loadNotes();
        loadPasswords();
    }

    @FXML
    public void handleAddNote() {
        String title = noteTitleField.getText();
        String content = noteContentArea.getText();

        if (title.isEmpty()) {
            showAlert("Error", "Please enter a note title");
            return;
        }

        DataStorage.Note note = new DataStorage.Note(title, content);
        user.getNotes().add(note);
        DataStorage.getInstance().saveData();
        
        noteTitleField.clear();
        noteContentArea.clear();
        loadNotes();
    }

    @FXML
    public void handleSavePassword() {
        String title = passwordTitleField.getText();
        String username = passwordUsernameField.getText();
        String password = passwordField.getText();
        String notes = passwordNotesArea.getText();

        if (title.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter at least a title and password");
            return;
        }

        DataStorage.Password newPassword = new DataStorage.Password(title, username, password, notes);
        user.getPasswords().add(newPassword);
        DataStorage.getInstance().saveData();
        
        passwordTitleField.clear();
        passwordUsernameField.clear();
        passwordField.clear();
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