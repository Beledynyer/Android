package com.example.theagora;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LikeService {
    @POST("api/Likes/toggle")
    Call<LikeResponse> toggleLike(@Query("userId") int userId, @Query("forumPostId") int forumPostId);

    @GET("api/Likes/count")
    Call<LikeCountResponse> getLikeCount(@Query("forumPostId") int forumPostId);

    @GET("api/Likes/hasliked")
    Call<HasLikedResponse> hasUserLikedPost(@Query("userId") int userId, @Query("forumPostId") int forumPostId);
}