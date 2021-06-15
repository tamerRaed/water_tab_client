package com.tamer.alna99.watertabclient;

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
import com.tamer.alna99.watertabclient.model.SharedPrefs;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText et_email, et_password;
    private String email, password;
    private NetworkUtils networkUtils;
    private Button btn_login;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        networkUtils = NetworkUtils.getInstance();
        initViews();
    }

    private void initViews() {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.login_progressBar);
    }

    private boolean checkFields() {
        if (!TextUtils.isEmpty(et_email.getText().toString())) {
            if (!TextUtils.isEmpty(et_password.getText().toString())) {
                email = et_email.getText().toString();
                password = et_password.getText().toString();
                return true;
            } else {
                et_password.setError(getString(R.string.password_empty));
                return false;
            }
        } else {
            et_email.setError(getString(R.string.email_empty));
            return false;
        }
    }

    public void login(View view) {
        btn_login.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        if (checkFields()) {
            Call<ResponseBody> responseBodyCall = networkUtils.getApiInterface().login(email, password);
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    try {
                        assert response.body() != null;
                        JsonObject root = new JsonParser().parse(response.body().string()).getAsJsonObject();
                        boolean success = root.get("loginSuccess").getAsBoolean();

                        if (success) {
                            btn_login.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            JsonObject user = root.getAsJsonObject("user");

                            String id = user.get("_id").getAsString();
                            String username = user.get("name").getAsString();
                            String phone = user.get("phone").getAsString();
                            String email = user.get("email").getAsString();

                            SharedPrefs.setUserInfo(getApplicationContext(), id, username, email, phone);

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            String message = root.get("message").getAsString();
                            showAlerter(message);
                            btn_login.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    btn_login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    showAlerter(getString(R.string.error));
                }
            });
        }
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void showAlerter(String message) {
        Alerter.create(this)
                .setText(message)
                .setDuration(3000)
                .setBackgroundColorRes(R.color.teal_200)
                .show();
    }

}