package com.example.myedu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;  // Import FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore;  // Import FirebaseFirestore

public class MainActivity extends AppCompatActivity {

    // Initialize Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);  // Initialize Firebase
        db = FirebaseFirestore.getInstance();  // Initialize Firestore instance

        // Fix for window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find Button and set click listener
        Button imageButton = findViewById(R.id.btnGetStarted);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Example usage of Firestore (Optional, remove if unnecessary)
        // You can now interact with Firestore as needed, for example:
        // db.collection("users").get().addOnCompleteListener(task -> {
        //     if (task.isSuccessful()) {
        //         // Handle successful fetch
        //     } else {
        //         // Handle failure
        //     }
        // });
    }
}
