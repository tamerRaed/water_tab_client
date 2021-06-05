package com.tamer.alna99.watertabclient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    String LOGIN_PATH = "api/users/logInUser";
    String REGISTER_PATH = "api/users/registerUser";
    String FIND_DRIVER_PATH = "api/users/findNearByDriver";
    String PARAM_EMAIL = "email";
    String PARAM_PASSWORD = "password";
    String PARAM_NAME = "name";
    String PARAM_PHONE = "phone";
    String PARAM_LAT = "lat";
    String PARAM_LONG = "long";

    @FormUrlEncoded
    @POST(LOGIN_PATH)
    Call<ResponseBody> login(@Field(PARAM_EMAIL) String email, @Field(PARAM_PASSWORD) String password);

    @FormUrlEncoded
    @POST(REGISTER_PATH)
    Call<ResponseBody> register(@Field(PARAM_NAME) String name,
                                @Field(PARAM_EMAIL) String email,
                                @Field(PARAM_PASSWORD) String password,
                                @Field(PARAM_PHONE) String phone);

    @FormUrlEncoded
    @POST(FIND_DRIVER_PATH)
    Call<ResponseBody> findDriver(@Field(PARAM_LONG) String lon, @Field(PARAM_LAT) String lat);

}
