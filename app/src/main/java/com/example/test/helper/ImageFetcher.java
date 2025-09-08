package com.example.test.helper;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.test.api.LambdaApi;
import com.example.test.api.RetrofitClient;
import com.example.test.model.ImageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageFetcher {
    private Call<ImageResponse> currentCall;

    // Fetch image and load into ImageView (with caching)
    public void fetchImage(Activity activity, Long vendorId, ImageView imageView, TextView placeholder) {
        // 1. Check cache
        String cachedImage = MenuCacheBustingHelper.getCachedImage(activity);
        if (cachedImage != null) {
            imageView.setVisibility(View.VISIBLE);
            if (placeholder != null) placeholder.setVisibility(View.GONE);
            Glide.with(activity.getApplicationContext())
                    .load(cachedImage)
                    .into(imageView);
            return; // ✅ Done, no API call needed
        }

        // 2. If not cached → Call Lambda
        LambdaApi api = RetrofitClient.getLambdaApi();

        // Cancel previous call if still running
        if (currentCall != null) currentCall.cancel();

        currentCall = api.getImage(vendorId);
        currentCall.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                Log.e("ImageFetcher", "Calling API with vendorId=" + vendorId);
                if (!call.isCanceled() && response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getImageUrl();
                    Log.e(imageUrl, imageUrl);

                    // 🔹 Save to cache
                    MenuCacheBustingHelper.saveImage(activity, imageUrl);

                    imageView.setVisibility(View.VISIBLE);
                    if (placeholder != null) placeholder.setVisibility(View.GONE);

                    // Load image
                    Glide.with(activity.getApplicationContext())
                            .load(imageUrl)
                            .into(imageView);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
                Log.e("ImageFetcher", "Calling API with vendorId=" + vendorId);
                if (!call.isCanceled()) t.printStackTrace();
                Log.e("ImageFetcher", "Couldn't fetch image.");
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