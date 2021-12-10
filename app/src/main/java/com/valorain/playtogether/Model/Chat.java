package com.valorain.playtogether.Model;

public class Chat {

    private String userMessage, sender, receiver, messageType,docId,userName,imgProfil;


    public Chat(String userMessage, String sender, String receiver, String messageType, String docId, String imgProfil) {
        this.userMessage = userMessage;
        this.sender = sender;
        this.receiver = receiver;
        this.messageType = messageType;
        this.docId = docId;
      //  this.userName = userName;
        this.imgProfil = imgProfil;
    }

    public Chat() {
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getImgProfil() {
        return imgProfil;
    }

    public void setImgProfil(String imgProfil) {
        this.imgProfil = imgProfil;
    }
}
