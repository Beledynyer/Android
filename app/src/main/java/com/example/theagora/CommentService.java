package com.example.theagora;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CommentService {
    @GET("api/Comment/GetCommentById")
    Call<Comment> getComment(@Query("id") int id);
}