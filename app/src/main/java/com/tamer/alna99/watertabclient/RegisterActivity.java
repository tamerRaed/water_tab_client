package com.tamer.alna99.watertabclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tamer.alna99.watertabclient.model.SharedPrefs;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText et_email, et_username, et_phone, et_password;
    private Button btn_register;
    private ProgressBar progressBar;
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
            Call<ResponseBody> responseBodyCall = networkUtils.getApiInterface().register(username, email, password, phone);
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    Log.d("dddd", "onResponse");
                    try {
                        if (response.body() != null) {
                            JsonObject root = new JsonParser().parse(response.body().string()).getAsJsonObject();
                            boolean success = root.get("success").getAsBoolean();
                            if (success) {
                                JsonObject user = root.getAsJsonObject("user");
                                Log.d("ddd", user.toString());

                                String id = user.get("_id").getAsString();
                                SharedPrefs.setUserInfo(getApplicationContext(), id, username, email, phone, password);
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                String message = root.get("success").getAsString();
                                showAlerter(message);
                            }
                        } else {
                            showAlerter(getString(R.string.email_is_used));
                        }
                        btn_register.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    btn_register.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    showAlerter(getString(R.string.error));
                }
            });
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