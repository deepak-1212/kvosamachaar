package com.example.ngonotification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AdminPassword extends BottomSheetDialogFragment {

    private EditText password;
    private Button button;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_password, container, false);

        initViews(view);

        button.setOnClickListener(v -> {
            String password_edit = password.getText().toString().trim();

            if (!password_edit.isEmpty()) {

                if (password_edit.equals("password")) {

                    editor = settings.edit();
                    editor.putBoolean("password", true);
                    editor.apply();

                    Intent intent = new Intent(getContext(), CreateNotification.class);
                    startActivity(intent);
                } else {
                    password.setError("Wrong password");
                }

            }

        });

        return view;
    }

    private void initViews(View view) {
        password = view.findViewById(R.id.password);
        button = view.findViewById(R.id.submit);

        settings = PreferenceManager.getDefaultSharedPreferences(getContext());
    }
}
