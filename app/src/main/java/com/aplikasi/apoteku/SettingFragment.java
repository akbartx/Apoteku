package com.aplikasi.apoteku;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aplikasi.apoteku.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SettingFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private EditText usernameEditText;
    private Button selectImageButton, saveButton;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String username;
    private Uri imageUri;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString("username");
            Log.d(TAG, "Received username: " + username);
        } else {
            Log.d(TAG, "No username received in arguments");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Initialize UI elements
        profileImageView = view.findViewById(R.id.profile_image);
        usernameEditText = view.findViewById(R.id.username_edit);
        selectImageButton = view.findViewById(R.id.select_image_button);
        saveButton = view.findViewById(R.id.save_button);

        // Get the reference to the Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Fetch and display user data
        if (username != null) {
            Log.d(TAG, "Fetching data for user: " + username);
            databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "DataSnapshot received: " + dataSnapshot.toString());
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            Log.d(TAG, "User data: " + user.toString());
                            usernameEditText.setText(user.getUsername());
                            String photoUrl = user.getPhotoUrl();
                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                Log.d(TAG, "Loading photo from URL: " + photoUrl);
                                Picasso.get().load(photoUrl).into(profileImageView);
                            } else {
                                Log.d(TAG, "Photo URL is empty");
                            }
                        } else {
                            Log.d(TAG, "User object is null");
                        }
                    } else {
                        Log.d(TAG, "User data does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors.
                    Log.d(TAG, "DatabaseError: " + databaseError.getMessage());
                }
            });
        }

        // Select image button click listener
        selectImageButton.setOnClickListener(v -> openFileChooser());

        // Save button click listener
        saveButton.setOnClickListener(v -> saveUserData());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void saveUserData() {
        String newUsername = usernameEditText.getText().toString().trim();

        if (newUsername.isEmpty()) {
            usernameEditText.setError("Username tidak boleh kosong");
            usernameEditText.requestFocus();
            return;
        }

        if (imageUri != null) {
            uploadPhotoAndUpdateUser(newUsername);
        } else {
            updateUserInDatabase(newUsername, null);
        }
    }

    private void uploadPhotoAndUpdateUser(String newUsername) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(newUsername + ".jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String photoUrl = uri.toString();
                updateUserInDatabase(newUsername, photoUrl);
            })).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Upload photo gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateUserInDatabase(String newUsername, String photoUrl) {
        DatabaseReference userReference = databaseReference.child(username);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        String oldUsername = user.getUsername();
                        user.setUsername(newUsername);
                        if (photoUrl != null) {
                            user.setPhotoUrl(photoUrl);
                        }
                        databaseReference.child(oldUsername).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                databaseReference.child(newUsername).setValue(user).addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Berhasil mengupdate data", Toast.LENGTH_SHORT).show();
                                        // Clear login status
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.remove("isLoggedIn");
                                        editor.remove("username");
                                        editor.apply();
                                        // Redirect to LoginActivity
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(getActivity(), "Gagal mengupdate data: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Gagal menghapus data lama: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "DatabaseError: " + databaseError.getMessage());
            }
        });
    }
}
