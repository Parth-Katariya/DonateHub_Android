package com.example.donatehub;

public class User {
    public String name;
    public String email;
    public String phone;
    public String profileImageUrl; // Add this field for the profile image URL

    public User() {
        // Default constructor required for Firebase
    }

    public User(String name, String email, String phone, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
    }
}

