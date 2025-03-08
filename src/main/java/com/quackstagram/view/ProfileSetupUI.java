import java.awt.event.*;
import javax.swing.*;

public class ProfileSetupUI extends AuthUI {
    private final UserService userService;
    private final User currentUser;

    public ProfileSetupUI(User user) {
        super("Profile Setup");
        this.userService = new UserService();
        this.currentUser = user;
        
        // Custom initialization to avoid showing username/password fields
        initializeCustomUI();
    }
    
    private void initializeCustomUI() {
        // Call parent's initializeUI but hide username/password fields afterward
        initializeUI(
                true,   // has bio
                true,   // has photo upload
                "Complete Profile",
                this::onCompleteProfileClicked,
                "Back to Sign In",  // In case user wants to change accounts
                this::onBackToSignInClicked,
                null,   // no tertiary button
                null,   // no tertiary button color
                null    // no tertiary button action
        );
        
        if (txtUsername != null) txtUsername.setVisible(false);
        if (txtPassword != null) txtPassword.setVisible(false);
        
    }

    private void onCompleteProfileClicked(ActionEvent e) {
        String bio = txtBio.getText();
        
        if (bio.isEmpty() || bio.equals("Bio")) {
            bio = ""; // Default empty bio
        }

        try {
            // Update the user's bio
            currentUser.setBio(bio);
            userService.saveUser(currentUser);
            
            // Navigate to main app
            JOptionPane.showMessageDialog(this, "Profile setup complete! Welcome to Quackstagram!", "Success", JOptionPane.INFORMATION_MESSAGE);
            NavigationUtils.navigateTo(this, () -> new InstagramProfileUI(currentUser));
        } catch (Exception ex) {
            DialogUtils.showError(this, "Failed to update profile: " + ex.getMessage());
        }
    }

    private void onBackToSignInClicked(ActionEvent e) {
        NavigationUtils.navigateTo(this, () -> new SignInUI());
    }
} 