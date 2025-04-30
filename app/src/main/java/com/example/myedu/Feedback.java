package com.example.myedu;

public class Feedback {
    private String id;
    private String userName;
    private String userId;
    private String comment;
    private float rating;

    public Feedback() {
        // Default constructor required for Firestore
    }

    public Feedback(String id, String userName, String userId, String comment, float rating) {
        this.id = id;
        this.userName = userName;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getComment() {
        return comment;
    }

    public float getRating() {
        return rating;
    }
}
