package com.freejob.freejob.BLLs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.freejob.freejob.Activities.ClientActivity;
import com.freejob.freejob.Activities.SignInActivity;
import com.freejob.freejob.Activities.WorkerActivity;
import com.freejob.freejob.Fragments.SignInFragments.PhoneLoginFragment;
import com.freejob.freejob.Globals.SharedPreferences;
import com.freejob.freejob.Items.Phone_Login;
import com.freejob.freejob.Items.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Phone_Login_Methods{

    //Phone Authenticators
    private static final String TAG = "PhoneAuthActivity";

    private FirebaseAuth mAuth;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    static final String APP_ID = "71307";
    static final String AUTH_KEY = "dB2wWzD3T8D67Jh";
    static final String AUTH_SECRET = "bWzsCGDUaLcXAtC";
    static final String ACCOUNT_KEY = "abX4pMMVWAUWMyrhyJ8s";
    private PhoneLoginFragment phone_login_fragment;
    private Context ctx;
    private Activity act;
    //Firebase Database
    private static FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    public Phone_Login phone_login = new Phone_Login();

    public Phone_Login_Methods(PhoneLoginFragment phone_login_fragment, Context ctx, Activity act) {
        this.phone_login_fragment = phone_login_fragment;
        this.ctx = ctx;
        this.act = act;
    }

    public void Init_Firebase() {
        FirebaseApp.initializeApp(ctx);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                phone_login.setmVerificationInProgress(false);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w(TAG, "onVerificationFailed", e);
                phone_login.setmVerificationInProgress(false);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    phone_login_fragment.progress.dismiss();
                    phone_login_fragment.phone.setError("Número Inválido");

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    phone_login_fragment.progress.dismiss();

                    Snackbar.make(act.findViewById(android.R.id.content), "Servidor lotado, aguarde um momento e tente novamente",
                            Snackbar.LENGTH_LONG).show();

                } else if (e instanceof FirebaseNetworkException){
                    phone_login_fragment.progress.dismiss();
                    Snackbar.make(act.findViewById(android.R.id.content), "Não foi possível acessar o servidor. Verifique a sua conexão",
                            Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show();
                }

            }

            @Override
            public void onCodeSent(final String verificationId, final PhoneAuthProvider.ForceResendingToken token) {
                Log.d("verification", "onCodeSent:" + verificationId);
                phone_login_fragment.OnCodeSent();
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };
    }

    public void verifyPhoneNumberWithCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    public void resendVerificationCode(Phone_Login phone_login) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_login.getNumber_phone(),
                60,
                TimeUnit.SECONDS,
                act,
                mCallbacks,
                mResendToken);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = task.getResult().getUser();
                            final String phone = mAuth.getCurrentUser().getPhoneNumber();
                            mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mAuth.createUserWithEmailAndPassword(phone+"@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                                SharedPreferences.setNumberPhone(ctx, phone_login.getNumber_phone());
                                                if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {

                                                    SharedPreferences.setNumberPhone(ctx, phone_login.getNumber_phone());

                                                    final DatabaseReference user = reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("type");

                                                    reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            SharedPreferences.setUser(ctx, dataSnapshot.getValue(User.class));
                                                            user.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    //SharedPreferences.setCustomToken();
                                                                    switch (dataSnapshot.getValue(String.class) ) {
                                                                        case "C":
                                                                            if(!SharedPreferences.getUser(ctx).getCPF().equals("")){
                                                                                if(!SharedPreferences.getUser(ctx).getMainAddress().getCEP().equals("0")) {
                                                                                    act.startActivity(new Intent(ctx, ClientActivity.class));
                                                                                    act.finish();
                                                                                }else{
                                                                                    act.startActivity(new Intent(ctx, SignInActivity.class).putExtra("Address", false));
                                                                                    act.finish();
                                                                                }
                                                                            }
                                                                            else{
                                                                                act.startActivity(new Intent(ctx, SignInActivity.class).putExtra("CPF", false));
                                                                                act.finish();
                                                                            }
                                                                            break;
                                                                        case "T":
                                                                            if(!SharedPreferences.getUser(ctx).getCPF().equals("")){
                                                                                if(!SharedPreferences.getUser(ctx).getMainAddress().getCEP().equals("0")) {
                                                                                    act.startActivity(new Intent(ctx, WorkerActivity.class));
                                                                                    act.finish();
                                                                                }else{
                                                                                    act.startActivity(new Intent(ctx, SignInActivity.class).putExtra("Address", false));
                                                                                    act.finish();
                                                                                }
                                                                            }
                                                                            else{
                                                                                act.startActivity(new Intent(ctx, SignInActivity.class).putExtra("CPF", false));
                                                                                act.finish();
                                                                            }
                                                                            break;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });


                                                } else {


                                                    SharedPreferences.setNumberPhone(ctx, phone_login.getNumber_phone());

                                                    phone_login_fragment.startActivity(new Intent(ctx, SignInActivity.class));

                                                }

                                            }
                                            else Log.w(TAG, "EmailFailure", task.getException());
                                        }

                                    });
                                }
                            });

                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Toast.makeText(act, "Código Inválido", Toast.LENGTH_SHORT).show();

                            }
                            Toast.makeText(act, "Não foi possível conectar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void startPhoneNumberVerification(Phone_Login phone_login) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_login.getNumber_phone(),
                60,
                TimeUnit.SECONDS,
                act,
                mCallbacks);

        phone_login.setmVerificationInProgress(true);
    }

    public void signInWithAlreadyCreatedPhone(final Phone_Login phone_login, final ProgressDialog tdialog){
        mAuth.signInWithEmailAndPassword(phone_login.getNumber_phone()+"@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SharedPreferences.setOffline(act, false);
                    user = task.getResult().getUser();
                            SharedPreferences.setNumberPhone(act, phone_login.getNumber_phone());
                            SharedPreferences.setNumberPhone(act, phone_login.getNumber_phone());
                            if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {

                                SharedPreferences.setNumberPhone(act,phone_login.getNumber_phone());


                                reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        SharedPreferences.setUser(act, dataSnapshot.getValue(User.class));
                                        User user = dataSnapshot.getValue(User.class);
                                        switch (user.getType()) {
                                            case "C":
                                                if (!SharedPreferences.getUser(act).getCPF().equals("")) {
                                                        act.startActivity(new Intent(act, ClientActivity.class));
                                                        act.finish();

                                                } else {
                                                    act.startActivity(new Intent(ctx, SignInActivity.class).putExtra("CPF", false));
                                                    act.finish();
                                                }
                                                break;
                                            case "T":
                                                if (!SharedPreferences.getUser(act).getCPF().equals("")) {
                                                        act.startActivity(new Intent(act, WorkerActivity.class));
                                                        act.finish();

                                                } else {
                                                    act.startActivity(new Intent(ctx, SignInActivity.class).putExtra("CPF", false));
                                                    act.finish();
                                                }
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {

                                SharedPreferences.setNumberPhone(act, phone_login.getNumber_phone());

                                phone_login_fragment.startActivity(new Intent(ctx, SignInActivity.class));

                            }

                } else {
                    tdialog.dismiss();
                    final AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                    alert.setMessage("Não encontramos o seu cadastro no sistema. Gostaria de fazer um novo utilizando o número "+phone_login.getNumber_phone()+"?");
                    alert.setPositiveButton("Claro, faça meu cadastro!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tdialog.show();
                            startPhoneNumberVerification(phone_login);
                        }
                    });
                    alert.setNegativeButton("Espere, vou conferir o número!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.setTitle("Você se Cadastrou?");
                    alert.setCancelable(false);
                    alert.create().show();

                }

            }
        });
    }
}
