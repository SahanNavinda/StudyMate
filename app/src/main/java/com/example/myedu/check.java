package com.example.myedu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class check extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

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
                // Handle Settings Click
            }

            // Close drawer after selection
            drawerLayout.closeDrawers();
            return true;
        });
    }
}
