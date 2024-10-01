package com.example.theagora;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ForumPostService {
    @GET("api/ForumPost/GetApprovedForumPosts")
    Call<List<ForumPost>> getApprovedForumPosts();

    @GET("api/ForumPost/GetUnapprovedForumPosts")
    Call<List<ForumPost>> getUnapprovedForumPosts();

    @POST("api/ForumPost/CreateForumPost")
    Call<ForumPost> createForumPost(@Body ForumPost post);

    @GET("api/ForumPost/GetForumPostById")
    Call<ForumPost> getForumPostById(@Query("id") int id);

    @DELETE("api/ForumPost/DeleteForumPost")
    Call<Void> deleteForumPost(@Query("id") int id);

    @PUT("api/ForumPost/ApproveForumPost/{id}")
    Call<ForumPost> approveForumPost(@Path("id") int id);

    @PUT("api/ForumPost/UpdateNumberOfLikes/{id}")
    Call<ForumPost> updateNumberOfLikes(@Path("id") int id, @Body int numberOfLikes);
}