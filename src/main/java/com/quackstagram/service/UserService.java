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
    private String followingFilePath = "data/following.txt";
    
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
                    continue; 
                }
                
                String[] credentials = line.split(":");
                if (credentials.length > 0) {
                    String username = credentials[0];
                    String bio = credentials.length > 2 ? credentials[2] : "";
                    String password = credentials.length > 1 ? credentials[1] : "";
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
                    if (credentials.length > 1) {
                        return credentials[1];
                    } else {
                        return ""; 
                    }
                }
            }
        }
        return null;
    }
    
    public void changePassword(String username, String newPassword) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Create updated user with same bio but new password
        User updatedUser = new User(username, user.getBio(), newPassword);
        saveUser(updatedUser);
    }

    public void saveUser(User user) {
        List<String> lines = new ArrayList<>();
        boolean updated = false;
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
        
        // Write all users back using the principle
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFilePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving user: ");
        }
    }
    
    private void initializeFollowingEntry(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(followingFilePath, true))) {
            writer.write(username + ":");  // Initialize with empty following list
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error initializing following entry: " + e.getMessage());
        }
    }
    
    @Override
    public User registerUser(String username, String password, String bio) {
        if (userExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        try {
            User newUser = new User(username, bio, password);
            users.add(newUser);
            saveUser(newUser);
            initializeFollowingEntry(username);  // Initialize following entry
            return newUser;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating user: " + e.getMessage());
        }
    }
    
    @Override
    public User authenticateUser(String username, String password) {
        if (!userExists(username)) {
            throw new IllegalArgumentException("User not found");
        }
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
    
  public void followUser(String follower, String followed) {
        try {
            // Read all current following relationships
            List<String> lines = new ArrayList<>();
            boolean updated = false;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(followingFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(follower + ":")) {
                        // Update the follower's line
                        String[] parts = line.split(":");
                        String following = parts.length > 1 ? parts[1] : "";
                        if (!following.isEmpty()) {
                            // Add to existing following list
                            lines.add(follower + ":" + following + "; " + followed);
                        } else {
                            // Create new following list
                            lines.add(follower + ":" + followed);
                        }
                        updated = true;
                    } else {
                        lines.add(line);
                    }
                }
            }
            
            // If the follower wasn't found, add a new entry
            if (!updated) {
                lines.add(follower + ":" + followed);
            }
            
            // Write back all relationships
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(followingFilePath))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating following relationship: " + e.getMessage());
        }
    }

    public List<String> getFollowing(String username) {
        List<String> following = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(followingFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(username + ":")) {
                    String[] parts = line.split(":");
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        String[] followedUsers = parts[1].split(";");
                        for (String user : followedUsers) {
                            following.add(user.trim());
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading following relationships: " + e.getMessage());
        }
        return following;
    }

    public boolean isFollowing(String follower, String followed) {
        List<String> following = getFollowing(follower);
        return following.contains(followed);
    }

    public int getFollowerCount(String username) {
        int followerCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(followingFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length > 1 && !parts[0].equals(username)) {
                    String[] followedUsers = parts[1].split(";");
                    for (String user : followedUsers) {
                        if (user.trim().equals(username)) {
                            followerCount++;
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading following relationships: " + e.getMessage());
        }
        return followerCount;
    }

    public void updateFollowerCount(User user) {
        int followerCount = getFollowerCount(user.getUsername());
        user.setFollowersCount(followerCount);
    }
}