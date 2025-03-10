package com.quackstagram.view;

import java.awt.*;
import javax.swing.*;
import com.quackstagram.model.User;


// Main application UI with access to the messaging system

public class MainAppUI extends JFrame {
    private User currentUser;
    
    public MainAppUI(User user) {
        this.currentUser = user;
        
        setTitle("Quackstagram - " + user.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
         JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton homeButton = new JButton("Home");
        JButton profileButton = new JButton("Profile");
        JButton messagesButton = new JButton("Messages");
        
        toolbar.add(homeButton);
        toolbar.add(profileButton);
        toolbar.add(messagesButton);
        
        add(toolbar, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(new JLabel("Welcome to Quackstagram, " + user.getUsername() + "!"));
        add(contentPanel, BorderLayout.CENTER);
        
        messagesButton.addActionListener(e -> {
            MessageUI.showMessagesFor(currentUser);
        });
    }
    
   
      //Start the application with a specific user
     
    public static void startApp(User user) {
        MainAppUI app = new MainAppUI(user);
        app.setVisible(true);
    }
    
    // Main method to start the application
     
    public static void main(String[] args) {
        User demoUser = new User("demo_user", "This is a demo user", "password");
        
        User user1 = new User("user1", "User 1's bio", "pass1");
        User user2 = new User("user2", "User 2's bio", "pass2");
        demoUser.sendMessage(user1, "Hello User 1!");
        user1.sendMessage(demoUser, "Hi Demo User!");
        demoUser.sendMessage(user2, "Hey User 2, how's it going?");
        SwingUtilities.invokeLater(() -> startApp(demoUser));
    }
} 