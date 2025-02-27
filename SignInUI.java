import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class SignInUI extends BaseUI {
    private static final Color PRIMARY_COLOR = new Color(255, 90, 95);
    private static final Color SECONDARY_COLOR = new Color(51, 51, 51);
    private static final Dimension FIELD_SIZE = new Dimension(250, 35);
    
    private final AuthService authService;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;

    public SignInUI() {
        super("Quackstagram - Sign In");
        this.authService = new AuthService();
        this.txtUsername = new JTextField();
        this.txtPassword = new JPasswordField();
        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = componentFactory.createPanel(20);
        
        // Add logo
        mainPanel.add(componentFactory.createLogo("img/logos/QuackstagramLogoTemp.png", 100, 120));
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Add input fields
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(Color.WHITE);

        componentFactory.setupTextField(txtUsername, "Username", FIELD_SIZE);
        componentFactory.setupPasswordField(txtPassword, "Password", FIELD_SIZE);
        
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(txtPassword);
        mainPanel.add(fieldsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Add buttons
        mainPanel.add(componentFactory.createButtonPanel(
            new ButtonConfig("Sign In", PRIMARY_COLOR, Color.WHITE, this::onSignInClicked),
            new ButtonConfig("No Account? Register Now", Color.WHITE, SECONDARY_COLOR, this::onRegisterClicked)
        ));

        add(createHeaderPanel("Sign In"), BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void onSignInClicked(ActionEvent e) {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty() || 
            username.equals("Username") || password.equals("Password")) {
            DialogUtils.showWarning(this, "Please enter your username and password");
            return;
        }

        try {
            User user = authService.authenticate(username, password);
            NavigationUtils.navigateTo(this, () -> new InstagramProfileUI(user));
        } catch (AuthException ex) {
            DialogUtils.showError(this, ex.getMessage());
        }
    }

    private void onRegisterClicked(ActionEvent e) {
        NavigationUtils.navigateTo(this, SignUpUI::new);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignInUI().setVisible(true));
    }
}

// Authentication 
class AuthService {
    public User authenticate(String username, String password) throws AuthException {
    try (BufferedReader reader = new BufferedReader(new FileReader("data/credentials.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] credentials = line.split(":");
            if (credentials[0].equals(username) && credentials[1].equals(password)) {
                    User user = new User(username, credentials[2], password);
                    saveUser(user);
                    return user;
                }
            }
            throw new AuthException("Invalid username or password");
        } catch (IOException e) {
            throw new AuthException("Authentication failed: " + e.getMessage());
        }
    }

    private void saveUser(User user) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.txt", false))) {
            writer.write(user.toString());
        }
    }
}

class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
