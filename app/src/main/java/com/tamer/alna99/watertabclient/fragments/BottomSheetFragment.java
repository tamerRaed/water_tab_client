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
import com.tamer.alna99.watertabclient.R;
import com.tamer.alna99.watertabclient.model.findDriver.Driver;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private final Driver driver;

    public BottomSheetFragment(Driver driver) {
        this.driver = driver;
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


        tv_name.setText(driver.getName());
        tv_rate.setText("4.5");
        tv_email.setText(driver.getEmail());
        tv_phone.setText("121212");

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Order driver
                Log.d("dddd", "Order");
            }
        });

        return view;
    }

}
