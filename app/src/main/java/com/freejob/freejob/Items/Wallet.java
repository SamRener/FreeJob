package com.freejob.freejob.Items;

import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private List<Payment> received_jobs = new ArrayList<>();
    private List<Payment> to_receive_jobs = new ArrayList<>();

    public Wallet() {
    }

    public Wallet(List<Payment> received_jobs, List<Payment> to_receive_jobs) {
        this.received_jobs = received_jobs;
        this.to_receive_jobs = to_receive_jobs;
    }

    public List<Payment> getReceived_jobs() {
        return received_jobs;
    }

    public List<Payment> getTo_receive_jobs() {
        return to_receive_jobs;
    }
}
