package com.freejob.freejob.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.freejob.freejob.Fragments.SignInFragments.EmailLoginFragment;
import com.freejob.freejob.Fragments.SignInFragments.PhoneLoginFragment;
import com.freejob.freejob.Globals.SharedPreferences;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements PhoneLoginFragment.OnPLFInteractionListener{

    FragmentManager fragmentManager;
    FragmentTransaction fragTransaction;
    FrameLayout frameLayout;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        fragmentManager = getSupportFragmentManager();
        fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.replace(R.id.AM_fragment_content, new PhoneLoginFragment());
        fragTransaction.commit();


      /*  MaterialButton btn_phone = findViewById(R.id.AM_phone);
        final MaterialButton btn_email = findViewById(R.id.AM_facebook);
        MaterialButton btn_google = findViewById(R.id.AM_google);

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInwGoogle();
            }
        });

        btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager = getSupportFragmentManager();
                fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.replace(R.id.AM_fragment_content, new PhoneLoginFragment());
                fragTransaction.commit();
                frameLayout = findViewById(R.id.AM_fragment_content);
                showView(frameLayout);

            }
        });

        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    fragmentManager = getSupportFragmentManager();
                    fragTransaction = fragmentManager.beginTransaction();
                    fragTransaction.replace(R.id.AM_fragment_content, new EmailLoginFragment());
                    fragTransaction.commit();
                    frameLayout = findViewById(R.id.AM_fragment_content);
                    showView(frameLayout);
            }
        }); */
}

    private void SignInwGoogle() {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 9001);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == 9001) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("", "Google sign in failed", e);
                    // ...
                }
            }
        }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.setOffline(MainActivity.this, false);
                               if(acct.getDisplayName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {

                                   startActivity(new Intent(MainActivity.this, SignInActivity.class).putExtra("gname",acct.getDisplayName()));
                               }
                               else{
                                   final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                                   dialog.setMessage("Aguarde enquanto fazemos o login");
                                   dialog.setCancelable(false);
                                   dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                   dialog.setCanceledOnTouchOutside(false);

                                   reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           User user = dataSnapshot.getValue(User.class);
                                           if (Objects.requireNonNull(user).getMainAddress().getLogradouro().equals("nulo")) {
                                               startActivity(new Intent(MainActivity.this, SignInActivity.class).putExtra("gname","nulo"));
                                           } else {
                                               dialog.show();
                                               switch (user.getType()) {
                                                   case "C":
                                                       if (!user.getCPF().equals("")) {
                                                           if (!user.getMainAddress().getCEP().equals("0")) {
                                                               startActivity(new Intent(MainActivity.this, ClientActivity.class));
                                                               finish();
                                                           } else {
                                                               startActivity(new Intent(MainActivity.this, SignInActivity.class).putExtra("Address", false));
                                                               finish();
                                                           }
                                                       } else {
                                                           startActivity(new Intent(MainActivity.this, SignInActivity.class).putExtra("CPF", false));
                                                           finish();
                                                       }
                                                       break;
                                                   case "T":
                                                       if (!user.getCPF().equals("")) {
                                                           if (!user.getMainAddress().getCEP().equals("0")) {
                                                               startActivity(new Intent(MainActivity.this, WorkerActivity.class));
                                                               finish();
                                                           } else {
                                                               startActivity(new Intent(MainActivity.this, SignInActivity.class).putExtra("Address", false));
                                                               finish();
                                                           }
                                                       } else {
                                                           startActivity(new Intent(MainActivity.this, SignInActivity.class).putExtra("CPF", false));
                                                           finish();
                                                       }
                                                       break;
                                               }
                                           }
                                       }
                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {

                                       }
                                   });
                               }
                        } else {
                            Toast.makeText(MainActivity.this, "Login com o Google não realizado. Tente mais tarde",
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
          // hideView(frameLayout);

    }

    private void showView(final View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });

        view.startAnimation(animation);
    }

    private void hideView(final View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            }
        });
        view.startAnimation(animation);
    }

    @Override
    public void onPLFInteraction() {

    }
}
