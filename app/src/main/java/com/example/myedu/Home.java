package com.example.myedu;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        // Example: Open the drawer when a button is clicked
        findViewById(R.id.imageButton4).setOnClickListener(view -> {
            openDrawer();
        });
        ImageButton imageButton = findViewById(R.id.imageButton3);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, QuizActivity.class); // Navigate to MainActivity5
            startActivity(intent);
        });
        ImageButton imageButton1 = findViewById(R.id.imageButton5);
        imageButton1.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, QuizActivity.class); // Navigate to MainActivity5
            startActivity(intent);
        });

        // Set up the navigation item listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Handle Home click
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(Home.this, ProfileActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_settings) {
                // Handle Settings click
            }

            // Close the drawer after selection
            drawerLayout.closeDrawers();
            return true;
        });
        ImageView box1 = findViewById(R.id.imageView1);
        ImageView box2 = findViewById(R.id.imageView2);
        ImageView box3 = findViewById(R.id.imageView3);
        ImageView box4 = findViewById(R.id.imageView4);
        // Set click listeners for the boxes
        box1.setOnClickListener(v -> {
            // Navigate to the Quiz Activity (or any other activity you want)
            Intent intent = new Intent(Home.this, LessonActivity.class);
            startActivity(intent);
        });

        box2.setOnClickListener(v -> {
            // Navigate to the Quiz Activity (or any other activity you want)
            Intent intent = new Intent(Home.this, LessonActivity.class);
            startActivity(intent);
        });

        box3.setOnClickListener(v -> {
            // Navigate to the Quiz Activity (or any other activity you want)
            Intent intent = new Intent(Home.this, LessonActivity.class);
            startActivity(intent);
        });

        box4.setOnClickListener(v -> {
            // Navigate to the Quiz Activity (or any other activity you want)
            Intent intent = new Intent(Home.this, LessonActivity.class);
            startActivity(intent);
        });
    }
    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START); // Opens the drawer from the left
        }
    }
}