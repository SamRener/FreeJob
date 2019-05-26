package com.freejob.freejob.Items;

import java.util.Date;

public class Message {
    String message;
    long timestamp;
    Boolean isNew;
    User sender;
    int messageid;

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public int getMessageid() {
        return messageid;
    }

    public void setMessageid(int messageid) {
        this.messageid = messageid;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Message(String message, Boolean isNew, User sender, int messageid) {
        this.message = message;

        this.isNew = isNew;
        this.sender = sender;
        this.messageid = messageid;
    }
    public Message(Boolean isNew, int i){
        this.isNew=isNew;
    }

    public Message(){

    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
