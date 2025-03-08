import java.time.LocalDateTime;

// Represents a message between two users
public class Message {
    private int messageId;
    private User sender;
    private User receiver;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;
    
    // Constructor for new messages
    public Message(User sender, User receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.messageId = 0; // Will be set by MessageService
    }
    
    // Constructor for loading messages from file
    public Message(int messageId, User sender, User receiver, String content, LocalDateTime timestamp, boolean isRead) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }
    
    // Getter methods
    public int getMessageId() { return messageId; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    
    // Setter methods
    public void setMessageId(int messageId) { this.messageId = messageId; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
    
    @Override
    public String toString() {
        return "From: " + sender.getUsername() + 
               " To: " + receiver.getUsername() + 
               " [" + timestamp + "]: " + content;
    }
} 