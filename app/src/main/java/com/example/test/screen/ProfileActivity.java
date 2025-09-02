package com.example.test.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private EditText editTextName;
    private EditText editTextRollNo;
    private Button saveButton;
    private ProgressBar progressBar;
    private FrameLayout loadingOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user == null) {
            Toast.makeText(this, "No authenticated user found", Toast.LENGTH_SHORT).show();
            return;
        }

        editTextName = findViewById(R.id.editTextName);
        editTextRollNo = findViewById(R.id.editTextRollNumber);
        saveButton = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        saveButton.setOnClickListener(v -> saveProfile());

    }

    private void saveProfile() {

        saveButton.setEnabled(false); // disable button
        loadingOverlay.setVisibility(View.VISIBLE); // show overlay
        progressBar.setVisibility(View.VISIBLE); // show spinner

        String authUid  = user.getUid();
        String name = editTextName.getText().toString().trim();
        String rollNo = editTextRollNo.getText().toString().trim();
        CollectionReference usersRef = db.collection("users");

        if (name.isEmpty() || rollNo.isEmpty()) {
            saveButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            loadingOverlay.setVisibility(View.GONE);
            if (name.isEmpty()) editTextName.setError("Name required");
            if (rollNo.isEmpty()) editTextRollNo.setError("Roll No required");
            return;
        }

        usersRef.document(rollNo)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.exists()) {
                        usersRef.document(rollNo)
                                .update("linkedAuthUid", authUid, "name", name)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, HomeActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error updating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                                .addOnCompleteListener(task -> {
                                    saveButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    loadingOverlay.setVisibility(View.GONE);
                                });
                    }
                    else {
                        User userProfile = new User();
                        userProfile.setLinkedAuthUid(authUid);
                        userProfile.setName(name);
                        userProfile.setRollNo(rollNo);

                        usersRef.document(rollNo)
                                .set(userProfile)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, HomeActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                                .addOnCompleteListener(task -> {
                                    saveButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    loadingOverlay.setVisibility(View.GONE);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    saveButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    loadingOverlay.setVisibility(View.GONE);
                });
    }
}
