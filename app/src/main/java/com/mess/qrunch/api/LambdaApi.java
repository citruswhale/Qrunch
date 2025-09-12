package com.mess.qrunch.api;

import com.mess.qrunch.model.ImageResponse;
import com.mess.qrunch.model.QRRequestBody;
import com.mess.qrunch.model.QRResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LambdaApi {
    @GET("fetchMenuImage")
    Call<ImageResponse> getImage(@Query("vendorId") Long vendorId);

    @POST("generateQR") // Lambda endpoint
    Call<QRResponse> generateQR(@Body QRRequestBody body);
}
