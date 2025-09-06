package com.example.test.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.helper.LoadUserHelper;
import com.example.test.helper.MenuCacheBustingHelper;
import com.example.test.helper.PrefsHelper;

public class HomeActivity extends AppCompatActivity {

    private ImageButton buttonEditProfile;
    private Button buttonViewMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonViewMenu = findViewById(R.id.buttonViewMenu);

        buttonEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        buttonViewMenu.setOnClickListener(view -> {
            loadMenu();
        });
    }

    private void loadMenu() {
        ImagePopupDialog.newInstance(
                "https://tstxevjuvcrabrrtkgfc.supabase.co/storage/v1/object/public/menu/ChatGPT%20Image%20Aug%2025,%202025,%2011_16_31%20PM.png"
        ).show(getSupportFragmentManager(), "ImagePopupDialog");
//        String MenuImgURL = MenuCacheBustingHelper.getCachedImage(this);
//        if (MenuImgURL == null) {
//            Long vendorId = PrefsHelper.getVendorId(this);
//
//            if (vendorId == null) {
//                LoadUserHelper loadUserHelper = new LoadUserHelper();
//                loadUserHelper.loadUserProfile(this, new LoadUserHelper.ProfileLoadCallback() {
//                    @Override
//                    public void onProfileLoaded(Long vendorId) {
//                        if (vendorId != null) {
//                            ImagePopupDialog.newInstance(
//                                    "https://tstxevjuvcrabrrtkgfc.supabase.co/storage/v1/object/public/menu/ChatGPT%20Image%20Aug%2025,%202025,%2011_16_31%20PM.png"
//                                    ).show(getSupportFragmentManager(), "ImagePopupDialog");
//                        } else {
//                            Log.e("HomeActivity", "VendorId is still null");
//                        }
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        Log.e("HomeActivity", "Failed to load profile", e);
//                    }
//                });
//            } else {
//                // already cached
//                ImagePopupDialog.newInstance(
//                        "https://tstxevjuvcrabrrtkgfc.supabase.co/storage/v1/object/public/menu/ChatGPT%20Image%20Aug%2025,%202025,%2011_16_31%20PM.png"
//                ).show(getSupportFragmentManager(), "ImagePopupDialog");
//            }
//        }
    }
}