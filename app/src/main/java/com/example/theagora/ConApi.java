package com.example.theagora;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConApi {
    Retrofit retrofit;
    public ConApi(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5280/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }
    public UserService getUserService(){
        return retrofit.create(UserService.class);
    }
}
