import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SignInUI extends AuthUI {
    private User newUser;

    public SignInUI() {
        super("Sign In");
        initializeUI(
                false,
                false,
                "Sign-In",
                this::onSignInClicked,
                "No Account? Register Now",
                this::openSignUpUI
        );
    }

    private void onSignInClicked(ActionEvent e) {
        String enteredUsername = txtUsername.getText();
        String enteredPassword = txtPassword.getText();
        System.out.println(enteredUsername + " <-> " + enteredPassword);

        if (verifyCredentials(enteredUsername, enteredPassword)) {
            System.out.println("Login successful!");

            dispose();

            SwingUtilities.invokeLater(() -> {
                InstagramProfileUI profileUI = new InstagramProfileUI(newUser);
                profileUI.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean verifyCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/credentials.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(":");
                if (credentials.length >= 3 && credentials[0].equals(username) && credentials[1].equals(password)) {
                    String bio = credentials[2];

                    newUser = new User(username, bio, password);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openSignUpUI(ActionEvent e) {
        setVisible(false);
        SwingUtilities.invokeLater(() -> {
            new SignUpUI().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignInUI frame = new SignInUI();
            frame.setVisible(true);
        });
    }
}