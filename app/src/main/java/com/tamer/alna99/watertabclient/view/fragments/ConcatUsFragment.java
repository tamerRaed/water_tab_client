package com.tamer.alna99.watertabclient.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.tamer.alna99.watertabclient.R;

public class ConcatUsFragment extends Fragment {

    @SuppressLint("IntentReset")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concat_us, container, false);
        TextInputEditText content_et = view.findViewById(R.id.concat_us_et_body);
        TextInputEditText subject_et = view.findViewById(R.id.concat_us_et_subject);
        Button sendEmail = view.findViewById(R.id.concat_us_btn_send);

        sendEmail.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(subject_et.getText().toString())) {
                if (!TextUtils.isEmpty(content_et.getText().toString())) {
                    String subject = subject_et.getText().toString();
                    String content = content_et.getText().toString();

                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setData(Uri.parse("mailto:"));
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{"tamer.alhasan.apps@gmail.com"});
                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
                    email.putExtra(Intent.EXTRA_TEXT, content);
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Send mail..."));
                } else {
                    content_et.setError(getString(R.string.content_error));
                }
            } else {
                subject_et.setError(getString(R.string.subject_error));
            }

        });
        return view;
    }
}