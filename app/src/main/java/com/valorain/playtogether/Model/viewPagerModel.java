package com.valorain.playtogether.Model;

public class viewPagerModel {
    String userName,userAge,userGender,userDescription;
    int userBackground,userProfile;

    public viewPagerModel(String userName, String userAge, String userGender, String userDescription, int userBackground, int userProfile) {
        this.userName = userName;
        this.userAge = userAge;
        this.userGender = userGender;
        this.userDescription = userDescription;
        this.userBackground = userBackground;
        this.userProfile = userProfile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public int getUserBackground() {
        return userBackground;
    }

    public void setUserBackground(int userBackground) {
        this.userBackground = userBackground;
    }

    public int getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(int userProfile) {
        this.userProfile = userProfile;
    }
}
