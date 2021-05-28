package com.tamer.alna99.watertabclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tamer.alna99.watertabclient.model.findDriver.FindDriverResponse;

public class DriverInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);
        TextView tv = findViewById(R.id.tv);
        Intent intent = getIntent();

        FindDriverResponse driver = intent.getParcelableExtra("driver");

        if (driver != null) {
            Log.d("dddd", "FindDriverResponse not null");
            if (driver.getDriver() != null) {
                Log.d("dddd", "getDriver not null");
            } else {
                Log.d("dddd", "getDriver null");
            }
        } else {
            Log.d("dddd", "FindDriverResponse null");

        }
        Log.d("dddd", driver.toString());
        Log.d("dddd", driver.getDriver().getName() +
                driver.getDriver().getEmail());
        tv.setText(driver.getDriver().getName() + "\n"
                + driver.getDriver().getEmail());
    }
}