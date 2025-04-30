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

public class ForgotPass extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnResetPassword;
    private TextView tvBackToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Views
        edtEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Handle Reset Password button click
        btnResetPassword.setOnClickListener(v -> sendPasswordResetEmail());

        // Handle Back to Login button click
        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPass.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close ForgotPass activity to prevent going back to it
        });
    }

    private void sendPasswordResetEmail() {
        String email = edtEmail.getText().toString().trim();

        // Validate email input
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ForgotPass.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send password reset email using Firebase Authentication
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If email sent successfully, show a message
                        Toast.makeText(ForgotPass.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPass.this, LoginActivity.class));
                        finish(); // Close ForgotPass activity
                    } else {
                        // If an error occurs, show the error message
                        Toast.makeText(ForgotPass.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}