package com.example.mess.screen;

import static com.example.mess.api.baseApiURL.GENERATE_QR_BASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mess.R;
import com.example.mess.api.LambdaApi;
import com.example.mess.api.RetrofitClient;
import com.example.mess.helper.LoadUserHelper;
import com.example.mess.helper.PrefsHelper;
import com.example.mess.model.QRRequestBody;
import com.example.mess.model.QRResponse;

import retrofit2.Call;

public class HomeActivity extends AppCompatActivity {

    private ImageButton buttonEditProfile;
    private Button buttonViewMenu;
    private ImageView imageViewQR;
    private TextView textViewQRPlaceHolder;

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
                public void onProfileLoaded(Long vendorId) {
                    if (vendorId != null) {
                        ImagePopupDialog.newInstance(vendorId).show(getSupportFragmentManager(), "ImagePopupDialog");
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
            ImagePopupDialog.newInstance(vendorId).show(getSupportFragmentManager(), "ImagePopupDialog");
        }
    }

    private void loadQR() {
        Long vendorId = PrefsHelper.getVendorId(this);
        String rollNo = PrefsHelper.getRollNo(this);

        if (vendorId == null || rollNo == null) {
            textViewQRPlaceHolder.setVisibility(View.VISIBLE);
            imageViewQR.setVisibility(View.GONE);
            return;
        }

        LambdaApi api = RetrofitClient.getLambdaApi(GENERATE_QR_BASE_URL);
        QRRequestBody requestBody = new QRRequestBody(vendorId.toString(), rollNo);

        Call<QRResponse> call = api.generateQR(requestBody);
        call.enqueue(new retrofit2.Callback<QRResponse>() {
            @Override
            public void onResponse(Call<QRResponse> call, retrofit2.Response<QRResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String dataUri = response.body().getQrImage();
                    String base64Data = dataUri.split(",")[1];

                    byte[] decodedBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                    imageViewQR.setImageBitmap(bitmap);
                    imageViewQR.setVisibility(View.VISIBLE);
                    textViewQRPlaceHolder.setVisibility(View.GONE);
                } else {
                    textViewQRPlaceHolder.setVisibility(View.VISIBLE);
                    imageViewQR.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<QRResponse> call, Throwable t) {
                t.printStackTrace();
                textViewQRPlaceHolder.setVisibility(View.VISIBLE);
                imageViewQR.setVisibility(View.GONE);
            }
        });
    }
}