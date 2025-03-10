package com.quackstagram.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.quackstagram.controller.PostController;
import com.quackstagram.model.Post;
import com.quackstagram.model.User;

public class ExploreUI extends BaseUI {
    private static final int IMAGE_SIZE = WIDTH / 3;
    private final PostController postController;

    public ExploreUI() {
        super("Explore");
        this.postController = PostController.getInstance();
        
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        add(createHeaderPanel("Explore"), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel createMainContentPanel() {
        // Search bar at the top
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(" Search Users");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchField.getPreferredSize().height));

        JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 2, 2));

        File imageDir = new File("img/uploaded");
        if (imageDir.exists() && imageDir.isDirectory()) {
            File[] imageFiles = imageDir.listFiles((dir, name) -> name.matches(".*\\.(png|jpg|jpeg)"));
            if (imageFiles != null) {
                for (File imageFile : imageFiles) {
                    ImageIcon imageIcon = new ImageIcon(new ImageIcon(imageFile.getPath())
                            .getImage()
                            .getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH));
                    JLabel imageLabel = new JLabel(imageIcon);
                    imageLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            String imageId = imageFile.getName().split("\\.")[0];
                            displayImage(imageId);
                        }
                    });
                    imageGridPanel.add(imageLabel);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(imageGridPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.add(searchPanel);
        mainContentPanel.add(scrollPane);
        return mainContentPanel;
    }

    private void displayImage(String imageId) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        add(createHeaderPanel("Explore Page"), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.SOUTH);

        // Get post details using PostController instead of direct file reading
        Post post = postController.getPostById(imageId);
        if (post == null) {
            JLabel errorLabel = new JLabel("Image not found");
            add(errorLabel, BorderLayout.CENTER);
            revalidate();
            repaint();
            return;
        }

        String username = post.getUsername();
        String bio = post.getDescription();
        String timeSincePosting = postController.getTimeSincePosting(post);
        int likes = post.getLikes();
        String imagePath = post.getImagePath();

        // Top panel for username and time since posting
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton usernameLabel = new JButton(username);
        JLabel timeLabel = new JLabel(timeSincePosting);
        timeLabel.setHorizontalAlignment(JLabel.RIGHT);
        topPanel.add(usernameLabel, BorderLayout.WEST);
        topPanel.add(timeLabel, BorderLayout.EAST);

        // Prepare the image for display
        JLabel imageLabel = createImageLabel(imagePath);

        // Bottom panel for bio and likes
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JTextArea bioTextArea = new JTextArea(bio);
        bioTextArea.setEditable(false);
        
        // Create like button and panel
        JPanel likesPanel = new JPanel(new BorderLayout());
        JButton likeButton = new JButton(post.isLiked() ? "Unlike" : "Like");
        JLabel likesLabel = new JLabel("Likes: " + likes);
        
        likeButton.addActionListener(e -> {
            postController.toggleLike(post);
            likeButton.setText(post.isLiked() ? "Unlike" : "Like");
            likesLabel.setText("Likes: " + post.getLikes());
        });
        
        likesPanel.add(likeButton, BorderLayout.WEST);
        likesPanel.add(likesLabel, BorderLayout.EAST);
        
        bottomPanel.add(bioTextArea, BorderLayout.CENTER);
        bottomPanel.add(likesPanel, BorderLayout.SOUTH);

        // Panel for the back button
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(WIDTH-20, backButton.getPreferredSize().height));
        backButtonPanel.add(backButton);

        backButton.addActionListener(e -> {
            getContentPane().removeAll();
            add(createMainContentPanel(), BorderLayout.CENTER);
            add(createNavigationPanel(), BorderLayout.SOUTH);
            revalidate();
            repaint();
        });

        usernameLabel.addActionListener(e -> {
            User user = new User(username);
            InstagramProfileUI profileUI = new InstagramProfileUI(user);
            profileUI.setVisible(true);
            dispose();
        });

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(topPanel, BorderLayout.NORTH);
        containerPanel.add(imageLabel, BorderLayout.CENTER);
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(backButtonPanel, BorderLayout.NORTH);
        add(containerPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JLabel createImageLabel(String imagePath) {
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            imageLabel.setIcon(new ImageIcon(originalImage));
        } catch (IOException ex) {
            imageLabel.setText("Image not found");
        }
        return imageLabel;
    }
}
