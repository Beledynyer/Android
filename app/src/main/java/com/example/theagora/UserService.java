package com.example.theagora;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @GET("api/User/Login")
    Call<User> login(@Query("email") String email, @Query("password") String password);

    @POST("api/User/Register")
    Call<User> register(@Body User user);

    @GET("api/User/GetUserById")
    Call<User> getUserById(@Query("id") int id);
}
