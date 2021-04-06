package com.tamer.alna99.watertabclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText et_email, et_username, et_phone, et_password;
    private String email, username, phone, password;
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        networkUtils = NetworkUtils.getInstance();
    }

    private void initViews() {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_username = findViewById(R.id.et_username);
        et_phone = findViewById(R.id.et_phone);
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
        if (checkFields()) {
            Call<ResponseBody> responseBodyCall = networkUtils.getApiInterface().register(username, email, password);
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            JsonObject root = new JsonParser().parse(response.body().string()).getAsJsonObject();
                            boolean success = root.get("success").getAsBoolean();
                            if (success) {
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                String message = root.get("success").getAsString();
                                showAlerter(message);
                            }
                        } else {
                            showAlerter(getString(R.string.email_is_used));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

                }
            });
        }
    }

    public void login(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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