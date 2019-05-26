package com.freejob.freejob.Globals;

import android.content.Context;
import android.preference.PreferenceManager;

import com.freejob.freejob.Items.Request;
import com.freejob.freejob.Items.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferences {

    static android.content.SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    //Number Phone Email
    public static void setNumberPhone(Context ctx, String number_phone){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString("NUMBER_PHONE_EMAIL", number_phone);
        editor.apply();
    }
    public static String getNumberPhone(Context ctx){
        return getSharedPreferences(ctx).getString("NUMBER_PHONE_EMAIL", "");
    }

    public static void setEmail(Context ctx, String email){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString("EMAIL", email);
        editor.apply();
    }
    public static String getEmail(Context ctx){
        return getSharedPreferences(ctx).getString("EMAIL", "");
    }

    public static void setEmailPass(Context ctx, String pass){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString("PASS", pass);
        editor.apply();
    }
    public static String getEmailPass(Context ctx){
        return getSharedPreferences(ctx).getString("PASS", "");
    }

    //User class
    public static void setUser(Context ctx, User user){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("USER", json);
        editor.apply();
    }
    public static User getUser(Context ctx){
        Gson gson = new Gson();
        String json = getSharedPreferences(ctx).getString("USER", "");
        return gson.fromJson(json, User.class);
    }

    //User class
    public static void setRequests(Context ctx, List<Request> requests){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        Gson gson = new Gson();
        int cont = 0;
        while (requests.size() > cont) {
            if(cont == 0) cont++;
            String json = gson.toJson(requests.get(cont - 1));
            //editor.putString("REQUEST"+String.valueOf(cont), json);
           // editor.apply();
            cont++;
        }
        //editor.putInt("REQUESTS_SIZE", requests.size());
        //editor.apply();
    }
    public static List<Request> getRequests(Context ctx){
        Gson gson = new Gson();
        int cont = 0;
        List<Request> requests = new ArrayList<>();

       /* while (getSharedPreferences(ctx).getInt("REQUESTS_SIZE", 0) >= cont) {
            String json = getSharedPreferences(ctx).getString("REQUEST"+String.valueOf(cont), "");
            //requests.add(gson.fromJson(json, Request.class));
            cont++;
        } */
        return requests;
    }

    //LatLng
    public static void setLatLng(Context ctx, LatLng latLng){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        Gson gson = new Gson();
        String json = gson.toJson(latLng);
        editor.putString("LATLNG", json);
        editor.apply();
    }
    public static LatLng getLatLng(Context ctx){
        Gson gson = new Gson();
        String json = getSharedPreferences(ctx).getString("LATLNG", "");
        return gson.fromJson(json, LatLng.class);
    }

    //Offline Mode
    public static void setOffline(Context ctx, boolean offline){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean("OFFLINE", offline);
        editor.apply();
    }
    public static boolean isOffline(Context ctx){
        return getSharedPreferences(ctx).getBoolean("OFFLINE", false);
    }
}

