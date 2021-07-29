package com.tamer.alna99.watertabclient.viewmodel;

import android.util.Log;

import com.tamer.alna99.watertabclient.model.DataWrapper;
import com.tamer.alna99.watertabclient.model.Result;
import com.tamer.alna99.watertabclient.view.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomepageViewModel {
    private final NetworkUtils networkUtils;
    private final DataWrapper<Result<String>> dataWrapper;

    public HomepageViewModel() {
        networkUtils = NetworkUtils.getInstance();
        dataWrapper = new DataWrapper<>();
    }

    public DataWrapper<Result<String>> getInfo() {
        return dataWrapper;
    }

    public void requestOrderDriver(String clintID, String driverID, String clientName, double lat, double lon) {
        networkUtils.getApiInterface().orderDriver(clintID, driverID, clientName, lat, lon).enqueue(new Callback<ResponseBody>() {
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

    public void requestFindDriver(String lon, String lat) {
        networkUtils.getApiInterface().findDriver(lon, lat).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                Log.d("dddd", "onResponse");
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        dataWrapper.setData(Result.success(response.body().string()));
                        Log.d("dddd", response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    dataWrapper.setData(Result.error("Error"));
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.d("dddd", "onFailure");
                dataWrapper.setData(Result.error("Error"));
            }
        });
    }

    public void requestRateDriver(String driverID, int rate) {
        networkUtils.getApiInterface().rateDriver(driverID, rate).enqueue(new Callback<ResponseBody>() {
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
