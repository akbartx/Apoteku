package com.aplikasi.apoteku;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.aplikasi.apoteku.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "RegisterActivity";

    EditText registerUsername, registerEmail, registerPassword, registerConfirmPassword;
    ImageView profileImageView;
    Button registerButton, selectImageButton;
    TextView loginRedirect;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Declaration of ids from layout
        registerUsername = findViewById(R.id.username_register);
        registerEmail = findViewById(R.id.email_register);
        registerPassword = findViewById(R.id.password_register);
        registerConfirmPassword = findViewById(R.id.confirm_password_register);
        profileImageView = findViewById(R.id.profile_image);
        selectImageButton = findViewById(R.id.select_image_button);
        registerButton = findViewById(R.id.registerButton);
        loginRedirect = findViewById(R.id.register_to_login);

        // Firebase instances
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("profile_images");

        // Select image button click listener
        selectImageButton.setOnClickListener(v -> openFileChooser());

        // Register button click listener
        registerButton.setOnClickListener(v -> {
            // Get user inputs
            String username = registerUsername.getText().toString();
            String email = registerEmail.getText().toString();
            String password = registerPassword.getText().toString();
            String confirm_password = registerConfirmPassword.getText().toString();

            // Validate inputs
            if (!validateUsername() | !validateEmail() | !validatePassword() | !validateConfirmPassword()) {
                Toast.makeText(RegisterActivity.this, "Please fill your data first!", Toast.LENGTH_SHORT).show();
            } else {
                // Upload photo and save user data
                if (imageUri != null) {
                    uploadPhotoAndSaveUser(username, email, password, confirm_password);
                } else {
                    saveUserToDatabase(username, email, password, confirm_password, null);
                }
            }
        });

        // Redirect to login
        loginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void uploadPhotoAndSaveUser(String username, String email, String password, String confirm_password) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(username + ".jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String photoUrl = uri.toString();
                saveUserToDatabase(username, email, password, confirm_password, photoUrl);
            })).addOnFailureListener(e -> {
                Toast.makeText(RegisterActivity.this, "Photo upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveUserToDatabase(String username, String email, String password, String confirm_password, String photoUrl) {
        if (username != null && !username.isEmpty() &&
                email != null && !email.isEmpty() &&
                password != null && !password.isEmpty() &&
                confirm_password != null && !confirm_password.isEmpty()) {

            User user = new User(username, email, password, confirm_password, photoUrl);

            // Log the data
            Log.d(TAG, "Saving user: " + user.toString());

            reference.child(username).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration process success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to register: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean validateUsername() {
        String val = registerUsername.getText().toString();
        if (val.isEmpty()) {
            registerUsername.setError("Username not fill yet");
            return false;
        } else {
            registerUsername.setError(null);
            return true;
        }
    }

    public Boolean validateEmail() {
        String val = registerEmail.getText().toString();
        if (val.isEmpty()) {
            registerEmail.setError("Email not fill yet");
            return false;
        } else {
            registerEmail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = registerPassword.getText().toString();
        if (val.isEmpty()) {
            registerPassword.setError("Password not fill yet");
            return false;
        } else {
            registerPassword.setError(null);
            return true;
        }
    }

    public Boolean validateConfirmPassword() {
        String val = registerConfirmPassword.getText().toString();
        if (val.isEmpty()) {
            registerConfirmPassword.setError("Confirm Password not fill yet");
            return false;
        } else {
            registerConfirmPassword.setError(null);
            return true;
        }
    }
}
