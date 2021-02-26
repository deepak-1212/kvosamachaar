package com.example.ngonotification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HelpDeskClient {
    public static String BASE_URL = UrlList.APPURLSTRING + "android/";

    public static Retrofit retrofit = null;
    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build();

    public static Retrofit getClient() {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        return retrofit;
    }

}
