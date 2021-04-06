package com.tamer.alna99.watertabclient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    String LOGIN_PATH = "api/users/logInUser";
    String REGISTER_PATH = "api/users/registerUser";
    String PARAM_EMAIL = "email";
    String PARAM_PASSWORD = "password";
    String PARAM_NAME = "name";

    @FormUrlEncoded
    @POST(LOGIN_PATH)
    Call<ResponseBody> login(@Field(PARAM_EMAIL) String email, @Field(PARAM_PASSWORD) String password);

    @FormUrlEncoded
    @POST(REGISTER_PATH)
    Call<ResponseBody> register(@Field(PARAM_NAME) String name, @Field(PARAM_EMAIL) String email, @Field(PARAM_PASSWORD) String password);

}
