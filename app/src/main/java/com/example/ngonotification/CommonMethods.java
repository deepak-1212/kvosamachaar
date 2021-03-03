package com.example.ngonotification;

import android.content.Context;
import android.net.ConnectivityManager;

public class CommonMethods {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo()!= null) return true;
        else return false;
    }

}
