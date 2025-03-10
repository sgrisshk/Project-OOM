package com.quackstagram.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.quackstagram.service.UserService;

public class ChangePasswordUI extends BaseUI {
    private static final Dimension FIELD_SIZE = new Dimension(250, 35);
    
    private final UserService userService;
    private final JTextField txtUsername;
    private final JPasswordField txtCurrentPassword;
    private final JPasswordField txtNewPassword;
    private final JPasswordField txtConfirmPassword;
    
    // Constructor
    public ChangePasswordUI() {
        super("Quackstagram - Change Password");
        this.userService = new UserService();
        this.txtUsername = new JTextField();
        this.txtCurrentPassword = new JPasswordField();
        this.txtNewPassword = new JPasswordField();
        this.txtConfirmPassword = new JPasswordField();
        
        initializeUI();
    }
    
    private void initializeUI() {
        JPanel mainPanel = componentFactory.createPanel(20);
        
        mainPanel.add(componentFactory.createLogo("img/logos/QuackstagramLogoTemp.png", 100, 120));
        mainPanel.add(Box.createVerticalStrut(30));
    
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(Color.WHITE);
     
        componentFactory.setupTextField(txtUsername, "Username", FIELD_SIZE);
        componentFactory.setupPasswordField(txtCurrentPassword, "Current Password", FIELD_SIZE);
        componentFactory.setupPasswordField(txtNewPassword, "New Password", FIELD_SIZE);
        componentFactory.setupPasswordField(txtConfirmPassword, "Confirm New Password", FIELD_SIZE);
        
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(txtCurrentPassword);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(txtNewPassword);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(txtConfirmPassword);
        
        mainPanel.add(fieldsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        mainPanel.add(componentFactory.createButtonPanel(
            new ButtonConfig("Update Password", PRIMARY_COLOR, Color.WHITE, this::onUpdatePasswordClicked),
            new ButtonConfig("Cancel", Color.WHITE, SECONDARY_COLOR, this::onCancelClicked)
        ));

        add(createHeaderPanel("Change Password"), BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void onUpdatePasswordClicked(ActionEvent e) {
        String username = txtUsername.getText();
        String currentPassword = new String(txtCurrentPassword.getPassword());
        String newPassword = new String(txtNewPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        
        if (username.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            DialogUtils.showWarning(this, "Please fill in all fields");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            DialogUtils.showWarning(this, "Passwords don't match");
            return;
        }
        
        try {
            // Authenticate user with current password
            userService.authenticateUser(username, currentPassword);
            
            // Change password
            userService.changePassword(username, newPassword);
            
            DialogUtils.showWarning(this, "Password updated successfully");
            
            NavigationUtils.navigateTo(this, () -> new SignInUI());
         
            
        } catch (IllegalArgumentException ex) {
            DialogUtils.showError(this, "Current password is incorrect");
        }
    }
    
    private void onCancelClicked(ActionEvent e) {
        NavigationUtils.navigateTo(this, () -> new SignInUI());
        
    }
}
   