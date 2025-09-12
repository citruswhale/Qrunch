package com.mess.qrunch.helper;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadUserHelper {
    private final CollectionReference usersRef;

    public LoadUserHelper() {
        usersRef = FirebaseFirestore.getInstance().collection("users");
    }

    // Updated interface: return vendorId + rollNo + name
    public interface ProfileLoadCallback {
        void onProfileLoaded(Long vendorId, String rollNo, String name);
        void onError(Exception e);
    }

    public void loadUserProfile(Activity activity, ProfileLoadCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError(new Exception("No authenticated user"));
            return;
        }

        usersRef.whereEqualTo("linkedAuthUid", user.getUid())
                .limit(1)
                .get()
                .addOnSuccessListener(activity, querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String name = querySnapshot.getDocuments().get(0).getString("name");
                        String rollNo = querySnapshot.getDocuments().get(0).getId();
                        Long vendorId = querySnapshot.getDocuments().get(0).getLong("vendorId");

                        if (name != null) PrefsHelper.saveName(activity, name);
                        if (rollNo != null) PrefsHelper.saveRollNo(activity, rollNo);
                        if (vendorId != null) PrefsHelper.saveVendorId(activity, vendorId);

                        callback.onProfileLoaded(vendorId, rollNo, name);
                    } else {
                        callback.onError(new Exception("No profile found"));
                    }
                })
                .addOnFailureListener(activity, callback::onError);
    }
}
