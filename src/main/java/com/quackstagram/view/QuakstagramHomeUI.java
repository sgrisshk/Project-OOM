package com.quackstagram.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.quackstagram.controller.PostController;
import com.quackstagram.model.Post;
import com.quackstagram.model.User;

public class QuakstagramHomeUI extends BaseUI {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel homePanel;
    private JPanel imageViewPanel;
    private PostController postController;

    // Font constants
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 12);

    public QuakstagramHomeUI() {
        super("Quackstagram Home");
        postController = PostController.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setSize(QuackstagramStyles.WIDTH, QuackstagramStyles.HEIGHT);
        setMinimumSize(new Dimension(QuackstagramStyles.WIDTH, QuackstagramStyles.HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        setupPanels();
        setupHeader();
        setupNavigation();
    }

    private void setupPanels() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(QuackstagramStyles.BACKGROUND_COLOR);
        
        homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(QuackstagramStyles.BACKGROUND_COLOR);
        
        imageViewPanel = new JPanel(new BorderLayout());
        imageViewPanel.setBackground(QuackstagramStyles.BACKGROUND_COLOR);

        setupHomePanel();
        cardPanel.add(homePanel, "Home");
        cardPanel.add(imageViewPanel, "ImageView");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "Home");
    }

    private void setupHeader() {
        // message button to open DMs
        JButton messageButton = createIconButton("img/icons/message.png");
        messageButton.addActionListener(e -> {
            // get current user and show their messages
            String username = com.quackstagram.util.UserSession.getInstance().getCurrentUsername();
            MessageUI.showMessagesFor(new User(username));
        });

        // create the header with app name
        JPanel headerPanel = createHeaderPanel("Quackstagram");
        
        // make it blue like Instagram
        headerPanel.setBackground(QuackstagramStyles.HEADER_COLOR);
        headerPanel.setForeground(Color.WHITE);
        
        // fix layout issues - had problems with this before
        headerPanel.setLayout(new BorderLayout());
        
        // Add title in center with duck emoji lol
        JLabel titleLabel = new JLabel("Quackstagram 🐥", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));  // bigger font for title
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Add messaging button on right
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(QuackstagramStyles.HEADER_COLOR);
        rightPanel.add(messageButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }

    private void setupNavigation() {
        add(createNavigationPanel(), BorderLayout.SOUTH);
    }

    private void setupHomePanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(QuackstagramStyles.BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(QuackstagramStyles.BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Make sure we get ALL posts (including from followed users)
        List<Post> posts = postController.getPostsForCurrentUser();
        
        if (posts.isEmpty()) {
            JLabel emptyLabel = createStyledLabel("No posts to display. Follow users to see their posts!", 
                BODY_FONT, QuackstagramStyles.TEXT_COLOR, Component.CENTER_ALIGNMENT);
            contentPanel.add(wrapInFlowPanel(emptyLabel, QuackstagramStyles.BACKGROUND_COLOR, FlowLayout.CENTER));
        } else {
            for (Post post : posts) {
                createPostItem(contentPanel, post);
            }
        }
        
        homePanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void createPostItem(JPanel panel, Post post) {
        // First create the panel
        JPanel postPanel = new JPanel();
        // Then set its layout
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        // Then style it
        postPanel.setBackground(QuackstagramStyles.POST_BACKGROUND);
        postPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        postPanel.setAlignmentX(CENTER_ALIGNMENT);
        
        // Username
        JLabel usernameLabel = createStyledLabel(post.getUsername(), TITLE_FONT, 
            QuackstagramStyles.TEXT_COLOR, Component.LEFT_ALIGNMENT);
        postPanel.add(wrapInFlowPanel(usernameLabel, QuackstagramStyles.POST_BACKGROUND, FlowLayout.LEFT));
        postPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Image
        JLabel imageLabel = createImageLabel(post);
        postPanel.add(wrapInFlowPanel(imageLabel, QuackstagramStyles.POST_BACKGROUND, FlowLayout.CENTER));
        postPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Description
        if (!post.getDescription().isEmpty()) {
            JLabel descriptionLabel = createStyledLabel(post.getDescription(), BODY_FONT,
                QuackstagramStyles.TEXT_COLOR, Component.LEFT_ALIGNMENT);
            postPanel.add(wrapInFlowPanel(descriptionLabel, QuackstagramStyles.POST_BACKGROUND, FlowLayout.LEFT));
        }
        
        // Likes
        JLabel likesLabel = createStyledLabel("Likes: " + post.getLikes(), BODY_FONT,
            QuackstagramStyles.TEXT_COLOR, Component.LEFT_ALIGNMENT);
        postPanel.add(wrapInFlowPanel(likesLabel, QuackstagramStyles.POST_BACKGROUND, FlowLayout.LEFT));
        
        // Like button
        JButton likeButton = createLikeButton(post, likesLabel);
        postPanel.add(wrapInFlowPanel(likeButton, QuackstagramStyles.POST_BACKGROUND, FlowLayout.LEFT));
        
        // Make image clickable
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayImage(post);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        
        panel.add(postPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private JLabel createImageLabel(Post post) {
        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(QuackstagramStyles.IMAGE_WIDTH, QuackstagramStyles.IMAGE_HEIGHT));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        try {
            BufferedImage originalImage = ImageIO.read(new File(post.getImagePath()));
            BufferedImage croppedImage = originalImage.getSubimage(0, 0, 
                Math.min(originalImage.getWidth(), QuackstagramStyles.IMAGE_WIDTH), 
                Math.min(originalImage.getHeight(), QuackstagramStyles.IMAGE_HEIGHT));
            ImageIcon imageIcon = new ImageIcon(croppedImage);
            imageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            imageLabel.setText("Image not found");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        return imageLabel;
    }

    private JButton createLikeButton(Post post, JLabel likesLabel) {
        JButton likeButton = new JButton("♥️");
        likeButton.setFont(new Font("Arial", Font.BOLD, 16));
        likeButton.setForeground(Color.WHITE);
        updateLikeButtonState(likeButton, post.isLiked());
        
        likeButton.setOpaque(true);
        likeButton.setBorderPainted(false);
        likeButton.setFocusPainted(false);
        likeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        likeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        likeButton.addActionListener(e -> {
            postController.toggleLike(post);
            updateLikeButtonState(likeButton, post.isLiked());
            likesLabel.setText("Likes: " + post.getLikes());
        });
        
        return likeButton;
    }

    private void updateLikeButtonState(JButton likeButton, boolean isLiked) {
        likeButton.setBackground(isLiked ? 
            QuackstagramStyles.LIKE_BUTTON_COLOR : 
            QuackstagramStyles.UNLIKE_BUTTON_COLOR);
    }

    private void displayImage(Post post) {
        imageViewPanel.removeAll();
        
        // Back button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(QuackstagramStyles.HEADER_COLOR);
        backButton.setBackground(QuackstagramStyles.BACKGROUND_COLOR);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "Home"));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(QuackstagramStyles.BACKGROUND_COLOR);
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        imageViewPanel.add(topPanel, BorderLayout.NORTH);
        
        // Full image
        JLabel fullImageLabel = new JLabel();
        fullImageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        try {
            BufferedImage originalImage = ImageIO.read(new File(post.getImagePath()));
            int displayWidth = QuackstagramStyles.WIDTH - 40;
            int displayHeight = QuackstagramStyles.HEIGHT - 200;
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
            fullImageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (IOException ex) {
            fullImageLabel.setText("Image not found");
            fullImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        JPanel imageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imageContainer.setBackground(QuackstagramStyles.BACKGROUND_COLOR);
        imageContainer.add(fullImageLabel);
        imageViewPanel.add(imageContainer, BorderLayout.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(QuackstagramStyles.BACKGROUND_COLOR);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add details
        addInfoPanelDetails(infoPanel, post);
        
        imageViewPanel.add(infoPanel, BorderLayout.SOUTH);
        imageViewPanel.revalidate();
        imageViewPanel.repaint();
        cardLayout.show(cardPanel, "ImageView");
    }

    private void addInfoPanelDetails(JPanel infoPanel, Post post) {
        // Username
        infoPanel.add(createStyledLabel(post.getUsername(), TITLE_FONT,
            QuackstagramStyles.TEXT_COLOR, Component.LEFT_ALIGNMENT));
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Description
        if (!post.getDescription().isEmpty()) {
            infoPanel.add(createStyledLabel(post.getDescription(), BODY_FONT,
                QuackstagramStyles.TEXT_COLOR, Component.LEFT_ALIGNMENT));
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Likes
        JLabel likesLabel = createStyledLabel("Likes: " + post.getLikes(), BODY_FONT,
            QuackstagramStyles.TEXT_COLOR, Component.LEFT_ALIGNMENT);
        infoPanel.add(likesLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Like button
        JButton likeButton = createLikeButton(post, likesLabel);
        JPanel buttonPanel = wrapInFlowPanel(likeButton, QuackstagramStyles.BACKGROUND_COLOR, FlowLayout.LEFT);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(buttonPanel);
    }

    private JLabel createStyledLabel(String text, Font font, Color color, float alignment) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(alignment);
        return label;
    }

    private JPanel createStyledPanel(LayoutManager layout, Color background) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(background);
        return panel;
    }

    private JPanel wrapInFlowPanel(Component component, Color background, int alignment) {
        JPanel panel = createStyledPanel(new FlowLayout(alignment), background);
        panel.add(component);
        return panel;
    }

    private BufferedImage scaleImage(BufferedImage original, int targetWidth, int targetHeight) {
        double aspectRatio = (double) original.getWidth() / original.getHeight();
        int scaledWidth, scaledHeight;
        
        if (targetWidth / aspectRatio <= targetHeight) {
            scaledWidth = targetWidth;
            scaledHeight = (int) (targetWidth / aspectRatio);
        } else {
            scaledHeight = targetHeight;
            scaledWidth = (int) (targetHeight * aspectRatio);
        }
        
        Image scaled = original.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        BufferedImage result = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        result.getGraphics().drawImage(scaled, 0, 0, null);
        return result;
    }

    protected JButton createIconButton(String iconPath) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(QuackstagramStyles.NAV_ICON_SIZE, QuackstagramStyles.NAV_ICON_SIZE, Image.SCALE_SMOOTH);
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
        com.quackstagram.util.NavigationUtils.navigateTo(screenType);
    }

    // Add a method to refresh the home feed
    public void refreshFeed() {
        // Clear the home panel
        homePanel.removeAll();
        // Reinitialize the home panel with updated posts
        setupHomePanel();
        // Repaint and revalidate
        homePanel.revalidate();
        homePanel.repaint();
    }
}