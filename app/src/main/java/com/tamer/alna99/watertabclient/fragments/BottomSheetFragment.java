package com.tamer.alna99.watertabclient.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tamer.alna99.watertabclient.NetworkUtils;
import com.tamer.alna99.watertabclient.R;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private final String driverID;
    private final String lat;
    private final String lon;
    private NetworkUtils networkUtils;

    public BottomSheetFragment(String driverID, String lat, String lon) {
        this.driverID = driverID;
        this.lat = lat;
        this.lon = lon;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_driver_dialog, container, false);
        TextView tv_name = view.findViewById(R.id.tv_name);
        TextView tv_rate = view.findViewById(R.id.tv_rate);
        TextView tv_email = view.findViewById(R.id.tv_email);
        TextView tv_phone = view.findViewById(R.id.tv_phone);
        Button btn_order = view.findViewById(R.id.btn_order);

        networkUtils = NetworkUtils.getInstance();
//        tv_name.setText(driver.getName());
//        tv_rate.setText("4.5");
//        tv_email.setText(driver.getEmail());
//        tv_phone.setText("121212");

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> orderDriverResponse = networkUtils.getApiInterface().orderDriver(
                        "60bb2b4807608000040541c5",
                        driverID,
                        lat,
                        lon);
                orderDriverResponse.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            Log.d("dddd", "onResponse");
                            Log.d("dddd", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

        return view;
    }

}
