package com.example.test.api;

import com.example.test.model.ImageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LambdaApi {
    @GET("fetchMenuImage")
    Call<ImageResponse> getImage(@Query("vendorId") Long vendorId);
}
