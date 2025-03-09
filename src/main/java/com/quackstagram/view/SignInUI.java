package com.quackstagram.view;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import com.quackstagram.model.User;
import com.quackstagram.service.UserService;
import com.quackstagram.util.UserSession;

public class SignInUI extends AuthUI {
    private static final Color PRIMARY_COLOR = new Color(255, 90, 95);
    private static final Color SECONDARY_COLOR = new Color(51, 51, 51);
    
    private final UserService userService;

    public SignInUI() {
        super("Quackstagram - Sign In");
        this.userService = new UserService();
        
        // Use AuthUI's initializeUI method with all three buttons
        initializeUI(
            false,  // no bio
            false,  // no photo upload
            "Sign In",
            this::onSignInClicked,
            "No Account? Register Now",
            this::onRegisterClicked,
            "Change Password",
            PRIMARY_COLOR,
            this::onChangePasswordClicked
        );
    }
    
   
      

    private void onSignInClicked(ActionEvent e) {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty() || 
            username.equals("Username")) {
            DialogUtils.showWarning(this, "Please enter your username and password");
            return;
        }

        try {
            User user = userService.authenticateUser(username, password);
            // Set the current user in the UserSession
            UserSession.getInstance().setCurrentUser(username);
            System.out.println("User logged in: " + username); // Debug message
            NavigationUtils.navigateTo(this, () -> new InstagramProfileUI(user));
        } catch (IllegalArgumentException ex) {
            DialogUtils.showError(this, ex.getMessage());
        }
    }

    private void onRegisterClicked(ActionEvent e) {
        NavigationUtils.navigateTo(this, SignUpUI::new);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignInUI().setVisible(true));
    }

    private void onChangePasswordClicked(ActionEvent e) {
        NavigationUtils.navigateTo(this, () -> new ChangePasswordUI());
    }
}


