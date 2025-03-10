package com.quackstagram.util;

import javax.swing.JOptionPane;
import java.awt.Component;

//Utilitaire pour afficher des boîtes de dialogue

public class DialogUtils {
    private DialogUtils() {}

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
} 