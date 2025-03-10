package com.quackstagram.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Image Model

public class Picture {
    private String filePath;
    private String caption;
    private LocalDateTime timestamp;
    private User owner;
    private List<String> likedBy;
    
    public Picture(String filePath, String caption, User owner) {
        this.filePath = filePath;
        this.caption = caption;
        this.owner = owner;
        this.timestamp = LocalDateTime.now();
        this.likedBy = new ArrayList<>();
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public String getCaption() {
        return caption;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public int getLikesCount() {
        return likedBy.size();
    }
    
    public void addLike(String username) {
        if (!likedBy.contains(username)) {
            likedBy.add(username);
        }
    }
    
    public void removeLike(String username) {
        likedBy.remove(username);
    }
    
    public boolean isLikedBy(String username) {
        return likedBy.contains(username);
    }
    
    @Override
    public String toString() {
        return String.format("%s (by %s) - %d likes", 
                caption, owner.getUsername(), getLikesCount());
    }
}
