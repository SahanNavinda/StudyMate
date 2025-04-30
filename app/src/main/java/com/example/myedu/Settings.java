package com.example.myedu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.AuthCredential;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Button to delete the account
        Button btnDeleteAccount = findViewById(R.id.btnSignUp);

        btnDeleteAccount.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(Settings.this)
                    .setMessage("Are you sure you want to delete your account?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Ask user for password to reauthenticate before account deletion
                        promptForPasswordAndDelete();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Do nothing, just dismiss the dialog
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    // Prompt the user for their password to reauthenticate
    private void promptForPasswordAndDelete() {
        final EditText passwordInput = new EditText(Settings.this);
        passwordInput.setHint("Enter your password");
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(Settings.this)
                .setTitle("Reauthenticate to Delete Account")
                .setMessage("Please enter your password to confirm account deletion.")
                .setView(passwordInput)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    String password = passwordInput.getText().toString().trim();
                    if (!password.isEmpty()) {
                        // Proceed to reauthenticate and delete account
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            reauthenticateAndDelete(user, password);
                        } else {
                            Toast.makeText(Settings.this, "No user is logged in.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Settings.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Reauthenticate the user with the provided password
    private void reauthenticateAndDelete(FirebaseUser user, String password) {
        String email = user.getEmail();
        if (email != null) {
            // Create the credential for reauthentication
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            // Reauthenticate the user with the credential
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User reauthenticated, now proceed to delete the account
                            performAccountDeletion();
                        } else {
                            // Handle reauthentication failure
                            Toast.makeText(Settings.this, "Reauthentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(Settings.this, "Failed to fetch user email.", Toast.LENGTH_SHORT).show();
        }
    }

    // Perform account deletion
    private void performAccountDeletion() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Proceed with deleting the account
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Account deleted successfully
                            Toast.makeText(Settings.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            // Optionally log the user out and redirect to login screen
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(Settings.this, LoginActivity.class));  // Go to login screen
                            finish();
                        } else {
                            // If the deletion fails for any reason
                            Toast.makeText(Settings.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // If the user is null (logged out)
            Toast.makeText(Settings.this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
