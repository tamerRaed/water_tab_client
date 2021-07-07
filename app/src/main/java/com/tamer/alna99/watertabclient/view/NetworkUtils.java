package com.tamer.alna99.watertabclient.view;

import com.tamer.alna99.watertabclient.model.ApiInterface;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkUtils {

    private static NetworkUtils instance;
    public final static String BASE_URL = "https://miahy.herokuapp.com/";
    private final ApiInterface apiInterface;

    private NetworkUtils() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    public static NetworkUtils getInstance() {
        if (instance == null) {
            instance = new NetworkUtils();
        }
        return instance;
    }

    public ApiInterface getApiInterface() {
        return apiInterface;
    }

}
