package com.freejob.freejob.Classes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.freejob.freejob.Globals.SharedPreferences;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CommonMethods {


    public DatabaseReference getRef(Context ctx){
        FirebaseApp.initializeApp(ctx);
        return FirebaseDatabase.getInstance().getReference();
    }
    public FirebaseAuth getAuth(Context ctx){
        FirebaseApp.initializeApp(ctx);
        return FirebaseAuth.getInstance();
    }
    public StorageReference getStorage(Context ctx){
        FirebaseApp.initializeApp(ctx);
        return FirebaseStorage.getInstance().getReference();
    }
    public void LogOut(Context ctx){
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.setUser(ctx,null);
        SharedPreferences.setLatLng(ctx, null);
        SharedPreferences.setNumberPhone(ctx, null);
        SharedPreferences.setEmailPass(ctx, null);
        SharedPreferences.setEmail(ctx, null);

    }


    public LatLng getLocation(final Activity ctx) {
        final LatLng[] address = {null};
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ctx, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 7000);
        }
        else{
            LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            boolean gps = false, net = false;
            try {
                gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setMessage("Para continuar, ative os serviços de localização do seu dispositivo");
                dialog.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ctx.startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Não ativar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            }
            if (!net) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setMessage("Para continuar, ative os serviços de internet do seu dispositivo");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
            }
            if (net && gps) {
                final ProgressDialog dialog = new ProgressDialog(ctx);
                dialog.setMessage("Aguarde enquanto tentamos localizar a sua posição...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                dialog.setCancelable(false);
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        address[0] = new LatLng(location.getLatitude(), location.getLongitude());
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

            }
        }
        return address[0];
    }
    private String getAddressFromLocation(Location location, ProgressDialog dialog, Activity ctx){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(ctx, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
              return addresses.get(0).getAddressLine(0);
            }
            dialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int dpToPx(int dp, Activity c) {
        float density = c.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }


}
