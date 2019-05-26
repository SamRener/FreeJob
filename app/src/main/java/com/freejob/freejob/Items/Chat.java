package com.freejob.freejob.Items;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    User worker, client;
    List<Message> messages = new ArrayList<>();
    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Chat(User worker, User client, List<Message> messages, String uid) {
        this.worker = worker;
        this.client = client;
        this.messages = messages;
        this.uid = uid;

    }

    public Chat() {
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
