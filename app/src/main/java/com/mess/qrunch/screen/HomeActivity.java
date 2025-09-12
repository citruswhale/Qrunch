package com.mess.qrunch.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mess.qrunch.R;
import com.mess.qrunch.helper.LoadUserHelper;
import com.mess.qrunch.helper.PrefsHelper;
import com.mess.qrunch.helper.QRImageFetcher;

public class HomeActivity extends AppCompatActivity {

    private ImageButton buttonEditProfile;
    private Button buttonViewMenu;
    private ImageView imageViewQR;
    private TextView textViewQRPlaceHolder;
    private QRImageFetcher qrImageFetcher;

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
        imageViewQR = findViewById(R.id.imageViewQR);
        textViewQRPlaceHolder = findViewById(R.id.textViewPlaceholder);

        qrImageFetcher = new QRImageFetcher();

        loadQR();

        buttonEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        buttonViewMenu.setOnClickListener(view -> {
            loadMenu();
        });
    }

    private void loadMenu() {
        Long vendorId = PrefsHelper.getVendorId(this);

        if (vendorId == null) {
            LoadUserHelper loadUserHelper = new LoadUserHelper();
            loadUserHelper.loadUserProfile(this, new LoadUserHelper.ProfileLoadCallback() {
                @Override
                public void onProfileLoaded(Long vendorId, String rollNo, String name) {
                    if (vendorId != null) {
                        ImagePopupDialog.newInstance(vendorId).show(getSupportFragmentManager(), "ImagePopupDialog");
                    } else {
                        Log.e("HomeActivity", "VendorId is still null");
                        Toast.makeText(HomeActivity.this, "No vendor chosen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("HomeActivity", "Failed to load profile", e);
                }
            });
        } else {
            ImagePopupDialog.newInstance(vendorId).show(getSupportFragmentManager(), "ImagePopupDialog");
        }
    }

    private void loadQR() {
        Long vendorId = PrefsHelper.getVendorId(this);
        String rollNo = PrefsHelper.getRollNo(this);

        if (vendorId == null || rollNo == null) {
            LoadUserHelper loadUserHelper = new LoadUserHelper();
            loadUserHelper.loadUserProfile(this, new LoadUserHelper.ProfileLoadCallback() {
                @Override
                public void onProfileLoaded(Long vendorId, String rollNo, String name) {
                    if (vendorId != null) {
                        qrImageFetcher.fetchImage(HomeActivity.this, vendorId, rollNo, imageViewQR, textViewQRPlaceHolder);
                    } else {
                        Log.e("HomeActivity", "VendorId is still null");
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("HomeActivity", "Failed to load profile", e);
                }
            });
        } else {
            qrImageFetcher.fetchImage(this, vendorId, rollNo, imageViewQR, textViewQRPlaceHolder);
        }
    }
}