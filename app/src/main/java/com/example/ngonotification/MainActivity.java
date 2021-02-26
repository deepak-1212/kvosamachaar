package com.example.ngonotification;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngonotification.Model.TokenRequest;
import com.example.ngonotification.Model.TokenResponse;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    private String tokenupload;
    private TextView noNotification;

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ignored) {
        }
        return "02:00:00:00:00:00";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getToken();
        initViews();
        ArrayList<NotificationModel> notificationModels = new ArrayList<>();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        /*sqLiteDatabase.delete("notifications", null, null);

        for (int i = 0; i < 10; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("notification_title", "notification_title");
            contentValues.put("notification_body", "notification_body");
            contentValues.put("notification_url", "notification_url");

            sqLiteDatabase.insert("notifications", null, contentValues);
        }*/

        Cursor cursor = sqLiteDatabase.rawQuery("select * from notifications Order by notification_date desc", null, null);
        if (cursor.getCount() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noNotification.setVisibility(View.GONE);
            if (cursor.moveToFirst())
                do {
                    NotificationModel notificationModel = new NotificationModel();
                    notificationModel.setTitle(cursor.getString(cursor.getColumnIndex("notification_title")));
                    notificationModel.setBody(cursor.getString(cursor.getColumnIndex("notification_body")));

                    String url = "<a href='" + cursor.getString(cursor.getColumnIndex("notification_url")) + "'>Click here</a>";
                    //String url = "<a href='http://www.google.com/'>Click here</a>";

                    notificationModel.setUrl(Html.fromHtml(url));
                    notificationModel.setUrlSupport(cursor.getString(cursor.getColumnIndex("notification_url")));
                    notificationModels.add(notificationModel);
                } while (cursor.moveToNext());
        } else {
            recyclerView.setVisibility(View.GONE);
            noNotification.setVisibility(View.VISIBLE);
        }
        cursor.close();
        sqLiteDatabase.close();
        databaseHelper.close();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        NotificationAdaptor notificationAdaptor = new NotificationAdaptor(getApplicationContext(), notificationModels);
        recyclerView.setAdapter(notificationAdaptor);

    }

    private void initViews() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        recyclerView = findViewById(R.id.recyclerView);
        noNotification = findViewById(R.id.noNotification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (R.id.allnotification == item.getItemId()) {
            Log.i("TAG", "onOptionsItemSelected: Notification selected");

            boolean password = settings.getBoolean("password", false);

            if (!password) {

                new AdminPassword().show(getSupportFragmentManager(), "Dialog");

                /*LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(5, 5, 5, 5);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(50, 40, 50, 0);

                EditText editText = new EditText(this);
                editText.setPadding(20, 5, 20, 5);
                editText.setMinHeight(180);
                editText.setHint("Enter password");
                editText.setLayoutParams(layoutParams);


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(editText);
                builder.setNeutralButton("Submit", (dialog, which) -> {
                    //dialog.dismiss();
                    String password_edit = editText.getText().toString().trim();
                    Log.i("TAG", "onOptionsItemSelected: " + password_edit);

                    if (!password_edit.isEmpty()) {

                        if (password_edit.equals("password")) {
                            Toast.makeText(getApplicationContext(), "Correct password", Toast.LENGTH_SHORT).show();
                            editor = settings.edit();
                            editor.putBoolean("password", true);
                            editor.apply();

                            callIntent();
                        } else {
                            editText.setError("Wrong password");
                            Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                        }

                    }


                });

                builder.show();*/

            } else {
                callIntent();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void callIntent() {
        Intent intent = new Intent(getApplicationContext(), CreateNotification.class);
        startActivity(intent);

    }

    public void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    tokenupload = task.getResult().getToken();
                    Log.i("TAG", "run: Token: " + tokenupload);

                    String mac = getMacAddr();
                    TokenRequest tokenRequest = new TokenRequest(mac, tokenupload);

                    Retrofit retrofit = HelpDeskClient.getClient();
                    HelpdeskApi helpdeskApi = retrofit.create(HelpdeskApi.class);
                    Call<TokenResponse> call = helpdeskApi.TokenUpload(tokenRequest);

                    call.enqueue(new Callback<TokenResponse>() {
                        @Override
                        public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                            if (response.code() == 200) {
                                Log.i("TAG", "onResponse: " + response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenResponse> call, Throwable t) {
                            Log.i("TAG", "onFailure: " + t.getMessage());
                        }
                    });

                });
    }

}