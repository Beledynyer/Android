package com.example.theagora;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ForumPost implements Parcelable {
    private int postId;
    private Integer userId;
    private String content;
    private String dateAndTimeOfCreation;
    private Integer numberOfLikes;
    private Boolean isApproved;
    private String image;
    private String tags;
    private User user;
    private String title;

    // Constructor
    public ForumPost(int postId, Integer userId, String content, Integer numberOfLikes, Boolean isApproved, String image, String tags, String title) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;

        // Format the current date to the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        this.dateAndTimeOfCreation = dateFormat.format(new Date());

        this.numberOfLikes = numberOfLikes;
        this.isApproved = isApproved;
        if(image == null){
            this.image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAwAB/TrTKEwAAAAASUVORK5CYII=";
        }
        else{
            this.image = image;// Convert byte array to base64 string
        }
        //this.image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAwAB/TrTKEwAAAAASUVORK5CYII=";
        Log.d("IMAGEIMAGEIMAGE", "ForumPost: " + image);
        this.tags = tags;
        this.title = title;
    }

    // Additional Constructor
    public ForumPost(User user, Integer userId, String content, Integer numberOfLikes, byte[] image, String tags, String title) {
        this.user = user;
        this.isApproved = true;
        this.userId = userId;
        this.content = content;

        // Format the current date to the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        this.dateAndTimeOfCreation = dateFormat.format(new Date());

        this.numberOfLikes = numberOfLikes;
        this.image = Base64.encodeToString(image, Base64.NO_WRAP); // Convert byte array to base64 string
        this.tags = tags;
        this.title = title;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDateAndTimeOfCreation() {
        return dateAndTimeOfCreation;
    }

    public void setDateAndTimeOfCreation(String dateAndTimeOfCreation) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Parcelable implementation
    protected ForumPost(Parcel in) {
        postId = in.readInt();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        content = in.readString();
        dateAndTimeOfCreation = in.readString();
        if (in.readByte() == 0) {
            numberOfLikes = null;
        } else {
            numberOfLikes = in.readInt();
        }
        byte tmpIsApproved = in.readByte();
        isApproved = tmpIsApproved == 0 ? null : tmpIsApproved == 1;
        image = in.readString();
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
        dest.writeString(dateAndTimeOfCreation);
        if (numberOfLikes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numberOfLikes);
        }
        dest.writeByte((byte) (isApproved == null ? 0 : isApproved ? 1 : 2));
        dest.writeString(image);
        dest.writeString(tags);
        dest.writeParcelable(user, flags);
        dest.writeString(title);
    }
}
