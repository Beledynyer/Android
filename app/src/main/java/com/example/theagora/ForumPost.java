package com.example.theagora;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class ForumPost implements Parcelable {
    private int postId;
    private Integer userId;
    private String content;
    private Date dateAndTimeOfCreation;
    private Integer numberOfLikes;
    private Boolean isApproved;
    private byte[] image;
    private String tags;
    private User user;
    private String title;

    protected ForumPost(Parcel in) {
        postId = in.readInt();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        content = in.readString();
        if (in.readByte() == 0) {
            numberOfLikes = null;
        } else {
            numberOfLikes = in.readInt();
        }
        byte tmpIsApproved = in.readByte();
        isApproved = tmpIsApproved == 0 ? null : tmpIsApproved == 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            image = in.readBlob();
        }
        tags = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        title = in.readString();
    }

    public static final Creator<ForumPost> CREATOR = new Creator<ForumPost>() {
        @Override
        public ForumPost createFromParcel(Parcel in) {
            return new ForumPost(in);
        }

        @Override
        public ForumPost[] newArray(int size) {
            return new ForumPost[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ForumPost(int postId, Integer userId, String content, Date dateAndTimeOfCreation, Integer numberOfLikes, Boolean isApproved, byte[] image, String tags, User user) {
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


    public ForumPost(Integer userId, String content, Integer numberOfLikes, byte[] image, String tags, String title) {
        this.isApproved = true;
        this.userId = userId;
        this.content = content;
        this.dateAndTimeOfCreation = new Date();
        this.numberOfLikes = numberOfLikes;
        this.image = image;
        this.tags = tags;
        this.title = title;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

        dest.writeInt(postId);
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(content);
        if (numberOfLikes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numberOfLikes);
        }
        dest.writeByte((byte) (isApproved == null ? 0 : isApproved ? 1 : 2));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            dest.writeBlob(image);
        }
        dest.writeString(tags);
        dest.writeParcelable(user, flags);
        dest.writeString(title);
    }


}
