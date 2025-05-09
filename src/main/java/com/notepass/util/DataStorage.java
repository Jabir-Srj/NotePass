package com.notepass.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "notepass")
public class DataStorage {
    private static final String DATA_FILE = "notepass_data.xml";
    private static final XmlMapper xmlMapper = new XmlMapper();
    private static DataStorage instance;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "user")
    private List<User> users = new ArrayList<>();

    private DataStorage() {}

    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = loadData();
        }
        return instance;
    }

    private static DataStorage loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try {
                return xmlMapper.readValue(file, DataStorage.class);
            } catch (IOException e) {
                System.err.println("Error loading data file: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return new DataStorage();
    }

    public void saveData() {
        try {
            xmlMapper.writeValue(new File(DATA_FILE), this);
        } catch (IOException e) {
            System.err.println("Error saving data file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<User> findUser(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public void addUser(User user) {
        users.add(user);
        saveData();
    }

    public static class User {
        private String username;
        private String password;
        private List<Note> notes = new ArrayList<>();
        private List<Password> passwords = new ArrayList<>();

        public User() {}

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public List<Note> getNotes() { return notes; }
        public void setNotes(List<Note> notes) { this.notes = notes; }
        public List<Password> getPasswords() { return passwords; }
        public void setPasswords(List<Password> passwords) { this.passwords = passwords; }
    }

    public static class Note {
        private String title;
        private String content;
        private String createdAt;

        public Note() {}

        public Note(String title, String content) {
            this.title = title;
            this.content = content;
            this.createdAt = java.time.LocalDateTime.now().toString();
        }

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    public static class Password {
        private String title;
        private String username;
        private String password;
        private String notes;
        private String createdAt;

        public Password() {}

        public Password(String title, String username, String password, String notes) {
            this.title = title;
            this.username = username;
            this.password = password;
            this.notes = notes;
            this.createdAt = java.time.LocalDateTime.now().toString();
        }

        // Getters and setters
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