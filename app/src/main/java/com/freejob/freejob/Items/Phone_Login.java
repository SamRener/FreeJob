package com.freejob.freejob.Items;
public class  Phone_Login {

    private String number_phone;
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private boolean mVerificationInProgress = false;

    public static String getKeyVerifyInProgress() {
        return KEY_VERIFY_IN_PROGRESS;
    }

    public boolean getmVerificationInProgress() {
        return mVerificationInProgress;
    }

    public void setmVerificationInProgress(boolean mVerificationInProgress) {
        this.mVerificationInProgress = mVerificationInProgress;
    }

    public String getNumber_phone() {
        return number_phone;
    }

    public void setNumber_phone(String number_phone) {
        this.number_phone = number_phone;
    }
}
