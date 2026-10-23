package com.example.iprepare;

public class Announcement {
    private String id;
    private String title;
    private String content;
    private String author;
    private String datePosted;
    private String imageUrl;
    private String type;

    public Announcement() {
        // Empty constructor needed for Firestore
    }

    public Announcement(String id, String title, String content, String author, String datePosted, String imageUrl, String type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.datePosted = datePosted;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getDatePosted() { return datePosted; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthor(String author) { this.author = author; }
    public void setDatePosted(String datePosted) { this.datePosted = datePosted; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setType(String type) { this.type = type; }
}
