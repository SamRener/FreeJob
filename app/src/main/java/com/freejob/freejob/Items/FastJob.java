package com.freejob.freejob.Items;

import java.util.ArrayList;
import java.util.List;

public class FastJob {
    private String client, worker, type, description, uid;
    private double price, fjlat, fjlng, clat, clng, wlat, wlng;

    private int workerStage, clientStage;
    private boolean inProgress, cFinished, wFinished, finished, worker_waiting, client_waiting;
    private List<PossibleWorker> possibleWorkers = new ArrayList<>();
    private List<String> refusedWorkers = new ArrayList<>();


    public FastJob(String client, String description, String type, double fjlat, double fjlng, String uid) {
        this.client = client;
        this.type = type;
        this.description = description;
        this.uid = uid;
        this.fjlat = fjlat;
        worker_waiting = false;
        client_waiting = false;
        finished = false;
        wFinished = false;
        cFinished = false;
        inProgress = false;
        this.fjlng = fjlng;
        this.worker = "nulo";
        this.price = 0;
    }

    public FastJob(String worker, double price) {
        this.worker = worker;
        this.price = price;
    }

    public FastJob() {
    }

    public int getWorkerStage() { return workerStage; }

    public void setWorkerStage(int workerStage) { this.workerStage = workerStage; }

    public int getClientStage() { return clientStage; }

    public void setClientStage(int clientStage) { this.clientStage = clientStage; }

    public boolean isInProgress(){ return inProgress; }

    public boolean iscFinished() { return cFinished; }

    public boolean isWorkerWaiting(){ return worker_waiting; }

    public void setWorkerWaiting(boolean waiting){ this.worker_waiting = waiting; }

    public boolean isClientWaiting(){ return client_waiting; }

    public void setClientWaiting(boolean waiting){ this.client_waiting = waiting; }

    public void setcFinished(boolean cFinished) { this.cFinished = cFinished; }

    public boolean iswFinished() { return wFinished; }

    public void setwFinished(boolean wFinished) { this.wFinished = wFinished; }

    public boolean isFinished() { return finished; }

    public void setFinished() { if(cFinished && wFinished)finished = true; }

    public void setInProgress(boolean inProgress){ this.inProgress = inProgress; }

    public String getUid() {
        return uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PossibleWorker> getPossibleWorkers() {
        return possibleWorkers;
    }

    public List<String> getRefusedWorkers() {
        return refusedWorkers;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWlat() { return wlat; }

    public void setWlat(double wlat) { this.wlat = wlat; }

    public double getClng() { return clng; }

    public void setClng(double clng) { this.clng = clng; }

    public double getFjlat() { return fjlat; }

    public void setFjlat(double fjlat) { this.fjlat = fjlat; }

    public double getFjlng() { return fjlng; }

    public void setFjlng(double fjlng) { this.fjlng = fjlng; }

    public double getClat() { return clat; }

    public void setClat(double clat) { this.clat = clat; }

    public double getWlng() { return wlng; }

    public void setWlng(double wlng) { this.wlng = wlng; }
}


