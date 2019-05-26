package com.freejob.freejob.Items;

import java.util.ArrayList;
import java.util.List;

public class Request {
    private String type, descricao;
    private Address address;
    private String data;
    private long date;
    private int Rating;
    private Double value;
    private User client;
    private User worker;
    private boolean isAccepted, isFinished, payed;
    private String uid;

    public Request(String type, String descricao, Address address, String data, long date, User client,  boolean isAccepted, boolean isFinished, boolean payed, String uid, int finishMonth, int finishDay, int finishYear) {
        this.type = type;
        this.descricao = descricao;
        this.address = address;
        this.data = data;
        this.date = date;
        this.client = client;
        this.isAccepted = isAccepted;
        this.isFinished = isFinished;
        this.payed = payed;
        this.uid = uid;
        this.finishMonth = finishMonth;
        this.finishDay = finishDay;
        this.finishYear = finishYear;
    }

    private int finishMonth;
    private int finishDay;

    public int getFinishMonth() {
        return finishMonth;
    }

    public void setFinishMonth(int finishMonth) {
        this.finishMonth = finishMonth;
    }

    public int getFinishDay() {
        return finishDay;
    }

    public void setFinishDay(int finishDay) {
        this.finishDay = finishDay;
    }

    public int getFinishYear() {
        return finishYear;
    }

    public void setFinishYear(int finishYear) {
        this.finishYear = finishYear;
    }

    private int finishYear;

    private List<PossibleWorker> possibleWorkers = new ArrayList<>();

    public Request() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<PossibleWorker> getPossibleWorkers() {
        return possibleWorkers;
    }

    public User getWorker() { return worker; }

    public void setWorker(User worker) { this.worker = worker; }

    public void setPossibleWorkers(List<PossibleWorker> possibleWorkers) { this.possibleWorkers = possibleWorkers; }


    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
