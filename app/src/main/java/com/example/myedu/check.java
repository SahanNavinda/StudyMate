package com.example.myedu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class check extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView navUserName, navBestScore;  // References to the TextViews in the drawer

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Find the TextViews where we want to update the user's name and best score
        navUserName = navigationView.getHeaderView(0).findViewById(R.id.navUserName);
        navBestScore = navigationView.getHeaderView(0).findViewById(R.id.navBestScore);

        // ImageView for profile image in the drawer header
        ImageView navProfileImage = navigationView.getHeaderView(0).findViewById(R.id.navProfileImage);

        // Open Drawer when clicking the button
        ImageButton imageButton = findViewById(R.id.imageButton4);
        imageButton.setOnClickListener(view -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set up Navigation Item Click Listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Handle Home Click
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(check.this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(check.this, Settings.class)); // Handle Settings Click
            } else if (id == R.id.nav_feedback) {
                startActivity(new Intent(check.this, FeedbackActivity.class));

            }


            // Close drawer after selection
            drawerLayout.closeDrawers();
            return true;
        });

        // Fetch user data and update the navigation drawer
        updateDrawerUserInfo(navProfileImage);
    }

    private void updateDrawerUserInfo(ImageView navProfileImage) {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String bestScore = documentSnapshot.getString("bestScore");

                        // Update the TextView with the user's name in the drawer
                        if (fullName != null) {
                            navUserName.setText("Hello " + fullName + ",");
                        }

                        // Update the Best Score TextView
                        if (bestScore != null) {
                            navBestScore.setText("Best Score: " + bestScore);
                        }

                        // Load profile image from internal storage
                        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
                        String fileName = prefs.getString("profile_image_file", null);

                        if (fileName != null) {
                            File file = new File(getFilesDir(), fileName);
                            if (file.exists()) {
                                Glide.with(this)
                                        .load(file)
                                        .circleCrop() // optional: make it round
                                        .into(navProfileImage);
                            }
                        }
                    } else {
                        Toast.makeText(check.this, "No user data found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(check.this, "Error fetching user data", Toast.LENGTH_SHORT).show());
    }
}
