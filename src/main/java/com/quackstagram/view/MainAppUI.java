package com.quackstagram.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

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
} 