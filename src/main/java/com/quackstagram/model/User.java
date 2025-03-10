package com.quackstagram.model;

import java.util.ArrayList;
import java.util.List;

import com.quackstagram.service.MessageService;

// Represents a user on Quackstagram
public class User {
    private String username;
    private String bio;
    private String password;
    private int postsCount;
    private int followersCount;
    private int followingCount;
    private List<Picture> pictures;
    private List<Message> sentMessages;
    private List<Message> receivedMessages;

    public User(String username, String bio, String password) {
        this.username = username;
        this.bio = bio;
        this.password = password;
        this.pictures = new ArrayList<>();
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }

    public User(User user){
        this.username = user.username;
        this.password = ""; // Initialize password to empty string
        this.bio = "";      // Initialize bio to empty string
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
    }

    public User(String username){
        this.username = username;
        this.password = ""; // Initialize password to empty string
        this.bio = "";      // Initialize bio to empty string
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
    }

    public void addPicture(Picture picture) {
        pictures.add(picture);
        postsCount++;
    }

    public void sendMessage(User receiver, String content) {
        Message message = MessageService.saveMessage(this, receiver, content);
        if (message != null) {
            sentMessages.add(message);
            receiver.receiveMessage(message);
        }
    }
        public void receiveMessage(Message message) {
        receivedMessages.add(message);
    }
    
    // Get all messages between this user and another user
    public List<Message> getConversationWith(User otherUser) {
        return MessageService.getConversation(this, otherUser);
    }
    // Getter Setter Methods
    public List<Message> getSentMessages() { return sentMessages; }
    public List<Message> getReceivedMessages() { return receivedMessages; }
    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public void setBio(String bio) {this.bio = bio; }
    public int getPostsCount() { return postsCount; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }
    public List<Picture> getPictures() { return pictures; }
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
   public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
   public void setPostCount(int postCount) { this.postsCount = postCount;}
@Override
public String toString() {
    return username + ":" + password + ":" + bio; 
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    User otherUser = (User) obj;
    return username != null && username.equals(otherUser.username);
}


}