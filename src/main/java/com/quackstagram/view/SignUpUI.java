package com.quackstagram.view;

import java.awt.event.ActionEvent;
import javax.swing.*;

import com.quackstagram.model.User;
import com.quackstagram.service.UserService;

public class SignUpUI extends AuthUI {
    private final UserService userService;

    public SignUpUI() {
        super("Sign Up");
        this.userService = new UserService();
        initializeUI(
                false,  // no bio
                false,  // no photo upload
                "Register",
                this::onRegisterClicked,
                "Already have an account? Sign In",
                this::openSignInUI,
                null,   // no tertiary button
                null,   // no tertiary button color
                null    // no tertiary button action
        );
    }

    private void onRegisterClicked(ActionEvent e) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            // Register with empty bio first
            User newUser = userService.registerUser(username, password, "");
            JOptionPane.showMessageDialog(this, "Registration successful! Let's set up your profile.", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Navigate to profile setup
            NavigationUtils.navigateTo(this, () -> new ProfileSetupUI(newUser));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSignInUI(ActionEvent e) {
        NavigationUtils.navigateTo(this, () -> new SignInUI());
    }
}