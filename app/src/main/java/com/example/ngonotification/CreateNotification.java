package com.example.ngonotification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ngonotification.Model.NotificationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateNotification extends AppCompatActivity {

    private EditText title, body, url;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        initViews();

        submit.setOnClickListener(v -> {
            String titleText, bodyText, urlText;
            titleText = title.getText().toString().trim();
            bodyText = body.getText().toString().trim();
            urlText = url.getText().toString().trim();

            if (titleText.isEmpty() || bodyText.isEmpty() || urlText.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                Retrofit retrofit = HelpDeskClient.getClient();
                HelpdeskApi helpdeskApi = retrofit.create(HelpdeskApi.class);
                Call<NotificationResponse> call = helpdeskApi.Notification(titleText, bodyText, urlText);

                call.enqueue(new Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                        if (response.code() == 200) {
                            Log.i("TAG", "onResponse: " + response.body());
                            NotificationResponse notificationResponse = response.body();
                            if (notificationResponse.getMsg().equalsIgnoreCase("Notification Send")) {
                                title.setText("");
                                body.setText("");
                                url.setText("");
                                title.hasFocus();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationResponse> call, Throwable t) {
                        Log.i("TAG", "onResponse: " + t.getMessage());
                    }
                });
            }

        });
    }

    private void initViews() {
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        url = findViewById(R.id.url);
        submit = findViewById(R.id.submit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}