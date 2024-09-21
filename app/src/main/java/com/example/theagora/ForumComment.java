package com.example.theagora;

public class ForumComment {
    int commentId;
    int postId;
    int userId;

    public ForumComment(int commentId, int postId, int userId) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
    }

    public int getCommentId() {
        return commentId;
    }

    public int getPostId() {
        return postId;
    }

    public int getUserId() {
        return userId;
    }
}
