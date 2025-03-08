package com.quackstagram.util;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.function.Supplier;

/**
 * Utilitaire pour la navigation entre écrans
 */
public class NavigationUtils {
    private NavigationUtils() {}

    public static void navigateTo(JFrame current, Supplier<JFrame> nextScreen) {
        current.dispose();
        SwingUtilities.invokeLater(() -> nextScreen.get().setVisible(true));
    }
} 