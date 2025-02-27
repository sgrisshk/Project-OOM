import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class SignUpUI extends AuthUI {
    private final String credentialsFilePath = "data/credentials.txt";

    public SignUpUI() {
        super("Sign Up");
        initializeUI(
                true,
                true,
                "Register",
                this::onRegisterClicked,
                "Already have an account? Sign In",
                this::openSignInUI
        );
    }

    private void onRegisterClicked(ActionEvent e) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String bio = txtBio.getText();

        if (doesUsernameExist(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Choose another one.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            saveCredentials(username, password, bio);
            dispose();
            SwingUtilities.invokeLater(SignInUI::new);
        }
    }

    private boolean doesUsernameExist(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(username + ":")) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveCredentials(String username, String password, String bio) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFilePath, true))) {
            writer.write(username + ":" + password + ":" + bio);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSignInUI(ActionEvent e) {
        setVisible(false);  // Скрываем окно, но не закрываем приложение
        SwingUtilities.invokeLater(() -> {
            new SignInUI().setVisible(true);
        });
    }
}