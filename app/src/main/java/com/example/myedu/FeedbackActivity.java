package com.example.myedu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FeedbackActivity extends AppCompatActivity {

    private EditText commentEditText;
    private RatingBar ratingBar;
    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference feedbackRef;
    private FirebaseAuth auth;
    private String currentFeedbackId = null;
    private Button submitBtn;  // Declare the Button here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        feedbackRef = db.collection("feedback");

        commentEditText = findViewById(R.id.comment_edit_text);
        ratingBar = findViewById(R.id.rating_bar);
        recyclerView = findViewById(R.id.recycler_view);
        submitBtn = findViewById(R.id.buttonSubmitFeedback);  // Initialize the Button
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerView();

        submitBtn.setOnClickListener(v -> submitFeedback());
    }

    private void setupRecyclerView() {
        Query query = feedbackRef.orderBy("rating", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Feedback> options = new FirestoreRecyclerOptions.Builder<Feedback>()
                .setQuery(query, Feedback.class)
                .build();

        adapter = new FeedbackAdapter(options, new FeedbackAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String feedbackId, String comment, float rating) {
                currentFeedbackId = feedbackId;
                commentEditText.setText(comment);
                ratingBar.setRating(rating);
                submitBtn.setText("Update Feedback");  // Change button text when editing
            }

            @Override
            public void onItemDelete(String feedbackId) {
                feedbackRef.document(feedbackId).delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(FeedbackActivity.this, "Feedback deleted", Toast.LENGTH_SHORT).show();
                            clearForm();
                            refreshList();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(FeedbackActivity.this, "Error deleting feedback", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void submitFeedback() {
        String comment = commentEditText.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (comment.isEmpty() || rating == 0.0f) {
            Toast.makeText(this, "Please provide comment and rating", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("Users").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("fullName");

                    if (userName != null) {
                        if (currentFeedbackId != null) {
                            // Updating an existing feedback
                            Feedback updatedFeedback = new Feedback(currentFeedbackId, userName, userId, comment, rating);
                            feedbackRef.document(currentFeedbackId).set(updatedFeedback)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Feedback updated", Toast.LENGTH_SHORT).show();
                                        clearForm();
                                        refreshList();
                                        submitBtn.setText("Submit Feedback");  // Change button text back
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error updating feedback", Toast.LENGTH_SHORT).show());
                        } else {
                            // Adding new feedback
                            Feedback newFeedback = new Feedback("", userName, userId, comment, rating);
                            feedbackRef.add(newFeedback)
                                    .addOnSuccessListener(docRef -> {
                                        Toast.makeText(this, "Feedback submitted", Toast.LENGTH_SHORT).show();
                                        clearForm();
                                        refreshList();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error submitting feedback", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "User name not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            });

        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        commentEditText.setText("");
        ratingBar.setRating(0);
        currentFeedbackId = null;
        submitBtn.setText("Submit Feedback");  // Reset the button text
    }

    private void refreshList() {
        if (adapter != null) {
            adapter.stopListening();
        }
        setupRecyclerView();  // Recreate adapter with updated data
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}
