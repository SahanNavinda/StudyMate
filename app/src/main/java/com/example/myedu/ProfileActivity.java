package com.example.myedu;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnUpdate, btnClear;
    private ImageView imageView;
    private Uri imageUri;
    private boolean isImageChanged = false;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String PREFS_NAME = "user_profile";
    private static final String IMAGE_FILE_NAME_KEY = "profile_image_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFullName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdate = findViewById(R.id.btnSignUp);
        btnClear = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fetchUserData();

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnClear.setOnClickListener(v -> clearFields());

        imageView.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                String savedFileName = saveImageToInternalStorage(imageUri);
                if (savedFileName != null) {
                    isImageChanged = true;
                    saveImageFileName(savedFileName);
                    loadProfileImage(); // Load circular image
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) return null;

        deleteOldImage();

        String fileExtension = getFileExtension(uri);
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        File file = new File(getFilesDir(), fileName);

        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }

        outputStream.close();
        inputStream.close();
        return fileName;
    }

    private void deleteOldImage() {
        String oldFileName = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(IMAGE_FILE_NAME_KEY, null);
        if (oldFileName != null) {
            File oldFile = new File(getFilesDir(), oldFileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void saveImageFileName(String fileName) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IMAGE_FILE_NAME_KEY, fileName);
        editor.apply();
    }

    private void loadProfileImage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String fileName = prefs.getString(IMAGE_FILE_NAME_KEY, null);

        if (fileName != null) {
            File imageFile = new File(getFilesDir(), fileName);
            if (imageFile.exists()) {
                Glide.with(this)
                        .load(imageFile)
                        .circleCrop() // Apply circular crop
                        .into(imageView);
            }
        } else {
            imageView.setImageResource(R.drawable.user_1); // Default fallback image
        }
    }

    private void fetchUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");

                        etFullName.setText(fullName);
                        etEmail.setText(email);
                        loadProfileImage();
                    } else {
                        Toast.makeText(ProfileActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(password) && !password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        db.collection("Users").document(userId)
                .update("fullName", fullName, "email", email)
                .addOnSuccessListener(aVoid -> {
                    user.updateEmail(email)
                            .addOnSuccessListener(aVoid1 -> {
                                if (!TextUtils.isEmpty(password)) {
                                    user.updatePassword(password)
                                            .addOnSuccessListener(aVoid2 -> {
                                                Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Password update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Email update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        etFullName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
        imageView.setImageResource(R.drawable.user_1); // Default image
        deleteOldImage();
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().remove(IMAGE_FILE_NAME_KEY).apply();
    }
}
