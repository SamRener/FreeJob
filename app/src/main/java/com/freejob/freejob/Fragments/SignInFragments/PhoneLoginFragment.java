package com.freejob.freejob.Fragments.SignInFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.freejob.freejob.Activities.MainActivity;
import com.freejob.freejob.BLLs.Phone_Login_Methods;
import com.freejob.freejob.Items.Phone_Login;
import com.freejob.freejob.R;
import com.rilixtech.CountryCodePicker;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

public class PhoneLoginFragment extends Fragment {

    private OnPLFInteractionListener mListener;
    public CountryCodePicker ccp;
    public Button voltar, verificar, code;
    public EditText phone;
    public ProgressDialog progress;
    private View view;
    Phone_Login phone_login = new Phone_Login();
    Phone_Login_Methods phone_login_methods;

    public PhoneLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_phone_login, container, false);
        phone_login_methods = new Phone_Login_Methods(this, getContext(), getActivity());
        phone_login_methods.Init_Firebase();
        setViews();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPLFInteractionListener) {
            mListener = (OnPLFInteractionListener) context;
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

    public interface OnPLFInteractionListener {
        void onPLFInteraction();
    }

    private void setViews() {
        //Setting Phone number

        phone = view.findViewById(R.id.FPL_phone);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        ccp = view.findViewById(R.id.FPL_ccp);

        //Verification Button Configuration

        verificar = view.findViewById(R.id.FPL_verificar);
        verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_number();
            }
        });

        //Back Button Configuration

        voltar = view.findViewById(R.id.FPL_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),MainActivity.class));
                getActivity().finish();
            }
        });
    }
    private void verify_number(){
    if(!phone.getText().toString().equals("")) {
        ccp.registerPhoneNumberTextView(phone);
        phone_login.setNumber_phone(ccp.getFullNumberWithPlus());
        phone_login.setNumber_phone(phone_login.getNumber_phone().replace(" ", ""));
        phone_login.setNumber_phone(phone_login.getNumber_phone().replace("-", ""));

        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progress = new ProgressDialog(getActivity());
                progress.setTitle("Verificando o número...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setCanceledOnTouchOutside(false);
                progress.setCancelable(false);
                progress.show();
                phone_login_methods.signInWithAlreadyCreatedPhone(phone_login, progress);

            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setCancelable(false);
        builder.setMessage("O número " + phone_login.getNumber_phone() + " está correto?");
        dialog = builder.create();
        dialog.show();
        }
        else{
        phone.setError("Insira um telefone válido");
    }
    }

    public void OnCodeSent(){
        progress.dismiss();
        new LovelyTextInputDialog(getActivity())
                .setConfirmButton("Verificar", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String code) {
                        phone_login_methods.verifyPhoneNumberWithCode(code);
                    }
                })
                .setNegativeButton("Reenviar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        phone_login_methods.resendVerificationCode(phone_login);
                    }
                })
                .setTitle("Código")
                .setMessage("Insira o código que lhe enviamos")
                .setHint("Insira o código")
                .setCancelable(false)
                .show();
    }



    //Necessary Methods (KEEP OUT)
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Phone_Login.getKeyVerifyInProgress(), phone_login_methods.phone_login.getmVerificationInProgress());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


    }
}
