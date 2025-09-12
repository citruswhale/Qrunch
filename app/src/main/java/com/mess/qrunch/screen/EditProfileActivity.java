package com.mess.qrunch.screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;

import com.mess.qrunch.R;
import com.mess.qrunch.helper.ReauthenticateUserForSensitiveOpsHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executors;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private CollectionReference usersRef;
    private CredentialManager credentialManager;

    private TextInputEditText editTextName;
    private TextInputEditText editTextStaticRollNumber;
    private Button buttonSaveChanges;
    private Button buttonSignOut;
    private Button buttonDeleteAccount;
    private String rollNo;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        View mainView = findViewById(R.id.main);

        final int initialLeftPadding = mainView.getPaddingLeft();
        final int initialTopPadding = mainView.getPaddingTop();
        final int initialRightPadding = mainView.getPaddingRight();
        final int initialBottomPadding = mainView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply initial padding plus system bar insets
            v.setPadding(
                    initialLeftPadding + systemBars.left,
                    initialTopPadding + systemBars.top,
                    initialRightPadding + systemBars.right,
                    initialBottomPadding + systemBars.bottom
            );
            return insets;
        });

        editTextName = findViewById(R.id.editTextEditName);
        editTextStaticRollNumber = findViewById(R.id.editTextStaticRollNumber);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonSignOut = findViewById(R.id.buttonSignOut);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);

        credentialManager = CredentialManager.create(getBaseContext());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        usersRef = db.collection("users");

        if (user != null) {
            loadUserProfile(user.getUid());
        } else {
            Toast.makeText(this, "No authenticated user. Please sign in.", Toast.LENGTH_LONG).show();
        }

        buttonSaveChanges.setOnClickListener(view -> {
            updateProfile();
        });
        buttonSignOut.setOnClickListener(view -> {
             signOut();
        });
        buttonDeleteAccount.setOnClickListener(view -> {
             deleteAccount();
        });
    }

    private void loadUserProfile(String uid) {
        usersRef.whereEqualTo("linkedAuthUid", uid)
                .limit(1) // Expecting only one document for the given auth UID
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String name = querySnapshot.getDocuments().get(0).getString("name");
                        rollNo = querySnapshot.getDocuments().get(0).getId();

                        if (name != null) {
                            editTextName.setText(name);
                            this.name = name;
                        }
                        if (rollNo != null) {
                            editTextStaticRollNumber.setText(rollNo);
                        }
                    }
                    else {
                        Toast.makeText(EditProfileActivity.this, "Profile data not found. Please complete your profile.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, ProfileActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateProfile() {

        String authUid  = user.getUid();
        String name = editTextName.getText().toString().trim();

        if (this.name.equals(name)) {
            Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()) {
            editTextName.setError("Name required");
            return;
        }

        usersRef.document(rollNo)
                .update("linkedAuthUid", authUid, "name", name)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // When a user signs out, clear the current user credential state from all credential providers.
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(@NonNull Void result) {
                        startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.e(TAG, "Couldn't clear user credentials: " + e.getLocalizedMessage());
                    }
                });
    }

    private void deleteAccount() {
        new ReauthenticateUserForSensitiveOpsHelper(user, this, TAG)
            .reauthenticate((success, ex) -> {
                Log.e(TAG, "Re-auth success: " + success);
                if (success) {
                    usersRef.document(rollNo)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("DeleteUser", "Firestore data deleted");

                                // 2. Delete Firebase Auth account
                                user.delete().addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Log.d("DeleteUser", "Firebase account deleted");
                                        signOut();
                                    } else {
                                        Log.e("DeleteUser", "Error deleting user account", deleteTask.getException());
                                    }
                                });
                            })
                            .addOnFailureListener(e -> Log.e("DeleteUser", "Error deleting Firestore data", e));
                }
                else {
                    Log.e("DeleteUser", "Re-auth failed", ex);
                }
            });
    }
}