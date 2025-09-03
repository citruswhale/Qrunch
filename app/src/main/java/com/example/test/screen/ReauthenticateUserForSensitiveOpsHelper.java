package com.example.test.screen;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ReauthenticateUserForSensitiveOpsHelper {

    private final FirebaseUser user;
    private final AppCompatActivity activity;
    private final CredentialManager credentialManager;
    private final String TAG;
    boolean reauthSuccess;


    public ReauthenticateUserForSensitiveOpsHelper(FirebaseUser user, AppCompatActivity activity, String TAG) {
        this.user = user;
        this.activity = activity;
        this.credentialManager = CredentialManager.create(activity.getBaseContext());
        this.TAG = TAG;
        this.reauthSuccess = false;
    }

    public void reauthenticate(ReauthCallback callback) {
        String rawNonce = UUID.randomUUID().toString();
        String hashedNonce = "";
        try {
            byte[] bytes = rawNonce.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            hashedNonce = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to generate SHA-256 hash", e);
        }
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setNonce(hashedNonce)
                .setServerClientId("608617228445-ipki25pjggso3iidd1407g034g18g62s.apps.googleusercontent.com")
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                activity.getBaseContext(),
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        Credential cred = result.getCredential();

                        if (cred instanceof CustomCredential customCredential &&
                                cred.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {

                            Bundle credentialData = customCredential.getData();
                            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

                            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.getIdToken(), null);

                            user.reauthenticate(firebaseCredential).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Re-auth success"+reauthSuccess);
                                    callback.onComplete(true, null);
                                } else {
                                    Log.e(TAG, "Re-auth failed", task.getException());
                                    callback.onComplete(false, task.getException());
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "CredentialManager error: " + e.getLocalizedMessage());
                        callback.onComplete(false, e);
                    }
                }
        );
    }
}
