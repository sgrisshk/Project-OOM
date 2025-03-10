package com.quackstagram.util;

// track the currently logged in user

public class UserSession {
    private static UserSession instance;
    private String currentUsername;
    
    // Private constructor to prevent direct instantiation
    private UserSession() {
        // Default to "guest" if not logged in
        currentUsername = "guest";
    }
    
    // Get the single instance of UserSession
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    // Set the current user
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }
    
    // Get the current user
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    // Check if a user is logged in
    public boolean isLoggedIn() {
        return currentUsername != null && !currentUsername.isEmpty();
    }
    
    // Log out the current user
    public void logout() {
        currentUsername = null;
    }
} 