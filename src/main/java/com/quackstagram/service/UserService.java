package com.quackstagram.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.quackstagram.model.User;

public class UserService implements UserServiceInterface {
    private List<User> users;
    private String credentialsFilePath = "data/credentials.txt";
    
    // Constructor
    public UserService() {
        this.users = new ArrayList<>();
        loadUsers();
    }

    public void loadUsers() {
        try { 
            BufferedReader reader = new BufferedReader(new FileReader(credentialsFilePath));
            String line;
            while((line = reader.readLine()) != null){
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                String[] credentials = line.split(":");
                if (credentials.length > 0) {
                    String username = credentials[0];
                    String bio = credentials.length > 2 ? credentials[2] : "";
                    String password = credentials.length > 1 ? credentials[1] : "";
                    
                    // Create user with full info if available
                    User user = new User(username, bio, password);
                    users.add(user);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Credentials file not found: " + e.getMessage());
        }
    }

    public boolean userExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    public String getPassword(String username) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(":");
                if (credentials.length > 0 && credentials[0].equals(username)) {
                    // Make sure there's actually a password at index 1
                    if (credentials.length > 1) {
                        return credentials[1];
                    } else {
                        return ""; // Return empty string if no password set
                    }
                }
            }
        }
        return null;
    }
    
    public void changePassword(String username, String newPassword) {
        // Get existing user with all their data
        User user = getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Create updated user with same bio but new password
        User updatedUser = new User(username, user.getBio(), newPassword);
        
        // Use saveUser to persist the changes
        saveUser(updatedUser);
    }

    public void saveUser(User user) {
        List<String> lines = new ArrayList<>();
        boolean updated = false;
        
        //Read existing users
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(user.getUsername())) {
                    // Update existing user with the user.toString() which includes the new password
                    lines.add(user.toString());
                    updated = true;
                } else {
                    // Keep existing user
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving user: ");
        }
        
        //Add user if not updated
        if (!updated) {
            lines.add(user.toString());
        }
        
        // Write all users back
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFilePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving user: ");
        }
    }
    
    @Override
    public User registerUser(String username, String password, String bio) {
        // Check if username already exists
        try{
            userExists(username);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Username already exists");
        }
        try{
            // Create new user with provided details
            User newUser = new User(username, password, bio);
            users.add(newUser); 
            // Save user to storage (file/database)
            saveUser(newUser);
       
            return newUser;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Username already exists");
        }
    }
    
    @Override
    public User authenticateUser(String username, String password) {
        // Check if user exists
        if (!userExists(username)) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Check if password matches
        try {
            String storedPassword = getPassword(username);
            // Handle null or empty passwords
            if (storedPassword == null) {
                throw new IllegalArgumentException("User has no password set");
            }
            
            if (storedPassword.equals(password)) {
                return getUserByUsername(username);
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error accessing user data: " + e.getMessage());
        }
    }
    
}