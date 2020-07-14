package com.example.Post_Pc_7.Server;

import com.example.Post_Pc_7.Data.TokenResponse;
import com.example.Post_Pc_7.Data.User;
import com.example.Post_Pc_7.Data.UserResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MyServerInterface {

    @GET("/users/0")
    Call<User> connectivityCheck();

    @GET("/users/{username}/token/")
    Call<TokenResponse> getUserToken(@Path("username") String username);

    @GET("/user/")
    Call<UserResponse> getUser(@Header("Authorization") String token);

    @Headers({"Content-Type: application/json"})
    @POST("/user/edit/")
    Call<UserResponse> updateUser(@Header("Authorization") String token, @Body JsonObject json);

}
