package com.example.myedu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class Home extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final String PREFS_NAME = "user_profile";
    private static final String IMAGE_FILE_NAME_KEY = "profile_image_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Open drawer on button click
        findViewById(R.id.imageButton4).setOnClickListener(view -> openDrawer());

        // Navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Home clicked
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(Home.this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(Home.this, Settings.class));
            }else if (id == R.id.nav_feedback) {
                startActivity(new Intent(Home.this, FeedbackActivity.class));

            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Quiz buttons
        findViewById(R.id.imageButton3).setOnClickListener(v -> startActivity(new Intent(Home.this, QuizActivity.class)));
        findViewById(R.id.imageButton5).setOnClickListener(v -> startActivity(new Intent(Home.this, QuizActivity.class)));

        // Box clicks
        findViewById(R.id.imageView1).setOnClickListener(v -> startActivity(new Intent(Home.this, LessonActivity.class)));
        findViewById(R.id.imageView2).setOnClickListener(v -> startActivity(new Intent(Home.this, LessonActivity.class)));
        findViewById(R.id.imageView3).setOnClickListener(v -> startActivity(new Intent(Home.this, LessonActivity.class)));
        findViewById(R.id.imageView4).setOnClickListener(v -> startActivity(new Intent(Home.this, LessonActivity.class)));

        // Update drawer info
        updateDrawerInfo();
    }

    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void updateDrawerInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView userNameTextView = headerView.findViewById(R.id.navUserName);
            TextView bestScoreTextView = headerView.findViewById(R.id.navBestScore);
            ImageView profileImageView = headerView.findViewById(R.id.navProfileImage);

            String userId = currentUser.getUid();
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userId);

            // Listen in real-time
            userRef.addSnapshotListener((documentSnapshot, error) -> {
                if (error != null) {
                    Log.e("HomeActivity", "Listen failed.", error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fullName");
                    Long bestScore = documentSnapshot.getLong("bestScore");

                    userNameTextView.setText(fullName != null ? "Hello, " + fullName : "Hello, User");
                    bestScoreTextView.setText("Best Score: " + (bestScore != null ? bestScore : 0));

                    // Load local profile image
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    String fileName = prefs.getString(IMAGE_FILE_NAME_KEY, null);

                    if (fileName != null) {
                        File imageFile = new File(getFilesDir(), fileName);
                        if (imageFile.exists()) {
                            Glide.with(this)
                                    .load(imageFile)
                                    .circleCrop()
                                    .into(profileImageView);
                        } else {
                            profileImageView.setImageResource(R.drawable.user_1); // fallback image
                        }
                    } else {
                        profileImageView.setImageResource(R.drawable.user_1); // fallback image
                    }
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
