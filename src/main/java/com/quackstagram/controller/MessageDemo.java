package com.quackstagram.controller;

import com.quackstagram.model.User;
import com.quackstagram.util.UserSession;
import com.quackstagram.view.MessageUI;

/**
 * Demo class to show how to access and use the messaging system UI
 */
public class MessageDemo {
    
    public static void main(String[] args) {
        // Create some users
        User alice = new User("alice", "Alice's bio", "password1");
        User bob = new User("bob", "Bob's bio", "password2");
        User charlie = new User("charlie", "Charlie's bio", "password3");
        User xylo = new User("Xylo", "Xylo's bio", "password4");
        
        // Set the current user to test
        UserSession.getInstance().setCurrentUser("Xylo");
        System.out.println("Current user set to: " + UserSession.getInstance().getCurrentUsername());
        
        // Pre-populate with some messages
        alice.sendMessage(bob, "Hey Bob, how are you?");
        bob.sendMessage(alice, "I'm good, thanks for asking!");
        alice.sendMessage(bob, "Glad to hear that!");
        
        alice.sendMessage(charlie, "Hello Charlie!");
        charlie.sendMessage(alice, "Hi Alice, nice to hear from you!");
        
        xylo.sendMessage(alice, "Hello from Xylo!");
        
        // Show the messaging UI for the current user
        String currentUsername = UserSession.getInstance().getCurrentUsername();
        User currentUser = new User(currentUsername);
        MessageUI.showMessagesFor(currentUser);
    }
} 