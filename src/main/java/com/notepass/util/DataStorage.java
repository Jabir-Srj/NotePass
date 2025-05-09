package com.notepass.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private static DataStorage instance;

    private DataStorage() {}

    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    // User CRUD
    public User getUser(String username) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String password = rs.getString("password");
                return new User(userId, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addUser(User user) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL Error: " + e.getMessage(), e);
        }
        return false;
    }

    // Notes CRUD
    public List<Note> getNotes(int userId) {
        List<Note> notes = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM notes WHERE user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(new Note(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public boolean addOrUpdateNote(int userId, Note note) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            if (note.getId() > 0) {
                // Update
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE notes SET title=?, content=? WHERE id=? AND user_id=?")) {
                    stmt.setString(1, note.getTitle());
                    stmt.setString(2, note.getContent());
                    stmt.setInt(3, note.getId());
                    stmt.setInt(4, userId);
                    return stmt.executeUpdate() > 0;
                }
            } else {
                // Insert
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO notes (user_id, title, content, created_at) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, note.getTitle());
                    stmt.setString(3, note.getContent());
                    stmt.setString(4, note.getCreatedAt());
                    int affected = stmt.executeUpdate();
                    if (affected > 0) {
                        ResultSet keys = stmt.getGeneratedKeys();
                        if (keys.next()) note.setId(keys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Passwords CRUD
    public List<Password> getPasswords(int userId) {
        List<Password> passwords = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM passwords WHERE user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                passwords.add(new Password(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("notes"),
                    rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passwords;
    }

    public boolean addOrUpdatePassword(int userId, Password pwd) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            if (pwd.getId() > 0) {
                // Update
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE passwords SET title=?, username=?, password=?, notes=? WHERE id=? AND user_id=?")) {
                    stmt.setString(1, pwd.getTitle());
                    stmt.setString(2, pwd.getUsername());
                    stmt.setString(3, pwd.getPassword());
                    stmt.setString(4, pwd.getNotes());
                    stmt.setInt(5, pwd.getId());
                    stmt.setInt(6, userId);
                    return stmt.executeUpdate() > 0;
                }
            } else {
                // Insert
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO passwords (user_id, title, username, password, notes, created_at) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, pwd.getTitle());
                    stmt.setString(3, pwd.getUsername());
                    stmt.setString(4, pwd.getPassword());
                    stmt.setString(5, pwd.getNotes());
                    stmt.setString(6, pwd.getCreatedAt());
                    int affected = stmt.executeUpdate();
                    if (affected > 0) {
                        ResultSet keys = stmt.getGeneratedKeys();
                        if (keys.next()) pwd.setId(keys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Model classes
    public static class User {
        private int id;
        private String username;
        private String password;

        public User(int id, String username, String password) {
            this.id = id;
            this.username = username;
            this.password = password;
        }
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class Note {
        private int id;
        private String title;
        private String content;
        private String createdAt;

        public Note(int id, String title, String content, String createdAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.createdAt = createdAt;
        }
        public Note(String title, String content) {
            this(0, title, content, java.time.LocalDateTime.now().toString());
        }
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    public static class Password {
        private int id;
        private String title;
        private String username;
        private String password;
        private String notes;
        private String createdAt;

        public Password(int id, String title, String username, String password, String notes, String createdAt) {
            this.id = id;
            this.title = title;
            this.username = username;
            this.password = password;
            this.notes = notes;
            this.createdAt = createdAt;
        }
        public Password(String title, String username, String password, String notes) {
            this(0, title, username, password, notes, java.time.LocalDateTime.now().toString());
        }
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
} 