package com.freejob.freejob.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.freejob.freejob.Globals.SharedPreferences;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    public static FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS
            },7000);
        }
        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withBackgroundResource(R.drawable.bg_gradient)
                .withLogo(R.drawable.freejob_icon)
                .withSplashTimeOut(100000);

        View view = config.create();
        setContentView(view);
        initFirebase();
        if(!SharedPreferences.getNumberPhone(getBaseContext()).isEmpty()){
            signInWithAlreadyCreatedPhone();
        }
        else{
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }

    }

    private boolean CheckInternet(){
        final boolean[] isConnected = {false};
        ConnectivityManager cm = (ConnectivityManager)SplashScreen.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            isConnected[0] = true;
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
            builder.setIcon(R.drawable.ic_nonetwork);
            builder.setCancelable(false);
            builder.setTitle(R.string.nocon);
            builder.setMessage(R.string.noconmessage);
            builder.setPositiveButton(R.string.noconpositive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isConnected[0] = CheckInternet();

                }
            });

        }
        return isConnected[0];
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(getBaseContext());
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

    }

    public void signInWithAlreadyCreatedPhone(){
        mAuth.signInWithEmailAndPassword(SharedPreferences.getNumberPhone(getBaseContext())+"@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user = task.getResult().getUser();
                    if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
                    reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                SharedPreferences.setUser(getBaseContext(), dataSnapshot.getValue(User.class));

                                SharedPreferences.setOffline(SplashScreen.this, false);
                                switch (SharedPreferences.getUser(SplashScreen.this).getType()) {
                                    case "C":

                                        startActivity(new Intent(getBaseContext(), ClientActivity.class));
                                        finish();
                                        break;
                                    case "T":
                                        startActivity(new Intent(getBaseContext(), WorkerActivity.class));
                                        finish();
                                        break;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        SharedPreferences.setNumberPhone(getBaseContext(), SharedPreferences.getNumberPhone(getBaseContext()).substring(14, 24).replace("@gmail.com", ""));
                        SharedPreferences.setOffline(SplashScreen.this, false);
                        startActivity(new Intent(SplashScreen.this, SignInActivity.class));
                        finish();

                    }

                }
                else {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }

            }
        });
    }



}
