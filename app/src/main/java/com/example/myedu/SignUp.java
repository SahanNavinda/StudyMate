package com.example.myedu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtConfirmPassword, edtFullName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Adjust layout for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Views initialization
        edtEmail = findViewById(R.id.etEmail);
        edtPassword = findViewById(R.id.etPassword);
        edtConfirmPassword = findViewById(R.id.etConfirmPassword);
        edtFullName = findViewById(R.id.etName);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvBackToLogin = findViewById(R.id.tvAlreadyHaveAccount);

        // Set onClick listeners
        btnSignUp.setOnClickListener(v -> registerUser());
        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String fullName = edtFullName.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register the user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the user ID
                String userId = mAuth.getCurrentUser().getUid();

                // Prepare user data for Firestore
                Map<String, Object> user = new HashMap<>();
                user.put("fullName", fullName);
                user.put("email", email);
                user.put("uid", userId);
                user.put("bestScore", 0);  // ⭐ Add bestScore default 0

                // Save user data to Firestore
                db.collection("Users").document(userId)
                        .set(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SignUp.this, "Registered and data saved!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this, LoginActivity.class));
                            finish(); // Close SignUp activity
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SignUp.this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(SignUp.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
