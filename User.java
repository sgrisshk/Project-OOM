import java.util.ArrayList;
import java.util.List;

// Represents a user on Quackstagram
class User {
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
        // Initialize counts to 0
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }

    public User(String username){
        this.username = username;
        this.password = ""; // Initialize password to empty string
        this.bio = "";      // Initialize bio to empty string
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
    }

    // Add a picture to the user's profile
    public void addPicture(Picture picture) {
        pictures.add(picture);
        postsCount++;
    }

    // Message sending method
    public void sendMessage(User receiver, String content) {
        Message message = MessageService.saveMessage(this, receiver, content);
        if (message != null) {
            sentMessages.add(message);
            receiver.receiveMessage(message);
        }
    }
    
    // Method to handle receiving a message
    public void receiveMessage(Message message) {
        receivedMessages.add(message);
    }
    
    // Get all messages between this user and another user
    public List<Message> getConversationWith(User otherUser) {
        return MessageService.getConversation(this, otherUser);
    }
    
    // Getter methods for messages
    public List<Message> getSentMessages() { return sentMessages; }
    public List<Message> getReceivedMessages() { return receivedMessages; }

    // Getter methods for user details
    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public void setBio(String bio) {this.bio = bio; }
    public int getPostsCount() { return postsCount; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }
    public List<Picture> getPictures() { return pictures; }

    // Setter methods for followers and following counts
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
   public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
   public void setPostCount(int postCount) { this.postsCount = postCount;}
    // Implement the toString method for saving user information
@Override
public String toString() {
    return username + ":" + password + ":" + bio; // Correct format: username:password:bio
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    User otherUser = (User) obj;
    return username != null && username.equals(otherUser.username);
}

@Override
public int hashCode() {
    return username != null ? username.hashCode() : 0;
}

}