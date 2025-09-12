package com.mess.qrunch.helper;

import static com.mess.qrunch.api.baseApiURL.GENERATE_QR_BASE_URL;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mess.qrunch.api.LambdaApi;
import com.mess.qrunch.api.RetrofitClient;
import com.mess.qrunch.model.QRRequestBody;
import com.mess.qrunch.model.QRResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class QRImageFetcher {

    Call<QRResponse> currentCall;
    private ImageView imageViewQR;
    private TextView textViewQRPlaceHolder;

    public void fetchImage(Activity activity, Long vendorId, String rollNo, ImageView imageViewQR, TextView textViewQRPlaceHolder) {

        this.imageViewQR = imageViewQR;
        this.textViewQRPlaceHolder = textViewQRPlaceHolder;

        String cachedQR = QRCacheBustingHelper.getCachedQR(activity);

        // 1️⃣ Check cache first
        if (cachedQR != null) {
            showQR(cachedQR);
            return;
        }

        // 2️⃣ Fetch from API if not cached
        LambdaApi api = RetrofitClient.getLambdaApi(GENERATE_QR_BASE_URL);
        QRRequestBody requestBody = new QRRequestBody(vendorId.toString(), rollNo);

        if (currentCall != null) currentCall.cancel();

        currentCall = api.generateQR(requestBody);
        currentCall.enqueue(new Callback<QRResponse>() {
            @Override
            public void onResponse(@NonNull Call<QRResponse> call, @NonNull retrofit2.Response<QRResponse> response) {
                Log.e("QRImageFetcher", "Calling API with vendorId=" + vendorId + " and rollNo=" + rollNo);
                if (!call.isCanceled() && response.isSuccessful() && response.body() != null) {
                    String dataUri = response.body().getQrImage();
                    String base64Data = dataUri.split(",")[1];

                    // Save to cache for this month
                    QRCacheBustingHelper.saveQR(activity, base64Data);

                    showQR(base64Data);
                } else {
                    textViewQRPlaceHolder.setVisibility(View.VISIBLE);
                    imageViewQR.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<QRResponse> call, Throwable t) {
                Log.e("QRImageFetcher", "Calling API with vendorId=" + vendorId + " and rollNo=" + rollNo);
                t.printStackTrace();
                Log.e("QRImageFetcher", "Couldn't fetch QR.");
                textViewQRPlaceHolder.setVisibility(View.VISIBLE);
                imageViewQR.setVisibility(View.GONE);
            }
        });
    }

    private void showQR(String base64Data) {
        byte[] decodedBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
        android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        imageViewQR.setImageBitmap(bitmap);
        imageViewQR.setVisibility(View.VISIBLE);
        textViewQRPlaceHolder.setVisibility(View.GONE);
    }
}
