package com.example.myedu;

public class User {

    // Fields (represent Firestore document fields)
    private String fullName;
    private int bestScore;

    // Default constructor required for Firestore serialization
    public User() {
        // Firestore requires an empty constructor for deserialization
    }

    // Constructor to create a new User with fullName and bestScore
    public User(String fullName, int bestScore) {
        this.fullName = fullName;
        this.bestScore = bestScore;
    }

    // Getter and Setter for fullName
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Getter and Setter for bestScore
    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }
}
