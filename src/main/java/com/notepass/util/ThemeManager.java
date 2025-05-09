package com.notepass.util;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class ThemeManager {
    private static boolean isDarkMode = false;
    
    // Dark mode colors
    private static final String DARK_BACKGROUND = "#1e1e1e";
    private static final String DARK_SECONDARY = "#2d2d2d";
    private static final String DARK_ACCENT = "#3c3f41";
    private static final String DARK_TEXT = "#ffffff";
    private static final String DARK_PROMPT = "#a9a9a9";
    private static final String DARK_BUTTON = "#4c5052";
    private static final String DARK_BUTTON_HOVER = "#5c6062";
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    public static void toggleDarkMode(Pane rootContainer) {
        isDarkMode = !isDarkMode;
        applyTheme(rootContainer);
    }
    
    public static void applyTheme(Pane rootContainer) {
        if (isDarkMode) {
            // Apply dark mode styles
            rootContainer.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");
            
            // Style for text fields and password fields
            String inputDarkStyle = String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-prompt-text-fill: %s;",
                DARK_ACCENT, DARK_TEXT, DARK_PROMPT
            );
            
            // Style for text areas with additional properties
            String textAreaDarkStyle = String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-prompt-text-fill: %s; " +
                "-fx-control-inner-background: %s; -fx-faint-focus-color: transparent; " +
                "-fx-focus-color: %s; -fx-text-box-border: transparent;",
                DARK_ACCENT, DARK_TEXT, DARK_PROMPT, DARK_ACCENT, DARK_BUTTON
            );
            
            // Style for list views
            String listViewDarkStyle = String.format(
                "-fx-background-color: %s; -fx-control-inner-background: %s; -fx-text-fill: %s;",
                DARK_ACCENT, DARK_ACCENT, DARK_TEXT
            );
            
            // Style for buttons
            String buttonDarkStyle = String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-border-color: %s; -fx-border-width: 1;",
                DARK_BUTTON, DARK_TEXT, DARK_ACCENT
            );
            
            // Style for labels
            String labelDarkStyle = "-fx-text-fill: " + DARK_TEXT + ";";
            
            // Apply styles to all nodes
            applyStylesToNodes(rootContainer, inputDarkStyle, textAreaDarkStyle, listViewDarkStyle, buttonDarkStyle, labelDarkStyle);
            
        } else {
            // Reset to light mode
            rootContainer.setStyle("");
            resetStyles(rootContainer);
        }
    }
    
    private static void applyStylesToNodes(Pane root, String inputStyle, String textAreaStyle, 
                                         String listViewStyle, String buttonStyle, String labelStyle) {
        for (Node node : root.getChildren()) {
            if (node instanceof TextField || node instanceof PasswordField) {
                node.setStyle(inputStyle);
            } else if (node instanceof TextArea) {
                TextArea textArea = (TextArea) node;
                textArea.setStyle(textAreaStyle);
                // Ensure the content area has the correct background
                Node content = textArea.lookup(".content");
                if (content != null) {
                    content.setStyle("-fx-background-color: " + DARK_ACCENT + ";");
                }
            } else if (node instanceof ListView) {
                node.setStyle(listViewStyle);
            } else if (node instanceof Button) {
                node.setStyle(buttonStyle);
            } else if (node instanceof Label) {
                node.setStyle(labelStyle);
            } else if (node instanceof Pane) {
                applyStylesToNodes((Pane) node, inputStyle, textAreaStyle, listViewStyle, buttonStyle, labelStyle);
            }
        }
    }
    
    private static void resetStyles(Pane root) {
        for (Node node : root.getChildren()) {
            if (node instanceof Control) {
                node.setStyle("");
                if (node instanceof TextArea) {
                    Node content = ((TextArea) node).lookup(".content");
                    if (content != null) {
                        content.setStyle("");
                    }
                }
            } else if (node instanceof Pane) {
                resetStyles((Pane) node);
            }
        }
    }
} 