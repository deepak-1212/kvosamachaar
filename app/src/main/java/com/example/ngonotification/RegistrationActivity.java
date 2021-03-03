package com.example.ngonotification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ngonotification.Model.TokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.ngonotification.MainActivity.getMacAddr;

public class RegistrationActivity extends AppCompatActivity {

    private EditText name, number;
    private Button submit;
    private String str_name, str_number;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();


    }

    private void initViews() {

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            str_name = name.getText().toString().trim();
            str_number = number.getText().toString().trim();

            if (str_name.isEmpty()) {
                name.setError(getResources().getString(R.string.field_not_blank));
            } else if (str_number.isEmpty()) {
                number.setError(getResources().getString(R.string.field_not_blank));
            } else {
                String mac = getMacAddr();

                if (CommonMethods.isOnline(getApplicationContext()))
                    uploadData(str_name, str_number, mac);
                else
                    Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadData(String str_name, String str_number, String mac) {

        ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = HelpDeskClient.getClient();
        HelpdeskApi helpdeskApi = retrofit.create(HelpdeskApi.class);
        Call<TokenResponse> call = helpdeskApi.Register(str_name, str_number, mac);

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.code() == 200) {
                    Log.i("TAG", "onResponse: " + response.body());
                    editor = settings.edit();
                    editor.putString("name", str_name);
                    editor.putString("number", str_number);
                    editor.apply();

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    finish();
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();
                Log.i("TAG", "onFailure: " + t.getMessage());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

}