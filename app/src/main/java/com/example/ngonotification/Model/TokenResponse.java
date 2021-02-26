package com.example.ngonotification.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("AffectedRows")
    @Expose
    private int AffectedRows;
    @SerializedName("Message")
    @Expose
    private String message;

    public int getAffectedRows() {
        return AffectedRows;
    }

    public String getMessage() {
        return message;
    }
}
