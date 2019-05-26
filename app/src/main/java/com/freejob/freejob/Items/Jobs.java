package com.freejob.freejob.Items;

/**
 * Created by Samuel on 16/04/2018.
 */

public class Jobs {

    public String job;
    public int icon;


    public Jobs(String job, int icon) {
        this.job = job;
        this.icon = icon;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
