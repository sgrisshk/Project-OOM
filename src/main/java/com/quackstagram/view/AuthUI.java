package com.quackstagram.view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public abstract class AuthUI extends BaseUI {
    protected JTextField txtUsername;
    protected JPasswordField txtPassword;
    protected JTextField txtBio;
    protected JButton btnUploadPhoto;
    protected JLabel lblPhoto;
    protected final String profilePhotoStoragePath = "img/storage/profile/";
    private static final Dimension FIELD_SIZE = new Dimension(250, 35);

    public AuthUI(String title) {
        super(title);
    }


    
    protected void initializeUI(boolean hasBio,
                              boolean hasPhotoUpload,
                              String primaryButtonText,
                              ActionListener primaryAction,
                              String secondaryButtonText,
                              ActionListener secondaryAction,
                              String tertiaryButtonText,
                              Color tertiaryButtonColor,
                              ActionListener tertiaryAction) {

        JPanel mainPanel = componentFactory.createPanel(20);
        
        // Add logo
        mainPanel.add(componentFactory.createLogo("img/logos/QuackstagramLogoTemp.png", 100, 120));
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Add input fields
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(Color.WHITE);

        // Initialize fields
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        
        // Setup fields using component factory
        componentFactory.setupTextField(txtUsername, "Username", FIELD_SIZE);
        componentFactory.setupPasswordField(txtPassword, "Password", FIELD_SIZE);
        
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(txtPassword);

        if (hasBio) {
            txtBio = new JTextField();
            componentFactory.setupTextField(txtBio, "Bio", FIELD_SIZE);
            fieldsPanel.add(Box.createVerticalStrut(15));
            fieldsPanel.add(txtBio);
        }

        if (hasPhotoUpload) {
     
            // Upload button
            btnUploadPhoto = new JButton("Upload Photo");
            btnUploadPhoto.addActionListener(e -> handleProfilePictureUpload());
            JPanel photoUploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            photoUploadPanel.setBackground(Color.WHITE);
            photoUploadPanel.add(btnUploadPhoto);
            fieldsPanel.add(Box.createVerticalStrut(10));
            fieldsPanel.add(photoUploadPanel);
        }

        mainPanel.add(fieldsPanel);
        
        if (tertiaryButtonText != null && tertiaryAction != null) {
            mainPanel.add(componentFactory.createButtonPanel(
                new ButtonConfig(primaryButtonText, PRIMARY_COLOR, Color.WHITE, primaryAction),
                new ButtonConfig(tertiaryButtonText, tertiaryButtonColor != null ? tertiaryButtonColor : PRIMARY_COLOR, Color.WHITE, tertiaryAction),
                new ButtonConfig(secondaryButtonText, Color.WHITE, SECONDARY_COLOR, secondaryAction)
            ));
        } else {
            mainPanel.add(componentFactory.createButtonPanel(
                new ButtonConfig(primaryButtonText, PRIMARY_COLOR, Color.WHITE, primaryAction),
                new ButtonConfig(secondaryButtonText, Color.WHITE, SECONDARY_COLOR, secondaryAction)
            ));
        }

        add(createHeaderPanel(getTitle()), BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void handleProfilePictureUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveProfilePicture(selectedFile, txtUsername.getText());
            
            // Update preview
            ImageIcon newIcon = new ImageIcon(selectedFile.getPath());
            lblPhoto.setIcon(new ImageIcon(newIcon.getImage()
                .getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        }
    }

    private void saveProfilePicture(File file, String username) {
        try {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(profilePhotoStoragePath + username + ".png");
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            DialogUtils.showError(this, "Failed to save profile picture: " + e.getMessage());
        }
    }
}