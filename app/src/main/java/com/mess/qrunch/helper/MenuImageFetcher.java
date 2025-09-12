package com.mess.qrunch.helper;

import static com.mess.qrunch.api.baseApiURL.FETCH_MENU_IMAGE_BASE_URL;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.mess.qrunch.api.LambdaApi;
import com.mess.qrunch.api.RetrofitClient;
import com.mess.qrunch.model.ImageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuImageFetcher {
    private Call<ImageResponse> currentCall;

    // Fetch image and load into ImageView (with caching)
    public void fetchImage(Activity activity, Long vendorId, ImageView imageViewMenu, TextView textViewMenuPlaceholder) {
        // 1. Check cache
        String cachedImage = MenuCacheBustingHelper.getCachedImage(activity);
        if (cachedImage != null) {
            imageViewMenu.setVisibility(View.VISIBLE);
            if (textViewMenuPlaceholder != null) textViewMenuPlaceholder.setVisibility(View.GONE);
            Glide.with(activity.getApplicationContext())
                    .load(cachedImage)
                    .into(imageViewMenu);
            return; // ✅ Done, no API call needed
        }

        // 2. If not cached → Call Lambda
        LambdaApi api = RetrofitClient.getLambdaApi(FETCH_MENU_IMAGE_BASE_URL);

        // Cancel previous call if still running
        if (currentCall != null) currentCall.cancel();

        currentCall = api.getImage(vendorId);
        currentCall.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                Log.e("MenuImageFetcher", "Calling API with vendorId=" + vendorId);
                if (!call.isCanceled() && response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getImageUrl();

                    // 🔹 Save to cache
                    MenuCacheBustingHelper.saveImage(activity, imageUrl);

                    imageViewMenu.setVisibility(View.VISIBLE);
                    if (textViewMenuPlaceholder != null) textViewMenuPlaceholder.setVisibility(View.GONE);

                    // Load image
                    Glide.with(activity.getApplicationContext())
                            .load(imageUrl)
                            .into(imageViewMenu);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
                Log.e("MenuImageFetcher", "Calling API with vendorId=" + vendorId);
                if (!call.isCanceled()) t.printStackTrace();
                Log.e("MenuImageFetcher", "Couldn't fetch image.");
                imageViewMenu.setVisibility(View.GONE);
                if (textViewMenuPlaceholder != null) textViewMenuPlaceholder.setVisibility(View.VISIBLE);
            }
        });
    }

    // Cancel ongoing request if Activity stops
    public void cancel() {
        if (currentCall != null) {
            currentCall.cancel();
        }
    }
}