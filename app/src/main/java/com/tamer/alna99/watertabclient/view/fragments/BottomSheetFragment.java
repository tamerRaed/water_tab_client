package com.tamer.alna99.watertabclient.view.fragments;

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

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private final String name;
    private final String email;
    private final String phone;
    private final double rate;
    private final OnOrderClick orderClick;

    public BottomSheetFragment(String name, String email, String phone, double rate, OnOrderClick orderClick) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.rate = rate;
        this.orderClick = orderClick;
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

        Log.d("dddd", name);

        tv_name.setText(name);
        tv_rate.setText(String.valueOf(rate));
        tv_email.setText(email);
        tv_phone.setText(phone);

        btn_order.setOnClickListener(view1 -> {
            orderClick.onClick();
            this.dismiss();
        });

        return view;
    }

    public interface OnOrderClick {
        void onClick();
    }
}
