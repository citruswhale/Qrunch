package com.example.test.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static LambdaApi getLambdaApi(String method) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        if (retrofit == null && method.equals("fetchMenuImage")) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://tlhqgr3g47.execute-api.eu-north-1.amazonaws.com/") // replace
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        else if (retrofit == null && method.equals("generateQR")) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://vtr3u7yvff.execute-api.eu-north-1.amazonaws.com") // replace
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(LambdaApi.class);
    }
}
