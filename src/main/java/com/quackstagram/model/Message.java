package com.quackstagram.model;

import java.time.LocalDateTime;

/**
 * Représente un message entre deux utilisateurs
 */
public class Message {
    private User sender;
    private User receiver;
    private String content;
    private LocalDateTime timestamp;
    private boolean read = false;
    private String messageId;
    
    public Message(User sender, User receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.messageId = generateMessageId();
    }
    
    private String generateMessageId() {
        // Simple message ID generation based on timestamp and users
        return sender.getUsername() + "_" + receiver.getUsername() + "_" + timestamp.toString().replace(":", "-");
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public User getSender() {
        return sender;
    }
    
    public User getReceiver() {
        return receiver;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s -> %s: %s", 
                timestamp, sender.getUsername(), receiver.getUsername(), content);
    }
} 