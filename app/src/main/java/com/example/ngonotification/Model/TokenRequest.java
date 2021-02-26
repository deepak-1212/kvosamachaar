package com.example.ngonotification.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenRequest {

    @SerializedName("mac")
    @Expose
    private String mac;
    @SerializedName("token")
    @Expose
    private String token;

    public TokenRequest(String mac, String token) {
        this.mac = mac;
        this.token = token;
    }

}
