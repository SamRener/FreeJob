package com.freejob.freejob.Items;

public class PossibleWorker{
    User worker;
    Double price;

    public PossibleWorker(User worker, Double price) {
        this.worker = worker;
        this.price = price;
    }

    public PossibleWorker() {
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}