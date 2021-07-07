package com.tamer.alna99.watertabclient.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tamer.alna99.watertabclient.R;
import com.tamer.alna99.watertabclient.model.Result;
import com.tamer.alna99.watertabclient.model.SharedPrefs;
import com.tamer.alna99.watertabclient.viewmodel.RegisterViewModel;
import com.tapadoo.alerter.Alerter;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText et_email, et_username, et_phone, et_password;
    private Button btn_register;
    private ProgressBar progressBar;
    private String email, username, phone, password;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        registerViewModel = new RegisterViewModel();
    }

    private void initViews() {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_username = findViewById(R.id.et_username);
        et_phone = findViewById(R.id.et_phone);
        btn_register = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.register_progressBar);
    }

    private boolean checkFields() {
        if (!TextUtils.isEmpty(et_email.getText().toString())) {
            if (!TextUtils.isEmpty(et_username.getText().toString())) {
                if (!TextUtils.isEmpty(et_phone.getText().toString())) {
                    if (!TextUtils.isEmpty(et_password.getText().toString())) {
                        email = et_email.getText().toString();
                        username = et_username.getText().toString();
                        phone = et_phone.getText().toString();
                        password = et_password.getText().toString();
                        return true;
                    } else {
                        et_password.setError(getString(R.string.password_empty));
                        return false;
                    }
                } else {
                    et_phone.setError(getString(R.string.phone_empty));
                    return false;
                }
            } else {
                et_username.setError(getString(R.string.username_empty));
                return false;
            }
        } else {
            et_email.setError(getString(R.string.email_empty));
            return false;
        }
    }

    public void register(View view) {
        btn_register.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (checkFields()) {
            registerViewModel.registerInfo().addObserver((observable, o) -> {
                Result result = (Result) o;
                switch (result.status) {
                    case SUCCESS:
                        String data = (String) result.data;
                        JsonObject root = new JsonParser().parse(data).getAsJsonObject();
                        boolean success = root.get("success").getAsBoolean();
                        if (success) {
                            JsonObject user = root.getAsJsonObject("user");
                            String id = user.get("_id").getAsString();
                            SharedPrefs.setUserInfo(getApplicationContext(), id, username, email, phone);
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        } else {
                            String message = root.get("success").getAsString();
                            showAlerter(message);
                        }
                        btn_register.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        break;
                    case ERROR:
                        btn_register.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        showAlerter(getString(R.string.error));
                        break;
                }
            });
            registerViewModel.requestRegister(username, email, password, phone);
        }
    }

    public void login(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    public void back(View view) {
        onBackPressed();
        finish();
    }

    private void showAlerter(String message) {
        Alerter.create(this)
                .setText(message)
                .setDuration(3000)
                .setBackgroundColorRes(R.color.teal_200)
                .show();
    }
}