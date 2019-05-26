package com.freejob.freejob.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Fragments.SignInFragments.AddAddressFragment;
import com.freejob.freejob.Fragments.SignInFragments.FullSignInFragment;
import com.freejob.freejob.Fragments.SignInFragments.SignUpFragment;
import com.freejob.freejob.Fragments.SignInFragments.WorksFragment;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity implements SignUpFragment.OnSGUFragmentInteractionListener, FullSignInFragment.OnFSIFragmentInteractionListener, AddAddressFragment.OnFAAFragmentInteractionListener, WorksFragment.OnWFInteractionListener{

    FrameLayout layout;
    DatabaseReference reference;
    FirebaseAuth auth;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        layout = findViewById(R.id.ASI_fragment_content);
        getIntent().getStringExtra("gname");
        auth = new CommonMethods().getAuth(SignInActivity.this);
        if(auth.getCurrentUser().getDisplayName()!=null){
        if(!getIntent().getStringExtra("gname").equals(auth.getCurrentUser().getDisplayName())) {
            initFirebase();
        }  else
            getSupportFragmentManager().beginTransaction().replace(R.id.ASI_fragment_content, new SignUpFragment()).commit();
            showView(layout);}
        else
        getSupportFragmentManager().beginTransaction().replace(R.id.ASI_fragment_content, new SignUpFragment()).commit();
        showView(layout);

    }

    private void initFirebase() {
        reference = new CommonMethods().getRef(SignInActivity.this);

        reference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user.getMainAddress().getLogradouro().equals("nulo")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.ASI_fragment_content, new AddAddressFragment()).commit();
                    showView(layout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showView(final View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_right);
        view.startAnimation(animation);
    }

    @Override
    public void FAAFragmentInteraction() {
        getSupportFragmentManager().beginTransaction().replace(R.id.ASI_fragment_content, new WorksFragment()).commit();
        showView(layout);
    }

    @Override
    public void FAAFragmentInteraction(boolean activity) {
        startActivity(new Intent(SignInActivity.this, ClientActivity.class));
        finish();
    }

    @Override
    public void FSIFragmentInteraction() {
        getSupportFragmentManager().beginTransaction().replace(R.id.ASI_fragment_content, new AddAddressFragment()).commit();
        showView(layout);
    }

    @Override
    public void SGUFragmentInteraction() {
        getSupportFragmentManager().beginTransaction().replace(R.id.ASI_fragment_content, new AddAddressFragment()).commit();
        showView(layout);
    }

    @Override
    public void WFInteraction() {
        startActivity(new Intent(SignInActivity.this, WorkerActivity.class));
        finish();
    }
}
