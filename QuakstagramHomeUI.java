import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class QuakstagramHomeUI extends BaseUI {
    // UI dimensions
    private static final int WIDTH = 360;
    private static final int HEIGHT = 600;
    private static final int NAV_ICON_SIZE = 24;
    private static final int IMAGE_WIDTH = WIDTH - 60;
    private static final int IMAGE_HEIGHT = 200;
    
    // Colors
    private static final Color LIKE_BUTTON_COLOR = new Color(255, 48, 64);
    private static final Color UNLIKE_BUTTON_COLOR = new Color(200, 200, 200);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color HEADER_COLOR = new Color(65, 105, 225);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color POST_BACKGROUND = new Color(255, 255, 255);
    
    // UI components
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel homePanel;
    private JPanel imageViewPanel;
    
    // Track liked posts
    private List<String> likedPosts = new ArrayList<>();
    

    public QuakstagramHomeUI() {
        super("Quakstagram Home");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Set up card layout for switching between home and image view
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BACKGROUND_COLOR);
        
        homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(BACKGROUND_COLOR);
        
        imageViewPanel = new JPanel(new BorderLayout());
        imageViewPanel.setBackground(BACKGROUND_COLOR);
        
        // Load liked posts
        loadLikedPosts();

        // Initialize UI components and add to layout
        setupHomePanel();
        cardPanel.add(homePanel, "Home");
        cardPanel.add(imageViewPanel, "ImageView");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "Home");
        
        // Add header and navigation
        JPanel headerPanel = createHeaderPanel("Quackstagram");
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setForeground(Color.WHITE);
        add(headerPanel, BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.SOUTH);
    }
    
  
    private void loadLikedPosts() {
        likedPosts.clear();
        String currentUser = getCurrentUser();
        
        // Check notifications.txt for likes by current user
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "notifications.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("; ");
                if (parts.length >= 3 && parts[1].equals(currentUser)) {
                    likedPosts.add(parts[2]); // Add the imageId to liked posts
                }
            }
        } catch (IOException e) {
            // If file doesn't exist yet, that's fine - just start with empty likes
        }
    }
    

    private String getCurrentUser() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                return line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    private void setupHomePanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Load posts and populate content panel
        String[][] posts = loadPosts();
        for (String[] post : posts) {
            createPostItem(contentPanel, post);
        }
        
        homePanel.add(scrollPane, BorderLayout.CENTER);
    }
  
    private String[][] loadPosts() {
        String currentUser = "";
        String followedUsers = "";
        
        // Get current user
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                currentUser = line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Get followed users
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "following.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(currentUser + ":")) {
                    followedUsers = line.split(":")[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Load posts from image_details.txt
        String[][] tempData = new String[100][];
        int count = 0;
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("img", "image_details.txt"))) {
            String line;
            while ((line = reader.readLine()) != null && count < tempData.length) {
                String[] details = line.split(", ");
                String imagePoster = details[1].split(": ")[1];
                
                if (followedUsers.contains(imagePoster)) {
                    String imagePath = "img/uploaded/" + details[0].split(": ")[1] + ".png";
                    String description = details[2].split(": ")[1];
                    String likes = "Likes: " + details[4].split(": ")[1];
                    
                    tempData[count++] = new String[]{imagePoster, description, likes, imagePath};
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Create final array with correct size
        String[][] posts = new String[count][];
        System.arraycopy(tempData, 0, posts, 0, count);
        
        return posts;
    }

    private void createPostItem(JPanel panel, String[] postData) {
        String username = postData[0];
        String description = postData[1];
        String likes = postData[2];
        String imagePath = postData[3];
        String imageId = new File(imagePath).getName().split("\\.")[0];
        boolean isLiked = likedPosts.contains(imageId);
        
        // Create post panel
        JPanel postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postPanel.setBackground(POST_BACKGROUND);
        postPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        postPanel.setAlignmentX(CENTER_ALIGNMENT);
        
        // Add username
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(TEXT_COLOR);
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.setBackground(POST_BACKGROUND);
        usernamePanel.add(usernameLabel);
        postPanel.add(usernamePanel);
        
        // Add spacing
        postPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Add image
        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            BufferedImage croppedImage = originalImage.getSubimage(0, 0, 
                Math.min(originalImage.getWidth(), IMAGE_WIDTH), 
                Math.min(originalImage.getHeight(), IMAGE_HEIGHT));
            ImageIcon imageIcon = new ImageIcon(croppedImage);
            imageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            imageLabel.setText("Image not found");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        JPanel imageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imageContainer.setBackground(POST_BACKGROUND);
        imageContainer.add(imageLabel);
        postPanel.add(imageContainer);
        
        // Add spacing
        postPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Add description
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionLabel.setForeground(TEXT_COLOR);
        JPanel descriptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        descriptionPanel.setBackground(POST_BACKGROUND);
        descriptionPanel.add(descriptionLabel);
        postPanel.add(descriptionPanel);
        
        // Add likes
        JLabel likesLabel = new JLabel(likes);
        likesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        likesLabel.setForeground(TEXT_COLOR);
        JPanel likesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        likesPanel.setBackground(POST_BACKGROUND);
        likesPanel.add(likesLabel);
        postPanel.add(likesPanel);
        
        // Add like button - use filled heart if already liked, otherwise use empty heart
        JButton likeButton = new JButton("♥️");
        likeButton.setFont(new Font("Arial", Font.BOLD, 16));
        likeButton.setForeground(Color.WHITE);
        
        // Set initial state based on if post is already liked
        updateLikeButtonState(likeButton, isLiked);
        
        likeButton.setOpaque(true);
        likeButton.setBorderPainted(false);
        likeButton.setFocusPainted(false);
        likeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        likeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        likeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean wasLiked = likedPosts.contains(imageId);
                handleLikeToggle(imageId, likesLabel, likeButton, wasLiked);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(POST_BACKGROUND);
        buttonPanel.add(likeButton);
        postPanel.add(buttonPanel);
        
        // Make image clickable
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayImage(postData, isLiked);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        
        // Add post to content panel
        panel.add(postPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void updateLikeButtonState(JButton likeButton, boolean isLiked) {
        if (isLiked) {
            likeButton.setBackground(LIKE_BUTTON_COLOR);
        } else {
            likeButton.setBackground(UNLIKE_BUTTON_COLOR);
        }
    }
    

    private void handleLikeToggle(String imageId, JLabel likesLabel, JButton likeButton, boolean wasLiked) {
        Path detailsPath = Paths.get("img", "image_details.txt");
        StringBuilder newContent = new StringBuilder();
        boolean updated = false;
        String currentUser = getCurrentUser();
        String imageOwner = "";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Update likes in image_details.txt
        try (BufferedReader reader = Files.newBufferedReader(detailsPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ImageID: " + imageId)) {
                    String[] parts = line.split(", ");
                    imageOwner = parts[1].split(": ")[1];
                    int likes = Integer.parseInt(parts[4].split(": ")[1]);
                    
                    if (wasLiked) {
                        // Unlike the post
                        likes--; // Decrement likes
                        likedPosts.remove(imageId);
                        updateLikeButtonState(likeButton, false);
                    } else {
                        // Like the post
                        likes++; // Increment likes
                        likedPosts.add(imageId);
                        updateLikeButtonState(likeButton, true);
                    }
                    
                    parts[4] = "Likes: " + likes;
                    line = String.join(", ", parts);

                    // Update UI
                    likesLabel.setText("Likes: " + likes);
                    updated = true;
                }
                newContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write updated content back to file
        if (updated) {
            try (BufferedWriter writer = Files.newBufferedWriter(detailsPath)) {
                writer.write(newContent.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // Update notifications file
            if (!wasLiked) {
                // Add notification for a new like
                String notification = String.format("%s; %s; %s; %s\n", imageOwner, currentUser, imageId, timestamp);
                try (BufferedWriter notificationWriter = Files.newBufferedWriter(
                        Paths.get("data", "notifications.txt"), 
                        StandardOpenOption.CREATE, 
                        StandardOpenOption.APPEND)) {
                    notificationWriter.write(notification);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Remove notification for unlike
                try {
                    Path notificationsPath = Paths.get("data", "notifications.txt");
                    List<String> notificationLines = Files.readAllLines(notificationsPath);
                    List<String> updatedLines = new ArrayList<>();
                    
                    for (String line : notificationLines) {
                        String[] parts = line.split("; ");
                        if (parts.length >= 3 && parts[1].equals(currentUser) && parts[2].equals(imageId)) {
                        } else {
                            updatedLines.add(line);
                        }
                    }
                    
                    Files.write(notificationsPath, updatedLines);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

  
    private void displayImage(String[] postData, boolean isLiked) {
        imageViewPanel.removeAll();
        
        String username = postData[0];
        String description = postData[1];
        String likes = postData[2];
        String imagePath = postData[3];
        String imageId = new File(imagePath).getName().split("\\.")[0];
        
        // Add back button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(HEADER_COLOR);
        backButton.setBackground(BACKGROUND_COLOR);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "Home"));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        imageViewPanel.add(topPanel, BorderLayout.NORTH);
        
        // Add image
        JLabel fullImageLabel = new JLabel();
        fullImageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            int displayWidth = WIDTH - 40;
            int displayHeight = HEIGHT - 200;
            double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
            int scaledWidth, scaledHeight;
            
            if (displayWidth / aspectRatio <= displayHeight) {
                scaledWidth = displayWidth;
                scaledHeight = (int) (displayWidth / aspectRatio);
            } else {
                scaledHeight = displayHeight;
                scaledWidth = (int) (displayHeight * aspectRatio);
            }
            
            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(scaledImage);
            fullImageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            fullImageLabel.setText("Image not found");
            fullImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        JPanel imageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imageContainer.setBackground(BACKGROUND_COLOR);
        imageContainer.add(fullImageLabel);
        imageViewPanel.add(imageContainer, BorderLayout.CENTER);
        
        // Add user info and like panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_COLOR);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add username
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(TEXT_COLOR);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(usernameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Add description
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionLabel.setForeground(TEXT_COLOR);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(descriptionLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Add likes
        JLabel likesLabel = new JLabel(likes);
        likesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        likesLabel.setForeground(TEXT_COLOR);
        likesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(likesLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Add like button
        JButton likeButton = new JButton("♥️");
        likeButton.setFont(new Font("Arial", Font.BOLD, 16));
        likeButton.setForeground(Color.WHITE);
        updateLikeButtonState(likeButton, isLiked);
        likeButton.setOpaque(true);
        likeButton.setBorderPainted(false);
        likeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        likeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean wasLiked = likedPosts.contains(imageId);
                handleLikeToggle(imageId, likesLabel, likeButton, wasLiked);
                
                // Update postData with new like count for when we refresh
                try (BufferedReader reader = Files.newBufferedReader(Paths.get("img", "image_details.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("ImageID: " + imageId)) {
                            String likes = line.split(", ")[4].split(": ")[1];
                            postData[2] = "Likes: " + likes;
                            break;
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(likeButton);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(buttonPanel);
        
        imageViewPanel.add(infoPanel, BorderLayout.SOUTH);
            
        imageViewPanel.revalidate();
        imageViewPanel.repaint();
        cardLayout.show(cardPanel, "ImageView");
    }
    
    
    protected JButton createIconButton(String iconPath) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(iconScaled));
        button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    

    protected JButton createIconButton(String iconPath, String buttonType) {
        JButton button = createIconButton(iconPath);
        button.addActionListener(e -> navigateToScreen(buttonType));
        return button;
    }
    
    
    @Override
    protected void navigateToScreen(String screenType) {
        this.dispose();
        
        switch (screenType) {
            case "home":
                new QuakstagramHomeUI().setVisible(true);
                break;
            case "explore":
                new ExploreUI().setVisible(true);
                break;
            case "upload":
                new ImageUploadUI().setVisible(true);
                break;
            case "notifications":
                new NotificationsUI().setVisible(true);
                break;
            case "profile":
                String username = getCurrentUser();
                User user = new User(username);
                new InstagramProfileUI(user).setVisible(true);
                break;
            default:
                new QuakstagramHomeUI().setVisible(true);
                break;
        }
    }
}