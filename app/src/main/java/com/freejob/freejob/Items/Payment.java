package com.freejob.freejob.Items;

public class Payment {
    String job;
    double payment;
    long date;
    String extense_date;
    int day, month, year;
    String receiver;
    String payer;
    double commission;

    public Payment() {
    }

    public Payment(String job, double payment, long date, String extense_date, int day, int month, int year, String receiver, String payer, double commission) {
        this.job = job;
        this.payment = payment;
        this.date = date;
        this.extense_date = extense_date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.receiver = receiver;
        this.payer = payer;
        this.commission = commission;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getExtense_date() {
        return extense_date;
    }

    public void setExtense_date(String extense_date) {
        this.extense_date = extense_date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }
}
