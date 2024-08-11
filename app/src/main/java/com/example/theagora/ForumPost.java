package com.example.theagora;

import java.util.Date;

public class ForumPost {
    private int postId;
    private Integer userId;
    private String content;
    private Date dateAndTimeOfCreation;
    private Integer numberOfLikes;
    private Boolean isApproved;
    private String image;
    private String tags;
    private User user;

    public ForumPost(int postId, Integer userId, String content, Date dateAndTimeOfCreation, Integer numberOfLikes, Boolean isApproved, String image, String tags,User user) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.dateAndTimeOfCreation = dateAndTimeOfCreation;
        this.numberOfLikes = numberOfLikes;
        this.isApproved = isApproved;
        this.image = image;
        this.tags = tags;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateAndTimeOfCreation() {
        return dateAndTimeOfCreation;
    }

    public void setDateAndTimeOfCreation(Date dateAndTimeOfCreation) {
        this.dateAndTimeOfCreation = dateAndTimeOfCreation;
    }

    public Integer getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
