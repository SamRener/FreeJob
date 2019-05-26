package com.freejob.freejob.Fragments.SignInFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Globals.SharedPreferences;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FullSignInFragment extends Fragment {

    private OnFSIFragmentInteractionListener mListener;
    private View view;
    private EditText cpf, telefone;
    Button cadastrar;
    FirebaseAuth auth;
    DatabaseReference reference;
    public FullSignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_full_sign_in, container, false);
        initFirebase();
        initVariables();
        return view;
    }

    private void initFirebase() {
        reference = new CommonMethods().getRef(getActivity());
        auth = new CommonMethods().getAuth(getActivity());
    }

    private void initVariables() {
        cpf = view.findViewById(R.id.FFSI_cpf);
        telefone = view.findViewById(R.id.FFSI_com);
            telefone.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);


        cadastrar = view.findViewById(R.id.FFSI_cadastrar);
       cadastrar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               SignUpInformation();
           }
       });
    }

    private void SignUpInformation() {
            if(!cpf.getText().toString().equals("")){
                if(cpf.getText().toString().length() == 11) {
                    if (!telefone.getText().toString().equals("")) {
                        final ProgressDialog dialog = new ProgressDialog(getContext());
                        dialog.setMessage("Inserindo novos dados...");
                        dialog.setTitle("Inserindo...");
                        String CPF = cpf.getText().toString();
                        CPF = CPF.replace(".","");
                       CPF = CPF.replace("-","");
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.show();
                        final String finalCPF = CPF;
                        reference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                user.setEmail(telefone.getText().toString());
                                user.setCPF(finalCPF);
                                SharedPreferences.setUser(getActivity(), user);
                                reference.child("Users").child(auth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            mListener.FSIFragmentInteraction();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else telefone.setError("Insira um valor válido");
                }cpf.setError("CPF Inválido");
            }else cpf.setError("Insira um valor válido");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFSIFragmentInteractionListener) {
            mListener = (OnFSIFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFSIFragmentInteractionListener {
        void FSIFragmentInteraction();
    }
}
