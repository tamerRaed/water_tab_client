package com.tamer.alna99.watertabclient.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tamer.alna99.watertabclient.R;
import com.tamer.alna99.watertabclient.model.Result;
import com.tamer.alna99.watertabclient.model.SharedPrefs;
import com.tamer.alna99.watertabclient.viewmodel.LoginViewModel;
import com.tapadoo.alerter.Alerter;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText et_email, et_password;
    private String email, password;
    private Button btn_login;
    private ProgressBar progressBar;
    private LoginViewModel loginViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String shared = SharedPrefs.getUserEmail(this);
        if (!shared.equals("-1")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        initViews();
        loginViewModel = new LoginViewModel();
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
            loginViewModel.loginInfo().addObserver((observable, o) -> {
                Result result = (Result) o;
                switch (result.status) {
                    case SUCCESS:
                        String data = (String) result.data;
                        JsonObject root = new JsonParser().parse(data).getAsJsonObject();
                        boolean success = root.get("loginSuccess").getAsBoolean();

                        if (success) {
                            btn_login.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            JsonObject user = root.getAsJsonObject("user");
                            String id = user.get("_id").getAsString();
                            String username = user.get("name").getAsString();
                            String phone = user.get("phone").getAsString();
                            String email = user.get("email").getAsString();
                            JsonArray jsonArray = user.get("orders").getAsJsonArray();
                            SharedPrefs.setUserInfo(getApplicationContext(), id, username, email, phone);
                            SharedPrefs.saveOrders(getApplicationContext(), jsonArray);

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            String message = root.get("message").getAsString();
                            showAlerter(message);
                            btn_login.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        break;
                    case ERROR:
                        btn_login.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        showAlerter(getString(R.string.error));
                        break;
                }
            });
            loginViewModel.requestLogin(email, password);
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