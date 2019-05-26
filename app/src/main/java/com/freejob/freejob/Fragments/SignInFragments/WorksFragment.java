package com.freejob.freejob.Fragments.SignInFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.freejob.freejob.Activities.WorkerActivity;
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
import com.google.firebase.database.ValueEventListener;


public class WorksFragment extends Fragment {



    private OnWFInteractionListener listener;
    private View view;
    private MaterialCardView costura, diarista, frete, motorista, pintor, jardineiro, encanador, pedreiro;

    private User user;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private String job;

    public WorksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_works, container, false);
        initFirebase();
        return view;
    }

    private void initFirebase() {
        auth = new CommonMethods().getAuth(getContext());
        reference = new CommonMethods().getRef(getContext());
        reference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                SharedPreferences.setUser(getContext(), user);
                initVariables();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initVariables() {
        costura = view.findViewById(R.id.FWS_costura);
        diarista = view.findViewById(R.id.FWS_empregada);
        frete = view.findViewById(R.id.FWS_frete);
        motorista = view.findViewById(R.id.FWS_motorista);
        pintor = view.findViewById(R.id.FWS_pintor);
        jardineiro = view.findViewById(R.id.FWS_jardineiro);
        encanador = view.findViewById(R.id.FWS_encanador);
        pedreiro = view.findViewById(R.id.FWS_pedreiro);


        costura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job = "Costureiro";
                costura.setStrokeColor(Color.parseColor("#21443F"));
                diarista.setStrokeColor(Color.parseColor("#C5EDEF"));
                frete.setStrokeColor(Color.parseColor("#C5EDEF"));
                motorista.setStrokeColor(Color.parseColor("#C5EDEF"));
                pintor.setStrokeColor(Color.parseColor("#C5EDEF"));
                encanador.setStrokeColor(Color.parseColor("#C5EDEF"));
                jardineiro.setStrokeColor(Color.parseColor("#C5EDEF"));
                pedreiro.setStrokeColor(Color.parseColor("#C5EDEF"));
            }
        });

        diarista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job = "Diarista";
                costura.setStrokeColor(Color.parseColor("#C5EDEF"));
                diarista.setStrokeColor(Color.parseColor("#21443F"));
                frete.setStrokeColor(Color.parseColor("#C5EDEF"));
                motorista.setStrokeColor(Color.parseColor("#C5EDEF"));
                pintor.setStrokeColor(Color.parseColor("#C5EDEF"));
                jardineiro.setStrokeColor(Color.parseColor("#C5EDEF"));
                encanador.setStrokeColor(Color.parseColor("#C5EDEF"));
                pedreiro.setStrokeColor(Color.parseColor("#C5EDEF"));
            }
        });


        frete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job = "Freteiro";
                costura.setStrokeColor(Color.parseColor("#C5EDEF"));
                diarista.setStrokeColor(Color.parseColor("#C5EDEF"));
                frete.setStrokeColor(Color.parseColor("#21443F"));
                motorista.setStrokeColor(Color.parseColor("#C5EDEF"));
                pintor.setStrokeColor(Color.parseColor("#C5EDEF"));
                jardineiro.setStrokeColor(Color.parseColor("#C5EDEF"));
                encanador.setStrokeColor(Color.parseColor("#C5EDEF"));
                pedreiro.setStrokeColor(Color.parseColor("#C5EDEF"));
            }
        });

        motorista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job = "Motorista";
                costura.setStrokeColor(Color.parseColor("#C5EDEF"));
                diarista.setStrokeColor(Color.parseColor("#C5EDEF"));
                frete.setStrokeColor(Color.parseColor("#C5EDEF"));
                motorista.setStrokeColor(Color.parseColor("#21443F"));
                pintor.setStrokeColor(Color.parseColor("#C5EDEF"));
                jardineiro.setStrokeColor(Color.parseColor("#C5EDEF"));
                encanador.setStrokeColor(Color.parseColor("#C5EDEF"));
                pedreiro.setStrokeColor(Color.parseColor("#C5EDEF"));
            }
        });

        pintor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job = "Pintor";
                costura.setStrokeColor(Color.parseColor("#C5EDEF"));
                diarista.setStrokeColor(Color.parseColor("#C5EDEF"));
                frete.setStrokeColor(Color.parseColor("#C5EDEF"));
                motorista.setStrokeColor(Color.parseColor("#C5EDEF"));
                pintor.setStrokeColor(Color.parseColor("#21443F"));
                encanador.setStrokeColor(Color.parseColor("#C5EDEF"));
                jardineiro.setStrokeColor(Color.parseColor("#C5EDEF"));
                pedreiro.setStrokeColor(Color.parseColor("#C5EDEF"));
            }
        });


        jardineiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job = "Jardineiro";
                costura.setStrokeColor(Color.parseColor("#C5EDEF"));
                diarista.setStrokeColor(Color.parseColor("#C5EDEF"));
                frete.setStrokeColor(Color.parseColor("#C5EDEF"));
                motorista.setStrokeColor(Color.parseColor("#C5EDEF"));
                pintor.setStrokeColor(Color.parseColor("#C5EDEF"));
                encanador.setStrokeColor(Color.parseColor("#C5EDEF"));
                jardineiro.setStrokeColor(Color.parseColor("#21443F"));
                pedreiro.setStrokeColor(Color.parseColor("#C5EDEF"));
            }
        });

        pedreiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job = "Pedreiro";
                costura.setStrokeColor(Color.parseColor("#C5EDEF"));
                diarista.setStrokeColor(Color.parseColor("#C5EDEF"));
                frete.setStrokeColor(Color.parseColor("#C5EDEF"));
                motorista.setStrokeColor(Color.parseColor("#C5EDEF"));
                pintor.setStrokeColor(Color.parseColor("#C5EDEF"));
                encanador.setStrokeColor(Color.parseColor("#C5EDEF"));
                jardineiro.setStrokeColor(Color.parseColor("#C5EDEF"));
                pedreiro.setStrokeColor(Color.parseColor("#21443F"));
            }
        });


        encanador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job = "Encanador";
                costura.setStrokeColor(Color.parseColor("#C5EDEF"));
                diarista.setStrokeColor(Color.parseColor("#C5EDEF"));
                frete.setStrokeColor(Color.parseColor("#C5EDEF"));
                motorista.setStrokeColor(Color.parseColor("#C5EDEF"));
                pintor.setStrokeColor(Color.parseColor("#C5EDEF"));
                encanador.setStrokeColor(Color.parseColor("#21443F"));
                jardineiro.setStrokeColor(Color.parseColor("#C5EDEF"));
                pedreiro.setStrokeColor(Color.parseColor("#C5EDEF"));
            }
        });

        Button continuar = view.findViewById(R.id.FWS_continue);
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog dialog = ProgressDialog.show(getActivity(), "Ajeitando as coisas...", "Cadastrando novos dados...", true, false);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                reference.child("Users").child(auth.getCurrentUser().getUid()).child("work_type").setValue(job).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        listener.WFInteraction();

                    }
                });
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWFInteractionListener) {
            listener = (OnWFInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface OnWFInteractionListener {
        void WFInteraction();
    }
}

