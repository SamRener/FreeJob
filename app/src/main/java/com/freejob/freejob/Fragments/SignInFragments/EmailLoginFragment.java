package com.freejob.freejob.Fragments.SignInFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.freejob.freejob.Activities.ClientActivity;
import com.freejob.freejob.Activities.SignInActivity;
import com.freejob.freejob.Activities.WorkerActivity;
import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Globals.SharedPreferences;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;


public class EmailLoginFragment extends Fragment {

    FirebaseAuth auth = new CommonMethods().getAuth(getActivity());
    DatabaseReference reference = new CommonMethods().getRef(getActivity());
    View view;
    public EmailLoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_email_sign_in, container, false);
        initVariables();
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initVariables() {
        final EditText email = view.findViewById(R.id.FE_email);
        final EditText pass = view.findViewById(R.id.FE_pass);
        Button verificar = view.findViewById(R.id.FE_verificar);
        verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().isEmpty()){
                    if(!pass.getText().toString().isEmpty()){
                        LogIn(email.getText().toString(), pass.getText().toString());
                    }
                    else pass.setError("Insira um valor válido");
                }else email.setError("Insira um valor válido");
            }
        });
    }

    private void LogIn(final String email, final String pass) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Fazendo Login...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Aguarde um momento");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SharedPreferences.setOffline(getActivity(), false);
                    SharedPreferences.setEmail(getActivity(), email);
                    SharedPreferences.setEmailPass(getActivity(), pass);
                    if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {

                        final DatabaseReference user = reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("type");
                        reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                SharedPreferences.setUser(getActivity(), dataSnapshot.getValue(User.class));
                                user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //SharedPreferences.setCustomToken();
                                        switch (dataSnapshot.getValue(String.class) ) {
                                            case "C":
                                                if(!SharedPreferences.getUser(getActivity()).getCPF().equals("")){
                                                    if(!SharedPreferences.getUser(getActivity()).getMainAddress().getCEP().equals("0")) {
                                                        getActivity().startActivity(new Intent(getActivity(), ClientActivity.class));
                                                        getActivity().finish();
                                                    }else{
                                                        SharedPreferences.setEmail(getActivity(), email);
                                                        SharedPreferences.setEmailPass(getActivity(), pass);
                                                        startActivity(new Intent(getActivity(), SignInActivity.class).putExtra("Address", false).putExtra("gname", UUID.randomUUID().toString()));
                                                        getActivity().finish();
                                                    }
                                                }
                                                else{
                                                    SharedPreferences.setEmail(getActivity(), email);
                                                    SharedPreferences.setEmailPass(getActivity(), pass);
                                                    startActivity(new Intent(getActivity(), SignInActivity.class).putExtra("CPF", false).putExtra("gname", UUID.randomUUID().toString()));
                                                    getActivity().finish();
                                                }
                                                break;
                                            case "T":
                                                if(!SharedPreferences.getUser(getActivity()).getCPF().equals("")){
                                                    if(!SharedPreferences.getUser(getActivity()).getMainAddress().getCEP().equals("0")) {
                                                        getActivity().startActivity(new Intent(getActivity(), WorkerActivity.class));
                                                        getActivity().finish();
                                                    }else{
                                                        SharedPreferences.setEmail(getActivity(), email);
                                                        SharedPreferences.setEmailPass(getActivity(), pass);
                                                        startActivity(new Intent(getActivity(), SignInActivity.class).putExtra("Address", false).putExtra("gname", UUID.randomUUID().toString()));
                                                        getActivity().finish();
                                                    }
                                                }
                                                else{
                                                    SharedPreferences.setEmail(getActivity(), email);
                                                    SharedPreferences.setEmailPass(getActivity(), pass);
                                                    startActivity(new Intent(getActivity(), SignInActivity.class).putExtra("CPF", false).putExtra("gname", UUID.randomUUID().toString()));
                                                    getActivity().finish();
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
                    }
                    else {

                        SharedPreferences.setEmail(getActivity(), email);
                        SharedPreferences.setEmailPass(getActivity(), pass);
                        startActivity(new Intent(getActivity(), SignInActivity.class));


                    }
                }
                else SignIn(email, pass, dialog);
            }
        });
    }

    private void SignIn(final String email, final String pass, final ProgressDialog dialog){

        dialog.setMessage("Fazendo cadastro...");
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                SharedPreferences.setOffline(getActivity(), false);
                    SharedPreferences.setEmail(getActivity(), email);
                    SharedPreferences.setEmailPass(getActivity(), pass);
                    dialog.dismiss();
                   getActivity().startActivity(new Intent(getActivity(), SignInActivity.class).putExtra("gname", UUID.randomUUID().toString()));

                }

        });
    }
}
