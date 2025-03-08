package com.quackstagram.view;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

import com.quackstagram.model.Message;
import com.quackstagram.model.User;
import com.quackstagram.service.MessageService;

/**
 * A simple UI for the messaging system in Quackstagram
 */
public class MessageUI extends JFrame {
    private User currentUser;
    private User selectedReceiver;
    
    private JList<String> contactsList;
    private DefaultListModel<String> contactsModel;
    private JTextArea conversationArea;
    private JTextField messageField;
    private JButton sendButton;
    private String followingPath = "data/following.txt";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public MessageUI(User currentUser) {
        this.currentUser = currentUser;
        
        // Set up the frame
        setTitle("Quackstagram Messages - " + currentUser.getUsername());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Contacts Panel
        JPanel contactsPanel = new JPanel(new BorderLayout());
        contactsPanel.setBorder(BorderFactory.createTitledBorder("Contacts"));
        contactsModel = new DefaultListModel<>();
        contactsList = new JList<>(contactsModel);
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane contactsScroll = new JScrollPane(contactsList);
        contactsPanel.add(contactsScroll, BorderLayout.CENTER);
        
        // Conv Panel
        JPanel conversationPanel = new JPanel(new BorderLayout());
        conversationPanel.setBorder(BorderFactory.createTitledBorder("Conversation"));
        conversationArea = new JTextArea();
        conversationArea.setEditable(false);
        JScrollPane conversationScroll = new JScrollPane(conversationArea);
        conversationPanel.add(conversationScroll, BorderLayout.CENTER);
        
        // Message Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        conversationPanel.add(inputPanel, BorderLayout.SOUTH);
        add(contactsPanel, BorderLayout.WEST);
        add(conversationPanel, BorderLayout.CENTER);
        
        // Add event listeners
        contactsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && contactsList.getSelectedIndex() != -1) {
                String username = contactsList.getSelectedValue();
                selectedReceiver = new User(username);
                refreshConversation();
            }
        });
        
        sendButton.addActionListener(e -> {
            if (selectedReceiver != null && !messageField.getText().trim().isEmpty()) {
                currentUser.sendMessage(selectedReceiver, messageField.getText().trim());
                messageField.setText("");
                refreshConversation();
            }
        });
        
        // Initialize contacts from following relationships
        addContacts();
        
        // Also add contacts from message history if any
        addContactsFromMessages();
    }
    
    private void addContacts() {
        // Read contacts from following.txt file
        try (BufferedReader reader = new BufferedReader(new FileReader(followingPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the current user's username
                if (line.startsWith(currentUser.getUsername() + ":")) {
                    // Extract the users that the current user is following
                    String[] parts = line.split(":");
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        String[] following = parts[1].split(",");
                        for (String username : following) {
                            contactsModel.addElement(username.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading following.txt: " + e.getMessage());
        }
    }
    
    /**
     * Add contacts from message history
     */
    private void addContactsFromMessages() {
        List<String> conversationUsers = MessageService.getUserConversations(currentUser);
        for (String username : conversationUsers) {
            if (!isInContactsList(username)) {
                contactsModel.addElement(username);
            }
        }
    }
    
    /**
     * Check if a username is already in the contacts list
     */
    private boolean isInContactsList(String username) {
        for (int i = 0; i < contactsModel.getSize(); i++) {
            if (contactsModel.getElementAt(i).equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Refresh the conversation display with the selected user
     */
    private void refreshConversation() {
        if (selectedReceiver == null) return;
        
        conversationArea.setText("");
        List<Message> conversation = currentUser.getConversationWith(selectedReceiver);
        
        for (Message message : conversation) {
            conversationArea.append(formatMessage(message) + "\n");
            
            // Mark messages from the other user as read
            if (message.getSender().getUsername().equals(selectedReceiver.getUsername()) 
                && !message.isRead()) {
                MessageService.markMessageAsRead(message.getMessageId());
            }
        }
        
        // Scroll to the bottom
        conversationArea.setCaretPosition(conversationArea.getDocument().getLength());
    }
    
    /**
     * Format a message for display
     */
    private String formatMessage(Message message) {
        String sender = message.getSender().getUsername();
        String time = message.getTimestamp().format(TIME_FORMATTER);
        String content = message.getContent();
        String readStatus = message.isRead() ? "✓" : "";
        
        // Format differently based on whether it's sent or received
        if (sender.equals(currentUser.getUsername())) {
            return "[" + time + "] You: " + content + " " + readStatus;
        } else {
            return "[" + time + "] " + sender + ": " + content;
        }
    }
    
    /**
     * Update the contacts list with actual users
     * @param users List of users to display as contacts
     */
    public void setContacts(List<User> users) {
        contactsModel.clear();
        for (User user : users) {
            if (!user.equals(currentUser)) {
                contactsModel.addElement(user.getUsername());
            }
        }
    }
    
    /**
     * Static method to show the messaging UI for a user
     */
    public static void showMessagesFor(User user) {
        MessageUI ui = new MessageUI(user);
        ui.setVisible(true);
    }
} 