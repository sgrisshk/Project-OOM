package com.quackstagram.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.quackstagram.model.Message;
import com.quackstagram.model.User;

//Service to handle messages between users

public class MessageService {
    private static final String MESSAGES_FILE = "data/Messages.txt";
    private static int nextMessageId = 1;
    
    private static List<Message> allMessages = new ArrayList<>();
    
    static {
        // Initialize nextMessageId by finding the highest ID in the file
        loadNextMessageId();
    }
    
    private MessageService() {
        // Private constructor so nobody can create instances
    }
    
    // Load the next available message ID from the file

    private static void loadNextMessageId() {
        File messagesFile = new File(MESSAGES_FILE);
        if (!messagesFile.exists()) {
            createMessagesFile();
            return; // nextMessageId stays at 1
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            int highestId = 0;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 1) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        if (id > highestId) {
                            highestId = id;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
            
            nextMessageId = highestId + 1;
        } catch (IOException e) {
            System.err.println("Error loading message IDs: " + e.getMessage());
        }
    }
    
    // Create the messages file with a header row
     
    private static void createMessagesFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(MESSAGES_FILE))) {
            writer.println("# messageID|timestamp|sender|receiver|content|isRead");
            writer.println("# Format: INTEGER|ISO_DATE_TIME|STRING|STRING|STRING|BOOLEAN");
            writer.println();
        } catch (IOException e) {
            System.err.println("Error creating messages file: " + e.getMessage());
        }
    }
    
    // register a new message
    public static Message saveMessage(User sender, User receiver, String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        
        Message message = new Message(sender, receiver, content);
        allMessages.add(message);
        return message;
    }
    
    // get the conversation between two users

    public static List<Message> getConversation(User user1, User user2) {
        // get the chat between two users
        return allMessages.stream()
                .filter(m -> (m.getSender().equals(user1) && m.getReceiver().equals(user2)) || 
                             (m.getSender().equals(user2) && m.getReceiver().equals(user1)))
                .collect(Collectors.toList());
    }
    
    // get all the messages
    public static List<Message> getAllMessages() {
        return new ArrayList<>(allMessages);
    }
    
    // Changes message to read=true
    public static void markMessageAsRead(String messageId) {
        for (Message message : allMessages) {
            if (message.getMessageId().equals(messageId)) {
                message.setRead(true);
                break;
            }
        }
    }
    
    // Find who you've been talking to
    public static List<String> getUserConversations(User user) {
        List<String> conversationUsers = new ArrayList<>();
        File messagesFile = new File(MESSAGES_FILE);
        
        // No file = no messages
        if (!messagesFile.exists()) {
            return conversationUsers;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    String sender = parts[2];
                    String receiver = parts[3];
                    
                    if (sender.equals(user.getUsername()) && !conversationUsers.contains(receiver)) {
                        conversationUsers.add(receiver);
                    } else if (receiver.equals(user.getUsername()) && !conversationUsers.contains(sender)) {
                        conversationUsers.add(sender);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding conversations: " + e.getMessage());
        }
        
        return conversationUsers;
    }
}
