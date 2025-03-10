package com.quackstagram.model;
import java.time.LocalDateTime;

public class Post {
    private String id;
    private String username;
    private String description;
    private String imagePath;
    private int likes;
    private LocalDateTime createdAt;
    private boolean isLiked;

    Post(String id, String username, String description, String imagePath, int likes, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.description = description;
        this.imagePath = imagePath;
        this.likes = likes;
        this.createdAt = createdAt;
        this.isLiked = false;
    }

    // Add static builder method
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String username;
        private String description;
        private String imagePath;
        private int likes;
        private LocalDateTime createdAt;

        public Builder() {
            this.createdAt = LocalDateTime.now();
            this.likes = 0;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder likes(int likes) {
            this.likes = likes;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Post build() {
            String finalDescription = "";
            if (description != null) {
                finalDescription = description;
            }
            
            return new Post(id, username, finalDescription, imagePath, likes, createdAt);
        }
    }


    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getLikes() {
        return likes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void incrementLikes() {
        likes++;
    }

    public void decrementLikes() {
        if (likes > 0) {
            likes--;
        }
    }

    @Override
    public String toString() {
        return String.format("Post{id='%s', username='%s', description='%s', likes=%d}", 
            id, username, description, likes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id.equals(post.id);
    }
} 