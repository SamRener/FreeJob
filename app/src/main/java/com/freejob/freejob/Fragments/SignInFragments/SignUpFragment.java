package com.freejob.freejob.Fragments.SignInFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.freejob.freejob.Activities.ClientActivity;
import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.freejob.freejob.Transforms.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class SignUpFragment extends Fragment {

    View view;
    Button cadastrar;
    RadioButton cliente, worker;
    EditText name, middlename, nasc;
    ImageButton take;
    Spinner gender;

    String nome;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageRef;

    private OnSGUFragmentInteractionListener mListener;

    private Uri photoPath;
    private final int PICK_IMAGE_REQUEST = 71;
    public SignUpFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        initVariables();
        initFirebase();
        return view;
    }

    private void initVariables(){

        name = view.findViewById(R.id.FSI_name);
        middlename = view.findViewById(R.id.FSI_middlename);
        nasc = view.findViewById(R.id.FSI_nascimento);
        cadastrar = view.findViewById(R.id.FSI_cadastrar);
        cliente = view.findViewById(R.id.FSI_cliente);
        worker  = view.findViewById(R.id.FSI_worker);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInUser();
            }
        });

        take = view.findViewById(R.id.FSI_profile_photo);
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              ChooseProfileImage();
            }
        });

        nasc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("/")){
                    nasc.setText(nasc.getText().toString().replace("/",""));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        gender = view.findViewById(R.id.FSI_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.genders, android.R.layout.simple_spinner_item);
        gender.setAdapter(adapter);
    }


    private void ChooseProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            photoPath = data.getData();
            Picasso.get().load(photoPath).transform(new CircleTransform()).into(take);
            take.setBackground(null);
        }
    }

    private void SignInUser(){
        if(!name.getText().toString().equals("")) {
            if (!middlename.getText().toString().equals("")) {
                if (!nasc.getText().toString().equals("")) {
                    if(nasc.getText().toString().length() == 10) {
                        if(cliente.isChecked() || (worker.isChecked())) {
                            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setTitle("Organizando as coisas...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            nome = name.getText().toString();
                            if (!nome.equals("")) {
                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name.getText().toString())
                                            .build();

                                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (photoPath != null) {
                                                            storageRef.child("Users").child("User: " + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profilePhoto").putFile(photoPath)
                                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                                                            final Uri[] URI = new Uri[1];
                                                                            URI[0] = taskSnapshot.getDownloadUrl();
                                                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                                    .setPhotoUri(URI[0])
                                                                                    .build();
                                                                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    createUser(name.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), cliente.isChecked() ? "C" : worker.isChecked() ? "T" : "", progressDialog, null);
                                                                                }
                                                                            });

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            progressDialog.dismiss();
                                                                            Snackbar.make(view.findViewById(android.R.id.content), "Não foi possível fazer o upload da sua foto. Tente novamente mais tarde!",
                                                                                    Snackbar.LENGTH_LONG).show();
                                                                            createUser(name.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), cliente.isChecked() ? "C" : worker.isChecked() ? "T" : "", progressDialog, null);

                                                                        }
                                                                    })
                                                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                                            progressDialog.setMessage("Imagem " + (int) progress + "% carregada");
                                                                            progressDialog.setProgress((int) progress);
                                                                        }
                                                                    });
                                                        } else {
                                                            if (worker.isChecked()) {
                                                                progressDialog.dismiss();
                                                                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                                                dialog.setMessage("Para se cadastrar como trabalhador, você precisa selecionar uma foto de perfil");
                                                                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                                        take.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotation360));
                                                                    }
                                                                });
                                                                dialog.show();
                                                            } else {
                                                                createUser(name.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), cliente.isChecked() ? "C" : worker.isChecked() ? "T" : "", progressDialog, null);
                                                            }
                                                        }

                                                    }
                                                }
                                            });
                                }
                            } else name.setError("Insira um nome válido");
                        }else {
                            cliente.setError("Selecione um tipo válido");
                            worker.setError("Selecione um tipo válido");
                        }
                    }else nasc.setError("Insira uma data válida");
                }else nasc.setError("Insira uma data válida");
            }else middlename.setError("Insira um sobrenome válido");
        }else name.setError("Insira um nome válido");
    }

    private void initFirebase() {
        storageRef = FirebaseStorage.getInstance().getReference();
        FirebaseApp.initializeApp(getActivity());
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    private User createUser(String name, String email, String type, final ProgressDialog progressDialog, String uri) {
        final User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),name,"",type, "5.0", name, middlename.getText().toString(), "", nasc.getText().toString(), "", email, "-19.9181213", "-43.9599618", uri);
        user.setWork_type("nulo");
        user.setCPF("nuo");
        user.getMainAddress().setLogradouro("nuo");
        user.setActive_fastJob("nulo");
        user.setRequested_fastJob("nulo");
        user.setWaitingFastJob("nulo");
        reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if(mListener != null){
                        progressDialog.dismiss();
                        mListener.SGUFragmentInteraction();
                    }

            }
        });
        return user;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSGUFragmentInteractionListener) {
            mListener = (OnSGUFragmentInteractionListener) context;
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

    public interface OnSGUFragmentInteractionListener {
        void SGUFragmentInteraction();
    }
}
