package com.example.theagora;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    @SerializedName("userId")
    private int id;

    @SerializedName("fname")
    private String fName;

    @SerializedName("lname")
    private String lName;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("isStaffMember")
    private boolean isStaffMember;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    // Constructor
    public User(int id, String fName, String lName, String email, String password, boolean isStaffMember, String phoneNumber) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.password = password;
        this.isStaffMember = isStaffMember;
        this.phoneNumber = phoneNumber;
    }

    // Parcelable implementation
    protected User(Parcel in) {
        id = in.readInt();
        fName = in.readString();
        lName = in.readString();
        email = in.readString();
        password = in.readString();
        isStaffMember = in.readByte() != 0;
        phoneNumber = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStaffMember() {
        return isStaffMember;
    }

    public void setStaffMember(boolean staffMember) {
        isStaffMember = staffMember;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // equals() method
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(fName);
        parcel.writeString(lName);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeByte((byte) (isStaffMember ? 1 : 0));
        parcel.writeString(phoneNumber);
    }
}
