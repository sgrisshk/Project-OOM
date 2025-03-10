package com.quackstagram.view;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.quackstagram.model.Message;
import com.quackstagram.model.User;
import com.quackstagram.network.MessageClient;
import com.quackstagram.service.MessageService;


 // A simple UI for the messaging system in Quackstagram
 
public class MessageUI extends JFrame {
    private User currentUser;
    private User selectedReceiver;
    private MessageClient messageClient;
    
    private JList<String> contactsList;
    private DefaultListModel<String> contactsModel;
    private JTextArea conversationArea;
    private JTextField messageField;
    private JButton sendButton;
    private static final String FOLLOWING_PATH = "data/following.txt";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public MessageUI(User currentUser) {
        this.currentUser = currentUser;
        initializeNetworking();
        setTitle("Quackstagram Messages - " + currentUser.getUsername());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel contactsPanel = new JPanel(new BorderLayout());
        contactsPanel.setBorder(BorderFactory.createTitledBorder("Contacts"));
        contactsModel = new DefaultListModel<>();
        contactsList = new JList<>(contactsModel);
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane contactsScroll = new JScrollPane(contactsList);
        contactsPanel.add(contactsScroll, BorderLayout.CENTER);
        JPanel conversationPanel = new JPanel(new BorderLayout());
        conversationPanel.setBorder(BorderFactory.createTitledBorder("Conversation"));
        conversationArea = new JTextArea();
        conversationArea.setEditable(false);
        JScrollPane conversationScroll = new JScrollPane(conversationArea);
        conversationPanel.add(conversationScroll, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        conversationPanel.add(inputPanel, BorderLayout.SOUTH);
        add(contactsPanel, BorderLayout.WEST);
        add(conversationPanel, BorderLayout.CENTER);
            contactsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && contactsList.getSelectedIndex() != -1) {
                String username = contactsList.getSelectedValue();
                selectedReceiver = new User(username);
                refreshConversation();
            }
        });
        
        sendButton.addActionListener(e -> {
            sendMessage();
        });
        
        addContacts();
        addContactsFromMessages();
    }
    
    private void initializeNetworking() {
        messageClient = new MessageClient(currentUser);
        messageClient.addListener((sender, content) -> {
            // Update conversation if message is from current chat
            if (selectedReceiver != null && sender.equals(selectedReceiver.getUsername())) {
                SwingUtilities.invokeLater(() -> {
                    conversationArea.append(String.format("[%s] %s: %s\n", 
                        LocalDateTime.now().format(TIME_FORMATTER),
                        sender, content));
                    conversationArea.setCaretPosition(conversationArea.getDocument().getLength());
                });
            }
        });
        messageClient.connect();
    }

    @Override
    public void dispose() {
        if (messageClient != null) {
            messageClient.disconnect();
        }
        super.dispose();
    }

    private void sendMessage() {
        if (selectedReceiver != null && !messageField.getText().trim().isEmpty()) {
            String content = messageField.getText().trim();
            // Send via network
            messageClient.sendMessage(selectedReceiver.getUsername(), content);
            // Store in local database
            currentUser.sendMessage(selectedReceiver, content);
            messageField.setText("");
            refreshConversation();
        }
    }
    
    private void addContacts() {
        // Read contacts from following.txt file
        try (BufferedReader reader = new BufferedReader(new FileReader(FOLLOWING_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the current user's username
                if (line.startsWith(currentUser.getUsername() + ":")) {
                    // Extract the users that the current user is following
                    String[] parts = line.split(":");
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        String[] following = parts[1].split(";");
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
    
     //Add contacts from message history

    private void addContactsFromMessages() {
        List<String> conversationUsers = MessageService.getUserConversations(currentUser);
        for (String username : conversationUsers) {
            if (!isInContactsList(username)) {
                contactsModel.addElement(username);
            }
        }
    }
    
    //Check if a username is already in the contacts list
     
    private boolean isInContactsList(String username) {
        for (int i = 0; i < contactsModel.getSize(); i++) {
            if (contactsModel.getElementAt(i).equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    //Refresh the conversation display with the selected user
  
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
    
    //Format a message for display
  
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
    

    public void setContacts(List<User> users) {
        contactsModel.clear();
        for (User user : users) {
            if (!user.equals(currentUser)) {
                contactsModel.addElement(user.getUsername());
            }
        }
    }
    
    // Static method to show the messaging UI for a user
     
    public static void showMessagesFor(User user) {
        MessageUI ui = new MessageUI(user);
        ui.setVisible(true);
    }
} 