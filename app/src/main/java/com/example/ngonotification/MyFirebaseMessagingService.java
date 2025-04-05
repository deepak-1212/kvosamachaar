package com.example.ngonotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    // This is the Notification Channel ID. More about this in the next section
    public static final String CHANNEL_ID = "notification_kvo";
    private static final int NOTIFICATION_ID = 1;

    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.i("TAG", "onNewToken: Called");

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) throws NullPointerException {
        String title = null, body = null, url = null;

        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);

        try {
            String data = object.getString("data");
            JSONObject jsonObject = new JSONObject(data);
            title = jsonObject.getString("title");
            body = jsonObject.getString("body");
            url = jsonObject.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        insertRecord(title, body, url);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setColor(getResources().getColor(R.color.black))
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setNumber(1)
                        .setSound(soundUri)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(body));

                manager.notify(NOTIFICATION_ID, builder.build());

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
                CharSequence name = "chanel_name";
                String description = "desc";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.setSound(soundUri, att);
                channel.setName("SGGSC");
                channel.setShowBadge(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                channel.enableLights(true);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
                Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title) // title for notification
                        /* .setContentText(body) */// message for notification
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setNumber(7)
                        .setOnlyAlertOnce(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(body));
                manager.notify(NOTIFICATION_ID, notification.build());
            } else {
                builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
                manager.notify(NOTIFICATION_ID, builder.build());
            }


        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void insertRecord(String title, String body, String url) {
        Runnable runnable = () -> {
            SQLiteDatabase sqLiteDatabase;
            try (DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext())) {
                sqLiteDatabase = databaseHelper.getWritableDatabase();
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put("notification_title", title);
            contentValues.put("notification_body", body);
            contentValues.put("notification_url", url);

            sqLiteDatabase.insert("notifications", null, contentValues);

        };

        runnable.run();
    }
}
