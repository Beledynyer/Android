package com.example.theagora;

import androidx.annotation.Nullable;

public class User {
    private int id;
    private String fName;
    private String lName;
    private String email;
    private String password;
    private boolean isStaffMember;
    private String phoneNumber;

    public User(int id, String fName, String lName, String email, String password, boolean isStaffMember, String phoneNumber) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.password = password;
        this.isStaffMember = isStaffMember;
        this.phoneNumber = phoneNumber;
    }

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

    @Override
    public boolean equals(@Nullable Object obj) {
        User u = (User)obj;
        return u.getId() == id;
    }
}
