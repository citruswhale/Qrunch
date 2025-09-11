package com.example.test.api;

import com.example.test.model.ImageResponse;
import com.example.test.model.QRRequestBody;
import com.example.test.model.QRResponse;

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
