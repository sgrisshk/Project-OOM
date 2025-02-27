import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public abstract class AuthUI extends JFrame {
    protected JTextField txtUsername, txtPassword, txtBio;
    protected JButton btnPrimary, btnSecondary, btnUploadPhoto;
    protected JLabel lblPhoto;
    protected final String profilePhotoStoragePath = "img/storage/profile/";

    public AuthUI(String title) {
        setTitle(title);
        setSize(350, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    protected void initializeUI(boolean hasBio,
                                boolean hasPhotoUpload,
                                String primaryButtonText,
                                ActionListener primaryAction,
                                String secondaryButtonText,
                                ActionListener secondaryAction) {

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51));
        JLabel lblTitle = new JLabel("Quackstagram 🐥");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 40));
        lblPhoto = new JLabel();
        lblPhoto.setPreferredSize(new Dimension(80, 80));
        lblPhoto.setIcon(new ImageIcon(new ImageIcon("img/logos/DACS.png").getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoPanel.add(lblPhoto);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        txtUsername = new JTextField("Username");
        txtPassword = new JTextField("Password");
        txtUsername.setForeground(Color.GRAY);
        txtPassword.setForeground(Color.GRAY);

        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(photoPanel);
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(txtPassword);
        fieldsPanel.add(Box.createVerticalStrut(10));

        if (hasBio) {
            txtBio = new JTextField("Bio");
            txtBio.setForeground(Color.GRAY);
            fieldsPanel.add(txtBio);
            fieldsPanel.add(Box.createVerticalStrut(10));
        }

        if (hasBio) {
            btnUploadPhoto = new JButton("Upload Photo");
            btnUploadPhoto.addActionListener(e -> handleProfilePictureUpload());
            JPanel photoUploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            photoUploadPanel.add(btnUploadPhoto);
            fieldsPanel.add(photoUploadPanel);
        }

        btnPrimary = new JButton(primaryButtonText);
        btnPrimary.setBackground(new Color(255, 90, 95));
        btnPrimary.setForeground(Color.BLACK);
        btnPrimary.setFocusPainted(false);
        btnPrimary.setBorderPainted(false);
        btnPrimary.setFont(new Font("Arial", Font.BOLD, 14));
        btnPrimary.addActionListener(primaryAction);

        btnSecondary = new JButton(secondaryButtonText);
        btnSecondary.setBackground(Color.WHITE);
        btnSecondary.setForeground(Color.BLACK);
        btnSecondary.setFocusPainted(false);
        btnSecondary.setBorderPainted(false);
        btnSecondary.addActionListener(secondaryAction);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(btnPrimary);
        buttonPanel.add(btnSecondary);

        add(headerPanel, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void handleProfilePictureUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveProfilePicture(selectedFile, txtUsername.getText());
        }
    }

    private void saveProfilePicture(File file, String username) {
        try {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(profilePhotoStoragePath + username + ".png");
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}