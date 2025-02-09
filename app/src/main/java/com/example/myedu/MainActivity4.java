package com.example.myedu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class MainActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Enable full screen
        setContentView(R.layout.activity_main4);

        // Set up window insets for edge-to-edge mode
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find ImageButton and set click listener for the Quiz button
        ImageButton imageButton = findViewById(R.id.imageButton3);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity4.this, MainActivity5.class); // Navigate to MainActivity5
            startActivity(intent);
        });

        // Find the 4 ImageViews (boxes) and set click listeners to navigate to another activity
        ImageView box1 = findViewById(R.id.imageView1);
        ImageView box2 = findViewById(R.id.imageView2);
        ImageView box3 = findViewById(R.id.imageView3);
        ImageView box4 = findViewById(R.id.imageView4);

        // Set click listeners for the boxes
        box1.setOnClickListener(v -> {
            // Navigate to the Quiz Activity (or any other activity you want)
            Intent intent = new Intent(MainActivity4.this, QuizActivity6.class);
            startActivity(intent);
        });

        box2.setOnClickListener(v -> {
            // Navigate to the Quiz Activity (or any other activity you want)
            Intent intent = new Intent(MainActivity4.this, LessonActivity.class);
            startActivity(intent);
        });

        box3.setOnClickListener(v -> {
            // Navigate to the Quiz Activity (or any other activity you want)
            Intent intent = new Intent(MainActivity4.this, QuizActivity6.class);
            startActivity(intent);
        });

        box4.setOnClickListener(v -> {
            // Navigate to the Quiz Activity (or any other activity you want)
            Intent intent = new Intent(MainActivity4.this, QuizActivity6.class);
            startActivity(intent);
        });
    }
}