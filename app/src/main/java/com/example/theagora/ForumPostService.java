package com.example.theagora;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface ForumPostService {
    @GET("api/ForumPost/GetForumPosts")
    Call<List<ForumPost>> getForumPosts();

    @POST("api/ForumPost/CreateForumPost")
    Call<ForumPost> createForumPost(@Body ForumPost post);

    @GET("api/ForumPost/GetForumPostById")
    Call<ForumPost> getForumPostById(@Query("id") int id);
}

