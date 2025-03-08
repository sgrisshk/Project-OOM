import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserService implements UserServiceInterface {
    private List<User> users;
    private String credentialsFilePath = "data/credentials.txt";
    
    // Constructor
    public UserService() {
        this.users = new ArrayList<>();
        loadUsers();
    }

    public void loadUsers() {
        try{ BufferedReader reader = new BufferedReader(new FileReader(credentialsFilePath));
            String line;
            while((line = reader.readLine()) != null){
                String[] credentials = line.split(":");
                String username = credentials[0];
                users.add(new User(username));
            }
        } catch (IOException e) {
            System.out.println("credentials file not found");
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
                if (credentials[0].equals(username)) {
                    return credentials[1];
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
            if (getPassword(username).equals(password)) {
                return getUserByUsername(username);
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error accessing user data");
        }
    }
    
}