import java.io.IOException;

public interface UserServiceInterface {
    User registerUser(String username, String password, String bio);
    User authenticateUser(String username, String password);
    boolean userExists(String username);
    User getUserByUsername(String username);
    void changePassword(String username, String newPassword);
    void saveUser(User user);
    String getPassword(String username) throws IOException;
}

