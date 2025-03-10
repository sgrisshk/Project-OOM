package com.quackstagram.util;

import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.quackstagram.model.User;
import com.quackstagram.view.BaseUI;
import com.quackstagram.view.ExploreUI;
import com.quackstagram.view.ImageUploadUI;
import com.quackstagram.view.InstagramProfileUI;
import com.quackstagram.view.NotificationsUI;
import com.quackstagram.view.QuakstagramHomeUI;

// Utility class for screen navigation within the application
public class NavigationUtils {
    private NavigationUtils() {}

    public static void navigateTo(JFrame current, Supplier<JFrame> nextScreen) {
        current.dispose();
        SwingUtilities.invokeLater(() -> nextScreen.get().setVisible(true));
    }

    public static void navigateTo(String screenType) {
        String username = UserSession.getInstance().getCurrentUsername();
        BaseUI nextScreen = createScreen(screenType, username);
        nextScreen.setVisible(true);
    }

    private static BaseUI createScreen(String screenType, String username) {
        BaseUI nextScreen;
        switch(screenType) {
            case "home":
                nextScreen = new QuakstagramHomeUI();
                break;
            case "explore":
                nextScreen = new ExploreUI(null);
                break;
            case "add":
                nextScreen = new ImageUploadUI();
                break;
            case "notifications":
                nextScreen = new NotificationsUI();
                break;
            case "profile":
                nextScreen = new InstagramProfileUI(new User(username));
                break;
            default:
                nextScreen = new QuakstagramHomeUI();
                break;
        }
        return nextScreen;
    }
}