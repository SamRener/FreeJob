package com.freejob.freejob.Fragments.FastJobFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Items.FastJob;
import com.freejob.freejob.Items.PossibleWorker;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.freejob.freejob.Transforms.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import me.grantland.widget.AutofitTextView;

public class WorkerFastJobFragment extends Fragment{

    View view;

    DatabaseReference reference;
    StorageReference storage;
    FirebaseAuth auth;
    User worker, client;
    public FastJob fastJob;

    LinearLayout mainLayout, priceLayout, waitLayout, acceptLayout;
    String fj_uid;
    int fj_type;

    OnWFJFragmentInteractionListener mlistener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fastjob_worker, container, false);

        fj_type = Objects.requireNonNull(getArguments()).getInt("fj_type", 1);

        initFirebase();
        return view;
    }

    private void initFirebase() {
        fastJob = new Gson().fromJson(getArguments().getString("fastJob", ""), FastJob.class);
        auth = new CommonMethods().getAuth(getActivity());
        storage = new CommonMethods().getStorage(getActivity());
        reference = new CommonMethods().getRef(getActivity());

        reference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                worker = dataSnapshot.getValue(User.class);
                reference.child("Users").child(fastJob.getClient()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                client = dataSnapshot.getValue(User.class);
                                createMainView();
                                initVariables();
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

    private void createMainView() {
        AutofitTextView client_name, client_rating, work_type;
        ImageView client_profile;

        client_name = view.findViewById(R.id.WFJF_client_name);
        client_rating = view.findViewById(R.id.WFJF_client_rating);
        work_type = view.findViewById(R.id.WFJF_work_type);
        client_profile = view.findViewById(R.id.WFJF_client_profile);

        mainLayout = view.findViewById(R.id.WFJF_clientLayout);
        waitLayout = view.findViewById(R.id.WFJF_waitLayout);
        priceLayout = view.findViewById(R.id.WFJF_priceLayout);
        acceptLayout = view.findViewById(R.id.WFJF_acceptLayout);

        client_name.setText(client.getName()+" "+client.getMiddlename());
        client_rating.setText(client.getRating()+"☆");
        work_type.setText("Precisa de "+fastJob.getType());

        Picasso.get().load(client.getUri()).transform(new CircleTransform()).into(client_profile);

        ImageView fullSize_job = view.findViewById(R.id.WFJF_fullSize_Job);
        switch (fastJob.getType()) {
            case "Costureiro":  Picasso.get().load("https://certificadocursosonline.com/wp-content/uploads/2018/07/curso-de-corte-e-costura.jpg").into(fullSize_job); break;
            case "Pedreiro":  Picasso.get().load("https://certificadocursosonline.com/wp-content/uploads/2018/07/curso-de-pedreiro.jpg").into(fullSize_job); break;
            case "Freteiro":  Picasso.get().load("http://www.transmaso.com.br/image_crop.php?imagem=20180521_111918_dsc_0139.jpg&largura=850&&altura=350&pasta=uploads/img_noticias").into(fullSize_job); break;
            case "Motorista":  Picasso.get().load("https://bibliotecaprt21.files.wordpress.com/2015/10/article.jpg").into(fullSize_job); break;
            case "Diarista":  Picasso.get().load("https://www.mumsnet.com/system/1/assets/files/000/011/233/11233/f8a6c420c/original/xsmall-bone-kitchen.jpg.pagespeed.ic.sy2XXDmX6u.jpg").into(fullSize_job); break;
            case "Jardineiro":  Picasso.get().load("http://smsmanpower.com/wp-content/uploads/2013/10/riding-slider.jpg").into(fullSize_job); break;
            case "Encanador":  Picasso.get().load("https://nicholsandphipps.com/wp-content/uploads/2018/04/Drain-Maintenance-101.jpg").into(fullSize_job); break;
            case "Pintor":  Picasso.get().load("http://www.mygreatpaintingservices.sitew.de/fs/Root/dhon3-2.jpg").into(fullSize_job); break;
        }
    }

    private void initVariables() {
        if(worker.getWaitingFastJob().equals("nulo")) {
            switch (fastJob.getWorkerStage()) {
                case 1:
                    Description();
                    break;
            }
        }
        else Wait();
    }

    private void Description() {
        //Setting views
        TransitionManager.beginDelayedTransition((ViewGroup)view.findViewById(R.id.WFJF_MainLayout), new ChangeBounds());

        mainLayout.setVisibility(View.VISIBLE);
        waitLayout.setVisibility(View.GONE);
        priceLayout.setVisibility(View.GONE);
        acceptLayout.setVisibility(View.VISIBLE);

        AutofitTextView fastJob_description = view.findViewById(R.id.WFJF_fastjob_description);
        fastJob_description.setVisibility(View.VISIBLE);
        fastJob_description.setText(fastJob.getDescription());

        //Setting buttons
        Button refuse = view.findViewById(R.id.WFJF_refuse);
        Button accept = view.findViewById(R.id.WFJF_accept);
        Button sendPrice = view.findViewById(R.id.WFJF_sendPrice);
        sendPrice.setVisibility(View.GONE);

        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worker.getRefused_fastJobs().add(fastJob.getUid());
                reference.child("Users").child(worker.getUuid()).setValue(worker).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Você recusou o FastJob de "+client.getName()+ " com sucesso", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Price();
            }
        });
    }

    private void Price() {
        //Setting views
        TransitionManager.beginDelayedTransition((ViewGroup)view.findViewById(R.id.WFJF_MainLayout), new ChangeBounds());

        mainLayout.setVisibility(View.VISIBLE);
        waitLayout.setVisibility(View.GONE);
        priceLayout.setVisibility(View.VISIBLE);
        acceptLayout.setVisibility(View.GONE);

        AutofitTextView fastJob_description = view.findViewById(R.id.WFJF_fastjob_description);
        fastJob_description.setVisibility(View.GONE);

        final EditText price = view.findViewById(R.id.WFJF_price);
        Button sendPrice = view.findViewById(R.id.WFJF_sendPrice);
        sendPrice.setVisibility(View.VISIBLE);

        sendPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!price.getText().toString().equals("") && price.getText().toString().length()>2){
                    fastJob.getPossibleWorkers().add(new PossibleWorker(worker, Double.parseDouble(price.getText().toString())));
                    fastJob.setClientStage(3);
                    reference.child("FastJobs").child(fastJob.getUid()).setValue(fastJob).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            worker.getAccepted_fastJobs().add(fastJob.getUid());
                            Wait();
                            worker.setWaitingFastJob(fastJob.getUid());
                            reference.child("Users").child(worker.getUuid()).setValue(worker).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }
                    });
                }else price.setError("Insira um valor válido");
            }
        });
    }

    private void Wait() {

        //Setting views
        TransitionManager.beginDelayedTransition((ViewGroup)view.findViewById(R.id.WFJF_MainLayout), new ChangeBounds());

        RelativeLayout clientLayout = view.findViewById(R.id.WFJF_descriptionLayout);

        mainLayout.setVisibility(View.VISIBLE);
        waitLayout.setVisibility(View.VISIBLE);
        priceLayout.setVisibility(View.GONE);
        acceptLayout.setVisibility(View.GONE);
        clientLayout.setVisibility(View.GONE);
        AutofitTextView fastJob_description = view.findViewById(R.id.WFJF_fastjob_description);
        fastJob_description.setVisibility(View.VISIBLE);
        fastJob_description.setText(fastJob.getDescription());

        //Setting buttons
        Button sendPrice = view.findViewById(R.id.WFJF_sendPrice);
        sendPrice.setVisibility(View.GONE);
        Button cancel = view.findViewById(R.id.WFJF_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelFastJob();
            }
        });

        reference.child("FastJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FastJob fj = dataSnapshot.getValue(FastJob.class);
                if(fj.getUid().equals(fastJob.getUid())){
                       if(fj.getWorker().equals(worker.getUuid())){
                           Client();
                       }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FastJob fj = dataSnapshot.getValue(FastJob.class);
                if(fj.getUid().equals(fastJob.getUid())){
                        if(fj.getWorker().equals(worker.getUuid())){
                            Client();
                        }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                FastJob fj = dataSnapshot.getValue(FastJob.class);
                if(fj.getUid().equals(fastJob.getUid())){
                    mlistener.CanceledFastJob(fj);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CancelFastJob() {
        int cont = 1;
        while (fastJob.getPossibleWorkers().size() >= cont){
            if(fastJob.getPossibleWorkers().get(cont - 1).getWorker().getUuid().equals(worker.getUuid())){
                fastJob.getPossibleWorkers().remove(cont - 1);
            }
            cont++;
        }
        cont = 1;
        while(worker.getAccepted_fastJobs().size()>= cont){
            if(worker.getAccepted_fastJobs().get(cont - 1).equals(fastJob.getUid())){
                worker.getAccepted_fastJobs().remove(cont-1);
            }
            cont++;
        }
        reference.child("FastJobs").child(fastJob.getUid()).setValue(fastJob).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                worker.getAccepted_fastJobs().add(fastJob.getUid());
                reference.child("Users").child(worker.getUuid()).setValue(worker).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Você cancelou o pedido de "+client.getName()+ "com sucesso, mas pode aceitá-lo novamente", Toast.LENGTH_LONG).show();
                        mlistener.CanceledFastJob(fastJob);
                    }
                });
            }
        });

    }

    private void Client() {
       if(mlistener!=null) mlistener.WFJFragmentInteraction(client, fastJob);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnWFJFragmentInteractionListener) {
            mlistener = (OnWFJFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mlistener = null;
    }

    public interface OnWFJFragmentInteractionListener {
        void WFJFragmentInteraction(User client, FastJob fastJob);
        void CanceledFastJob(FastJob fastJob);
    }
}

