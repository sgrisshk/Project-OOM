package com.quackstagram.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.quackstagram.model.Post;
import com.quackstagram.util.UserSession;

public class PostController {
    private static PostController instance;
    private static final String IMAGE_DETAILS_PATH = "img/image_details.txt";
    private static final String NOTIFICATIONS_PATH = "data/notifications.txt";
    private static final String FOLLOWING_PATH = "data/following.txt";
    private static final String IMAGE_UPLOAD_DIR = "img/uploaded/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final Set<String> likedPostIds = new HashSet<>();

    private PostController() {
        loadLikedPosts();
    }
    
    public static PostController getInstance() {
        if (instance == null) {
            instance = new PostController();
        }
        return instance;
    }
    
    // Post Retrieval Methods
    public List<Post> getPostsForCurrentUser() {
        String currentUser = getCurrentUser();
        String followedUsers = getFollowedUsers(currentUser);
        return getPosts(line -> followedUsers.contains(extractField(line, "Username:")));
    }
    
    public List<Post> getAllPosts() {
        return getPosts(line -> true);
    }
    
    public Post getPostById(String postId) {
        List<Post> posts = getPosts(line -> line.contains("ImageID: " + postId));
        return posts.isEmpty() ? null : posts.get(0);
    }
    
    // Main method to get posts with a filter
    private List<Post> getPosts(PostLineFilter filter) {
        List<Post> posts = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(IMAGE_DETAILS_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    if (line.trim().isEmpty() || !filter.accept(line)) {
                        continue;
                    }
                    
                    Post post = parsePostFromLine(line);
                    if (post != null) {
                        post.setLiked(isPostLiked(post.getId()));
                        posts.add(post);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return posts;
    }
    
    // Parse a post from a line in the image_details.txt file
    private Post parsePostFromLine(String line) {
        String imageId = extractField(line, "ImageID:");
        String username = extractField(line, "Username:");
        String description = extractField(line, "Description:");
        if (description.isEmpty()) {
            description = extractField(line, "Bio:");
        }
        
        String timestamp = extractField(line, "Timestamp:");
        if (timestamp.isEmpty()) {
            timestamp = "2023-01-01 00:00:00";
        }
        
        int likes = 0;
        try {
            likes = Integer.parseInt(extractField(line, "Likes:"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid likes format");
        }
        
        // Skip if we couldn't extract the essential fields
        if (imageId.isEmpty() || username.isEmpty()) {
            return null;
        }
        
        String imagePath = IMAGE_UPLOAD_DIR + imageId + ".png";
        
        LocalDateTime createdAt;
        try {
            createdAt = LocalDateTime.parse(timestamp, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid timestamp format: " + timestamp);
            createdAt = LocalDateTime.now();
        }
        
        return Post.builder()
            .id(imageId)
            .username(username)
            .description(description)
            .imagePath(imagePath)
            .likes(likes)
            .createdAt(createdAt)
            .build();
    }
    
    // Extract a field value from a line
    private String extractField(String line, String fieldName) {
        if (line == null || !line.contains(fieldName)) {
            return "";
        }
        
        String[] details = line.split(", ");
        for (String detail : details) {
            detail = detail.trim();
            if (detail.startsWith(fieldName)) {
                String[] parts = detail.split(": ", 2);
                if (parts.length > 1) {
                    return parts[1].trim();
                }
            }
        }
        return "";
    }
    
    // Like/Unlike functionality
    public void toggleLike(Post post) {
        boolean wasLiked = isPostLiked(post.getId());
        Path detailsPath = Paths.get(IMAGE_DETAILS_PATH);
        StringBuilder newContent = new StringBuilder();
        boolean updated = false;
        String currentUser = getCurrentUser();
        
        try (BufferedReader reader = Files.newBufferedReader(detailsPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ImageID: " + post.getId())) {
                    String[] parts = line.split(", ");
                    if (wasLiked) {
                        post.decrementLikes();
                        removeLikedPost(post.getId());
                    } else {
                        post.incrementLikes();
                        addLikedPost(post.getId());
                    }
                    // Update likes count in the line
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].startsWith("Likes:")) {
                            parts[i] = "Likes: " + post.getLikes();
                            break;
                        }
                    }
                    line = String.join(", ", parts);
                    updated = true;
                }
                newContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (updated) {
            try (BufferedWriter writer = Files.newBufferedWriter(detailsPath)) {
                writer.write(newContent.toString());
                updateNotifications(post, wasLiked, currentUser);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Liked posts management
    private void updateNotifications(Post post, boolean wasLiked, String currentUser) throws IOException {
        Path notificationsPath = Paths.get(NOTIFICATIONS_PATH);
        
        if (!wasLiked) {
            // Add notification for new like
            String notification = String.format("%s; %s; %s; %s\n", 
                post.getUsername(), currentUser, post.getId(), 
                LocalDateTime.now().format(DATE_FORMATTER));
                
            Files.write(notificationsPath, 
                notification.getBytes(), 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } else {
            // Remove notification for unlike
            List<String> lines = Files.readAllLines(notificationsPath);
            List<String> updatedLines = new ArrayList<>();
            
            for (String line : lines) {
                String[] parts = line.split("; ");
                if (!(parts.length >= 3 && parts[1].equals(currentUser) && parts[2].equals(post.getId()))) {
                    updatedLines.add(line);
                }
            }
            
            Files.write(notificationsPath, updatedLines);
        }
    }
    
    private void loadLikedPosts() {
        likedPostIds.clear();
        String currentUser = getCurrentUser();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(NOTIFICATIONS_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("; ");
                if (parts.length >= 3 && parts[1].equals(currentUser)) {
                    likedPostIds.add(parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isPostLiked(String postId) {
        return likedPostIds.contains(postId);
    }
    
    private void addLikedPost(String postId) {
        likedPostIds.add(postId);
    }
    
    private void removeLikedPost(String postId) {
        likedPostIds.remove(postId);
    }
    
    // User info methods
    private String getCurrentUser() {
        return UserSession.getInstance().getCurrentUsername();
    }
    
    private String getFollowedUsers(String currentUser) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FOLLOWING_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                if (line.startsWith(currentUser + ":")) {
                    String[] parts = line.split(":", 2);
                    return parts.length > 1 ? parts[1].trim() : "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    // Utility methods
    public String getTimeSincePosting(Post post) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = post.getCreatedAt();
        
        // Check for days, hours, minutes in sequence
        long amount = ChronoUnit.DAYS.between(createdAt, now);
        String unit = "day";
        
        if (amount == 0) {
            amount = ChronoUnit.HOURS.between(createdAt, now);
            unit = "hour";
            
            if (amount == 0) {
                amount = ChronoUnit.MINUTES.between(createdAt, now);
                unit = "minute";
                
                if (amount == 0) {
                    return "just now";
                }
            }
        }
        
        return formatTimeAgo(amount, unit);
    }
    
    private String formatTimeAgo(long amount, String unit) {
        return amount + " " + unit + (amount != 1 ? "s" : "") + " ago";
    }
    
    // Interface for filtering post lines
    @FunctionalInterface
    private interface PostLineFilter {
        boolean accept(String line);
    }
} 