package com.valorain.playtogether.Model;

public class Kullanici {

    private String kullaniciAdi,kullaniciEmail,userID,cins,Status,kullaniciProfil,arananCins;
    boolean premium,useronline;


    public Kullanici(String kullaniciAdi, String kullaniciEmail, String userID,String cins,String Status, boolean premium,boolean useronline,String kullaniciProfil,String arananCins) {
        this.kullaniciAdi = kullaniciAdi;
        this.kullaniciEmail = kullaniciEmail;
        this.userID = userID;
        this.cins = cins;
        this.Status = Status;
        this.premium = premium;
        this.useronline = useronline;
        this.kullaniciProfil = kullaniciProfil;


    }

    public Kullanici() {
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getKullaniciEmail() {
        return kullaniciEmail;
    }

    public void setKullaniciEmail(String kullaniciEmail) {
        this.kullaniciEmail = kullaniciEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCins() {
        return cins;
    }

    public void setCins(String cins) {
        this.cins = cins;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
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

    public String getKullaniciProfil() {
        return kullaniciProfil;
    }

    public void setKullaniciProfil(String kullaniciProfil) {
        this.kullaniciProfil = kullaniciProfil;
    }

    public String getArananCins() {
        return arananCins;
    }

    public void setArananCins(String arananCins) {
        this.arananCins = arananCins;
    }
}
