package com.tamer.alna99.watertabclient.viewmodel;

import com.tamer.alna99.watertabclient.model.DataWrapper;
import com.tamer.alna99.watertabclient.model.Result;
import com.tamer.alna99.watertabclient.view.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel {

    private final NetworkUtils networkUtils;
    private final DataWrapper<Result> dataWrapper;

    public LoginViewModel() {
        networkUtils = NetworkUtils.getInstance();
        dataWrapper = new DataWrapper<>();
    }

    public DataWrapper<Result> loginInfo() {
        return dataWrapper;
    }

    public void requestLogin(String email, String password) {
        networkUtils.getApiInterface().login(email, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        dataWrapper.setData(Result.success(response.body().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    dataWrapper.setData(Result.error("Error"));
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                dataWrapper.setData(Result.error("Error"));
            }
        });
    }
}
