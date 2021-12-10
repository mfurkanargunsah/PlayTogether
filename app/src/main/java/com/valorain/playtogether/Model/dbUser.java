package com.valorain.playtogether.Model;

public class dbUser {

    private String userName, userEmail,userID, gender,Status, profilePics;
    private int userCoin, userSentCoin, userReceivedCoin;
    boolean premium,useronline;



    public dbUser(String userName, String userEmail, String userID, String gender, String Status, boolean premium, boolean useronline, String profilePics, int userCoin, int userSentCoin, int userReceivedCoin) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userID = userID;
        this.gender = gender;
        this.Status = Status;
        this.premium = premium;
        this.useronline = useronline;
        this.profilePics = profilePics;
        this.userCoin = userCoin;
        this.userSentCoin = userSentCoin;
        this.userReceivedCoin = userReceivedCoin;


    }

    public dbUser() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getProfilePics() {
        return profilePics;
    }

    public void setProfilePics(String profilePics) {
        this.profilePics = profilePics;
    }

    public int getUserCoin() {
        return userCoin;
    }

    public void setUserCoin(int userCoin) {
        this.userCoin = userCoin;
    }

    public int getUserSentCoin() {
        return userSentCoin;
    }

    public void setUserSentCoin(int userSentCoin) {
        this.userSentCoin = userSentCoin;
    }

    public int getUserReceivedCoin() {
        return userReceivedCoin;
    }

    public void setUserReceivedCoin(int userReceivedCoin) {
        this.userReceivedCoin = userReceivedCoin;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isUseronline() {
        return useronline;
    }

    public void setUseronline(boolean useronline) {
        this.useronline = useronline;
    }
}
