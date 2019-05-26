package com.freejob.freejob.Items;


import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String uid, phone,type, rating, name, middlename, gender, nasc, CPF = "nulo", email;
    private String lat, lng, work_type;
    private boolean online = true;
    private List<Request> refused_jobs = new ArrayList<>();
    private List<Request> accepted_jobs = new ArrayList<>();
    private List<Request> active_jobs = new ArrayList<>();

    private int level;
    private String monthFastJobs;
    private int current_XP;

    private List<String> finished_jobs = new ArrayList<>();
    private List<String> refused_fastJobs = new ArrayList<>();
    private List<String> accepted_fastJobs = new ArrayList<>();
    private List<String> requested_fastJobs = new ArrayList<>();

    private String active_fastJob, requested_fastJob, waitingFastJob;
    private Wallet wallet = new Wallet();
    private Address MainAddress = new Address();
    private String uri;

    private List<String> fastJobs = new ArrayList<>();

    public User(String uid, String nickname, String phone, String type, String rating, String name, String middlename, String gender, String nasc, String cpf, String email, String lat, String lng, String uri) {
        this.uid = uid;
        this.name = nickname;
        this.phone = phone;
        this.type = type;
        this.rating = rating;
        this.name = name;
        this.middlename = middlename;
        this.gender = gender;
        this.nasc = nasc;
        CPF = cpf;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        MainAddress.setLogradouro("nulo");
        this.uri = uri;
    }
    public User(String uid, String nickname, String type, String rating) {
        this.uid = uid;
        this.name = nickname;
        this.phone = "";
        this.type = type;
        this.rating = rating;
        this.middlename = "";
        this.gender = "";
        this.nasc = "";
        CPF = "";
        this.email = "";
        this.lat = "";
        this.lng = "";
        MainAddress.setLogradouro("nulo");
    }
    public User() {
    }

    public String getRequested_fastJob() {
        return requested_fastJob;
    }

    public void setRequested_fastJob(String requested_fastJob) {
        this.requested_fastJob = requested_fastJob;
    }

    public String getUri() { return uri; }

    public void setUri(String uri) { this.uri = uri; }

    public List<String> getRefused_fastJobs() {
        return refused_fastJobs;
    }

    public List<String> getAccepted_fastJobs() {
        return accepted_fastJobs;
    }

    public List<String> getFinished_jobs(){ return finished_jobs; }

    public List<String> getRequested_fastJobs(){ return requested_fastJobs; }

    public String getActive_fastJob() {
        return active_fastJob;
    }

    public void setActive_fastJob(String active_fastJob) {
        this.active_fastJob = active_fastJob;
    }

    public List<String> getFastJobs() {
        return fastJobs;
    }

    public String getWaitingFastJob() { return waitingFastJob; }

    public void setWaitingFastJob(String waitingFastJob) { this.waitingFastJob = waitingFastJob; }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public List<Request> getAccepted_jobs() {
        return accepted_jobs;
    }

    public void setAccepted_jobs(List<Request> accepted_jobs) { this.accepted_jobs = accepted_jobs; }

    public List<Request> getRefused_jobs() {
        return refused_jobs;
    }

    public void setRefused_jobs(List<Request> refused_jobs) {
        this.refused_jobs = refused_jobs;
    }

    public String getUuid() {
        return uid;
    }

    public void setUuid(String uuid) {
        this.uid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNasc() {
        return nasc;
    }

    public void setNasc(String nasc) {
        this.nasc = nasc;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWork_type() {
        return work_type;
    }

    public void setWork_type(String work_type) {
        this.work_type = work_type;
    }

    public Address getMainAddress() { return MainAddress; }

    public void setMainAddress(Address mainAddress) { MainAddress = mainAddress; }

    public List<Request> getActive_jobs() {
        return active_jobs;
    }

    public void setActive_jobs(List<Request> active_jobs) {
        this.active_jobs = active_jobs;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public int CalculateXP(int job_rating, int job_price){
        current_XP = current_XP + job_rating * (50 / level) * (job_price*10/100);
        if(current_XP >= 100){
            current_XP = current_XP - 100;
            level++;
        }
        return current_XP;
    }

}
