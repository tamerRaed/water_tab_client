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

public class RegisterViewModel {
    private final NetworkUtils networkUtils;
    private final DataWrapper<Result> dataWrapper;

    public RegisterViewModel() {
        networkUtils = NetworkUtils.getInstance();
        dataWrapper = new DataWrapper<>();
    }

    public DataWrapper<Result> registerInfo() {
        return dataWrapper;
    }

    public void requestRegister(String name, String email, String password, String phone) {
        networkUtils.getApiInterface().register(name, email, password, phone).enqueue(new Callback<ResponseBody>() {
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
