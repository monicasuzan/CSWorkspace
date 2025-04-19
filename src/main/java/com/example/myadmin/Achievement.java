package com.example.myadmin;

public class Achievement {
    private String id;
    private String name;
    private String details;
    private String imageBase64;
    private long timestamp; // Added timestamp

    // Default constructor required for Firebase
    public Achievement() {}

    public Achievement(String id, String name, String details, String imageBase64, long timestamp) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.imageBase64 = imageBase64;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Firebase Compatibility: Alias getPhotoUrl() for Glide
    public String getPhotoUrl() {
        return imageBase64;
    }
}
