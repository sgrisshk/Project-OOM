package com.quackstagram.util;

import javax.swing.JOptionPane;
import java.awt.Component;

// Utility class for displaying dialog messages
public class DialogUtils {
    private DialogUtils() {}

    // Displays a warning message dialog
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    // Displays an error message dialog
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}