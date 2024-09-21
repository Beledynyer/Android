package com.example.theagora;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface ForumCommentService {
    @GET("api/ForumComment/GetForumComments")
    Call<List<ForumComment>> getCommentsForPost(@Query("postId") int postId);

    @POST("api/ForumComment/AddComment")
    Call<ForumComment> addComment(@Body CommentDto commentDto);

    @DELETE("api/ForumComment/DeleteComment")
    Call<Void> deleteComment(@Query("commentId") int commentId);
}

class CommentDto {
    public String content;
    public int userId;
    public int postId;

    public CommentDto(String content, int userId, int postId) {
        this.content = content;
        this.userId = userId;
        this.postId = postId;
    }
}