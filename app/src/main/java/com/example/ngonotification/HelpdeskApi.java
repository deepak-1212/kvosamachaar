package com.example.ngonotification;

import com.example.ngonotification.Model.NotificationResponse;
import com.example.ngonotification.Model.TokenRequest;
import com.example.ngonotification.Model.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HelpdeskApi {

    @Headers("Content-Type: application/json")
    @POST("token_update.php")
    Call<TokenResponse> TokenUpload(@Body TokenRequest tokenRequest);


    @GET("notification.php")
    Call<NotificationResponse> Notification(@Query("title") String title,
                                            @Query("message") String message,
                                            @Query("url") String url);

    @GET("registration.php")
    Call<TokenResponse> Register(@Query("name") String title,
                                 @Query("number") String message,
                                 @Query("mac") String url);

}
