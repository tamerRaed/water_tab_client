package com.tamer.alna99.watertabclient.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tamer.alna99.watertabclient.R;


public class BottomSheetRating extends BottomSheetDialogFragment {
    private final OnRateAnswerClick onRateAnswerClick;
    float rate = 0;
    private RatingBar ratingBar;

    public BottomSheetRating(OnRateAnswerClick onRateAnswerClick) {
        this.onRateAnswerClick = onRateAnswerClick;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rate_driver, container, false);
        ratingBar = view.findViewById(R.id.ratingBar);
        Button btnRate = view.findViewById(R.id.rate);
        Button btnLater = view.findViewById(R.id.later);

        btnRate.setOnClickListener(view12 -> {
            rate = ratingBar.getRating();

            onRateAnswerClick.onRateClick(rate);
            this.dismiss();
        });

        btnLater.setOnClickListener(view1 -> {
            onRateAnswerClick.onLaterClick();
            this.dismiss();
        });

        return view;
    }

    public interface OnRateAnswerClick {
        void onRateClick(double rate);

        void onLaterClick();
    }
}
