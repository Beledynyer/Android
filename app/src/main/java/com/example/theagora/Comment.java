package com.example.theagora;

public class Comment {
    int commentId;

    String commentType;

    String content;

    String dateAndTimeOfCreation;

    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment(int commentId, String commentType, String content, String dateAndTimeOfCreation) {
        this.commentId = commentId;
        this.commentType = commentType;
        this.content = content;
        this.dateAndTimeOfCreation = dateAndTimeOfCreation;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getCommentType() {
        return commentType;
    }

    public String getContent() {
        return content;
    }

    public String getDateAndTimeOfCreation() {
        return dateAndTimeOfCreation;
    }
}
