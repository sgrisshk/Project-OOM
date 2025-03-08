import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for handling message operations in Quackstagram
 */
public class MessageService {
    private static final String MESSAGES_FILE = "data/Messages.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static int nextMessageId = 1;
    
    static {
        // Initialize nextMessageId by finding the highest ID in the file
        loadNextMessageId();
    }
    
    /**
     * Load the next available message ID from the file
     */
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
    
    /**
     * Create the messages file with a header row
     */
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
    
    /**
     * Save a new message to the file
     */
    public static Message saveMessage(User sender, User receiver, String content) {
        // Create the message with a new ID
        Message message = new Message(sender, receiver, content);
        message.setMessageId(nextMessageId);
        
        // Format the message record
        String messageRecord = String.format("%d|%s|%s|%s|%s|%b",
                message.getMessageId(),
                message.getTimestamp().format(DATE_FORMATTER),
                sender.getUsername(),
                receiver.getUsername(),
                content.replace("|", "&#124;"), // Escape pipe characters in content
                message.isRead());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(MESSAGES_FILE, true))) {
            writer.println(messageRecord);
            nextMessageId++;
            return message;
        } catch (IOException e) {
            System.err.println("Error saving message: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get the conversation between two users
     */
    public static List<Message> getConversation(User user1, User user2) {
        List<Message> conversation = new ArrayList<>();
        File messagesFile = new File(MESSAGES_FILE);
        
        if (!messagesFile.exists()) {
            return conversation; // Empty list if file doesn't exist
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines and comment lines
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    String sender = parts[2];
                    String receiver = parts[3];
                    
                    // Check if this message is part of the conversation between the two users
                    if ((sender.equals(user1.getUsername()) && receiver.equals(user2.getUsername())) ||
                        (sender.equals(user2.getUsername()) && receiver.equals(user1.getUsername()))) {
                        
                        // Parse the message data
                        int id = Integer.parseInt(parts[0]);
                        LocalDateTime timestamp = LocalDateTime.parse(parts[1], DATE_FORMATTER);
                        String content = parts[4].replace("&#124;", "|"); // Unescape pipe characters
                        boolean isRead = Boolean.parseBoolean(parts[5]);
                        
                        // Create a message object
                        Message message = new Message(
                            id,
                            new User(sender),
                            new User(receiver),
                            content,
                            timestamp,
                            isRead
                        );
                        
                        conversation.add(message);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading conversation: " + e.getMessage());
        }
        
        // Sort the conversation by timestamp
        conversation.sort(Comparator.comparing(Message::getTimestamp));
        
        return conversation;
    }
    
    /**
     * Mark a message as read
     */
    public static boolean markMessageAsRead(int messageId) {
        List<String> allLines = new ArrayList<>();
        boolean found = false;
        
        // Read all lines from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    allLines.add(line); // Keep comments and empty lines as is
                    continue;
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && Integer.parseInt(parts[0]) == messageId) {
                    // This is the message we want to mark as read
                    parts[5] = "true";
                    allLines.add(String.join("|", parts));
                    found = true;
                } else {
                    allLines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading messages file: " + e.getMessage());
            return false;
        }
        
        if (found) {
            // Write the updated lines back to the file
            try (PrintWriter writer = new PrintWriter(new FileWriter(MESSAGES_FILE))) {
                for (String line : allLines) {
                    writer.println(line);
                }
                return true;
            } catch (IOException e) {
                System.err.println("Error updating messages file: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Get all conversations for a user
     */
    public static List<String> getUserConversations(User user) {
        List<String> conversationUsers = new ArrayList<>();
        File messagesFile = new File(MESSAGES_FILE);
        
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
