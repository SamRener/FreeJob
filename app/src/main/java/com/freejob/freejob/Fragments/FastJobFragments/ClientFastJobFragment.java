package com.freejob.freejob.Fragments.FastJobFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Items.FastJob;
import com.freejob.freejob.Items.PossibleWorker;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.freejob.freejob.Transforms.CircleTransform;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import me.grantland.widget.AutofitTextView;

public class ClientFastJobFragment extends Fragment {

    View view;
    OnCFJFragmentInteractionListener mlistener;
    DatabaseReference reference;
    FirebaseAuth auth;
    User user;
    StorageReference storage;

    String type;
    UUID fj_uuid = UUID.randomUUID();
    FastJob fj;
    int fj_type = 1;
    LatLng location;

    LinearLayout acceptLayout, descriptionLayout, waitLayout;
    ViewGroup mainLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fastjob_client, container, false);

        mainLayout = view.findViewById(R.id.CFJF_MainLayout);
        waitLayout = view.findViewById(R.id.CFJF_waitLayout);
        descriptionLayout = view.findViewById(R.id.CFJF_descriptionLayout);
        acceptLayout = view.findViewById(R.id.CFJF_acceptLayout);

        fj_type = Objects.requireNonNull(getArguments()).getInt("fj_type", 1);
        initFirebase();

        return view;
    }

    private void initFirebase(){
        reference = new CommonMethods().getRef(getActivity());
        auth = new CommonMethods().getAuth(getActivity());
       // storage = new CommonMethods().getStorage(getActivity());
        reference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(fj_type == 2) Description();
                    else{
                        reference.child("FastJobs").child(user.getRequested_fastJob()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                fj = dataSnapshot.getValue(FastJob.class);
                                switch (fj.getClientStage()){
                                    case 2: Wait(fj); break;
                                    case 3: Accept(); break;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void Description(){

        type = getArguments().getString("work_type", "");
        MainCardView(type);
        location = new Gson().fromJson(getArguments().getString("location", ""), LatLng.class);
        TransitionManager.beginDelayedTransition(mainLayout);
        MaterialCardView RcardView = view.findViewById(R.id.CFJF_RequestMCardView);
        RcardView.setVisibility(View.VISIBLE);
        MaterialCardView WcardView = view.findViewById(R.id.CFJF_workerMCardView);
        WcardView.setVisibility(View.GONE);
        waitLayout.setVisibility(View.GONE);


        final EditText description;
        final Button initFastJob;

        description = view.findViewById(R.id.CFJF_description);
        initFastJob = view.findViewById(R.id.CFJF_initFastJob);
        description.requestFocus();
        initFastJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!description.getText().toString().equals("")){
                    if(location.latitude == 0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Onde você está?");
                        builder.setMessage("Não conseguimos localizar você pelo GPS. Deseja usar o endereço padrão: "+ user.getMainAddress().getLogradouro() + ", "+user.getMainAddress().getNumero()+"?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Usar o padrão", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fj = new FastJob(user.getUuid(), description.getText().toString(), type, location.latitude, location.longitude, "FastJob: "+fj_uuid.toString());
                                fj.setWorkerStage(1);
                                fj.setClientStage(2);
                                user.setRequested_fastJob("FastJob: "+fj_uuid.toString());
                                reference.child("FastJobs").child("FastJob: "+fj_uuid.toString()).setValue(fj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference.child("Users").child(auth.getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Wait(fj);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Tentar o GPS novamente", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Deseja utilizar este endereço mesmo?");
                        builder.setMessage("Deseja utilizar o endereço: "+ getAddressFromLocation(location));
                        builder.setPositiveButton("Usar este endereço", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fj = new FastJob(user.getUuid(), description.getText().toString(), type, location.latitude, location.longitude, "FastJob: "+fj_uuid.toString());
                                fj.setWorkerStage(1);
                                fj.setClientStage(2);
                                user.setRequested_fastJob("FastJob: "+fj_uuid.toString());
                                reference.child("FastJobs").child("FastJob: "+fj_uuid.toString()).setValue(fj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference.child("Users").child(auth.getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Wait(fj);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Mudar o endereço", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setCancelable(false);
                        builder.show();

                    }

                }else description.setError("Insira uma descrição válida");
            }
        });


    }

    private String getAddressFromLocation(LatLng location){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getThoroughfare() + ", "+ addresses.get(0).getSubThoroughfare() +" - "+addresses.get(0).getLocality();
               }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void MainCardView(String type) {
        ImageView fullSize_work, work_icon;
        AutofitTextView work_name;
        work_name = view.findViewById(R.id.CFJF_work_name);
        work_name.setText(type);
        work_icon = view.findViewById(R.id.CFJF_work_image);
        //fullSize_work = view.findViewById(R.id.CFJF_fullSize_work_image);

        switch(type){
            case "Costureiro": work_icon.setImageResource(R.drawable.costura); break;
            case "Pedreiro": work_icon.setImageResource(R.drawable.constructor); break;
            case "Freteiro": work_icon.setImageResource(R.drawable.transporter); break;
            case "Motorista": work_icon.setImageResource(R.drawable.driver); break;
            case "Diarista": work_icon.setImageResource(R.drawable.maid); break;
            case "Jardineiro": work_icon.setImageResource(R.drawable.gardner); break;
            case "Encanador": work_icon.setImageResource(R.drawable.plumber); break;
            case "Pintor": work_icon.setImageResource(R.drawable.painter); break;
        }
    }

    private void Wait(final FastJob fj) {
        TransitionManager.beginDelayedTransition(mainLayout);
        MainCardView(fj.getType());
        MaterialCardView RcardView = view.findViewById(R.id.CFJF_RequestMCardView);
        RcardView.setVisibility(View.VISIBLE);
        MaterialCardView WcardView = view.findViewById(R.id.CFJF_workerMCardView);
        WcardView.setVisibility(View.GONE);
        descriptionLayout.setVisibility(View.GONE);
        waitLayout.setVisibility(View.VISIBLE);
        Button cancel = view.findViewById(R.id.CFJF_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelFastJob();
            }
        });

        reference.child("FastJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FastJob faj = dataSnapshot.getValue(FastJob.class);
                if(faj.getUid().equals(fj.getUid())) {
                    if (faj.getPossibleWorkers().size() > 0) {
                        ClientFastJobFragment.this.fj =faj;
                        switch (faj.getClientStage()){
                            case 2: Wait(faj); break;
                            case 3: Accept(); break;
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FastJob faj = dataSnapshot.getValue(FastJob.class);
                if(faj.getUid().equals(fj.getUid())) {
                    if (faj.getPossibleWorkers().size() > 0) {
                        ClientFastJobFragment.this.fj =faj;
                        switch (faj.getClientStage()){
                            case 2: Wait(faj); break;
                            case 3: Accept(); break;
                        }
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
        reference.child("FastJobs").child(user.getRequested_fastJob()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user.setRequested_fastJob("");
                reference.child("Users").child(user.getUuid()).setValue(user);
                mlistener.DeleteFastJob();
                Toast.makeText(getActivity(), "Você cancelou o FastJob com sucesso", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void Accept(){
        mlistener.CFJFragmentInteraction();
       final PossibleWorker worker = fj.getPossibleWorkers().get(0);

        TransitionManager.beginDelayedTransition(mainLayout);
        MaterialCardView RcardView = view.findViewById(R.id.CFJF_RequestMCardView);
        RcardView.setVisibility(View.GONE);
        MaterialCardView WcardView = view.findViewById(R.id.CFJF_workerMCardView);
        WcardView.setVisibility(View.VISIBLE);

        AutofitTextView worker_name = view.findViewById(R.id.CFJF_worker_name);
        worker_name.setText(worker.getWorker().getName()+ " " +worker.getWorker().getMiddlename());

        AutofitTextView worker_type = view.findViewById(R.id.CFJF_worker_type);
        worker_type.setText(worker.getWorker().getWork_type());

        AutofitTextView worker_rating = view.findViewById(R.id.CFJF_worker_rating);
        worker_rating.setText(worker.getWorker().getRating()+"☆");

        AutofitTextView job_price = view.findViewById(R.id.CFJF_job_price);
        job_price.setText("R$ "+worker.getPrice());

        ImageView worker_image = view.findViewById(R.id.CFJF_worker_image);
        Picasso.get()
                .load(worker.getWorker().getUri())
                .transform(new CircleTransform())
                .into(worker_image);

        ImageView fullSize_job = view.findViewById(R.id.CFJF_fullSize_Job);
        switch (worker.getWorker().getWork_type()) {
            case "Costureiro":  Picasso.get().load("https://certificadocursosonline.com/wp-content/uploads/2018/07/curso-de-corte-e-costura.jpg").into(fullSize_job); break;
            case "Pedreiro":  Picasso.get().load("https://certificadocursosonline.com/wp-content/uploads/2018/07/curso-de-pedreiro.jpg").into(fullSize_job); break;
            case "Freteiro":  Picasso.get().load("http://www.transmaso.com.br/image_crop.php?imagem=20180521_111918_dsc_0139.jpg&largura=850&&altura=350&pasta=uploads/img_noticias").into(fullSize_job); break;
            case "Motorista":  Picasso.get().load("https://bibliotecaprt21.files.wordpress.com/2015/10/article.jpg").into(fullSize_job); break;
            case "Diarista":  Picasso.get().load("https://www.mumsnet.com/system/1/assets/files/000/011/233/11233/f8a6c420c/original/xsmall-bone-kitchen.jpg.pagespeed.ic.sy2XXDmX6u.jpg").into(fullSize_job); break;
            case "Jardineiro":  Picasso.get().load("http://smsmanpower.com/wp-content/uploads/2013/10/riding-slider.jpg").into(fullSize_job); break;
            case "Encanador":  Picasso.get().load("https://nicholsandphipps.com/wp-content/uploads/2018/04/Drain-Maintenance-101.jpg").into(fullSize_job); break;
            case "Pintor":  Picasso.get().load("http://www.mygreatpaintingservices.sitew.de/fs/Root/dhon3-2.jpg").into(fullSize_job); break;
        }
        Button accept = view.findViewById(R.id.CFJF_accept_worker);
        Button refuse = view.findViewById(R.id.CFJF_refuse_worker);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptWorker(worker);
            }
        });

        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefuseWorker(worker);
            }
        });
    }

    private void RefuseWorker(final PossibleWorker worker) {
        int cont = 1;
        while (fj.getPossibleWorkers().size() >= cont){
            if(fj.getPossibleWorkers().get(cont - 1).getWorker().getUuid().equals(worker.getWorker().getUuid())){
                fj.getPossibleWorkers().remove(cont - 1);
                fj.getRefusedWorkers().add(worker.getWorker().getUuid());
            }
            cont++;
        }
        reference.child("Users").child(fj.getUid()).setValue(fj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Você recusou "+ worker.getWorker().getName()+" com sucesso.", Toast.LENGTH_LONG).show();
                mlistener.RefusedWorker(fj);

            }
        });
    }

    private void AcceptWorker(PossibleWorker worker) {
        fj.setWorker(worker.getWorker().getUuid());
        fj.getPossibleWorkers().clear();
        fj.setPrice(worker.getPrice());

        reference.child("Users").child(worker.getWorker().getUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                user.setActive_fastJob(fj.getUid());
                reference.child("FastJobs").child(fj.getUid()).setValue(fj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        reference.child("Users").child(user.getUuid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                              reference.child("Users").child(auth.getCurrentUser().getUid()).child("requested_fastJob").setValue(fj.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      Toast.makeText(getActivity(), "Abre o portão que eu cheguei!!! Aguarde "+user.getName()+" no local combinado!", Toast.LENGTH_LONG).show();
                                      Worker(user);
                                  }
                              });

                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void Worker(User worker){
        mlistener.InitFastJob(worker, fj);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnCFJFragmentInteractionListener) {
            mlistener = (OnCFJFragmentInteractionListener) context;
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

    public interface OnCFJFragmentInteractionListener {
        void CFJFragmentInteraction();
        void InitFastJob(User worker, FastJob fastJob);
        void DeleteFastJob();
        void RefusedWorker(FastJob fastJob);
    }


}

