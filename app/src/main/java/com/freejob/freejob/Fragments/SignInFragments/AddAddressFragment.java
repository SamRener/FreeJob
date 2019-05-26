package com.freejob.freejob.Fragments.SignInFragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Globals.SharedPreferences;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class AddAddressFragment extends Fragment {
    private View view;
    DatabaseReference reference;
    FirebaseAuth auth;
    User user;
    EditText cep, log, comp, num, city, state, bairro;
    Button atual_loc, cadastrar;
    Location location;
    private OnFAAFragmentInteractionListener mlistener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_address, container, false);
        initFirebase();
        return view;
    }

    private void initFirebase() {
        reference = new CommonMethods().getRef(getContext());
        auth = new CommonMethods().getAuth(getContext());
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
        cep = view.findViewById(R.id.FAD_CEP);
        log = view.findViewById(R.id.FAD_Log);
        comp = view.findViewById(R.id.FAD_Complemento);
        num = view.findViewById(R.id.FAD_Numero);
        city = view.findViewById(R.id.FAD_Cidade);
        state = view.findViewById(R.id.FAD_Estado);
        bairro = view.findViewById(R.id.FAD_Bairro);

        atual_loc = view.findViewById(R.id.FAD_actual_location);
        atual_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        cadastrar = view.findViewById(R.id.FAD_cadastrar);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                CadastrarEndereco();
            }
        });


    }

    private void CadastrarEndereco() {
        if(!cep.getText().toString().isEmpty()){
            if(!log.getText().toString().isEmpty()) {
                if (!bairro.getText().toString().isEmpty()) {
                    if (!comp.getText().toString().isEmpty()) {
                        if (!num.getText().toString().isEmpty()) {
                            if (!city.getText().toString().isEmpty()) {
                                if (!state.getText().toString().isEmpty()) {
                                    final ProgressDialog dialog = new ProgressDialog(getContext());
                                    dialog.setMessage("Aguarde enquanto tentamos localizar a sua posição...");
                                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    dialog.show();
                                    dialog.setCancelable(false);
                                    reference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final User user = dataSnapshot.getValue(User.class);
                                            user.setMainAddress(MakeAddress());

                                            reference.child("Users").child(auth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (user.getType().equals("T")) {
                                                       mlistener.FAAFragmentInteraction();
                                                       dialog.dismiss();
                                                    } else

                                                        mlistener.FAAFragmentInteraction(true);
                                                        dialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }else state.setError("Insira um valor válido");
                            }else city.setError("Insira um valor válido");
                        }else num.setError("Insira um valor válido");
                    }else comp.setError("Insira um valor válido");
                }else bairro.setError("Insira um valor válido");
            }else log.setError("Insira um valor válido");
        }else cep.setError("Insira um valor válido");
    }

    private com.freejob.freejob.Items.Address MakeAddress() {
        String nume = cep.getText().toString().replace("-","");
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addresses;
        String s = log.getText().toString();
        LatLng lg = new LatLng(0,0);
        try {
            addresses = geocoder.getFromLocationName (s, 1);
            if(addresses.size() > 0) {
                lg = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            }
            else{
                lg = new LatLng(0, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        com.freejob.freejob.Items.Address ad = new com.freejob.freejob.Items.Address(log.getText().toString(), comp.getText().toString(), bairro.getText().toString(), city.getText().toString(), state.getText().toString(), "BR", Integer.parseInt(num.getText().toString()), nume);
        ad.setLat(lg.latitude);
        ad.setLng(lg.longitude);
        return ad;
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 7000);
        }
        else{
            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            boolean gps = false, net = false;
            try {
                gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setMessage("Para continuar, ative os serviços de localização do seu dispositivo");
                dialog.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Não ativar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            }
            if (!net) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setMessage("Para continuar, ative os serviços de internet do seu dispositivo");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
            }
            if (net && gps) {
                final ProgressDialog dialog = new ProgressDialog(getContext());
                dialog.setMessage("Aguarde enquanto tentamos localizar a sua posição...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        getAddressFromLocation(location, dialog);
                        AddAddressFragment.this.location = location;
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

            }
        }
    }

    private void getAddressFromLocation(Location location, ProgressDialog dialog){
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getContext(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    log.setText(addresses.get(0).getThoroughfare());
                    num.setText(addresses.get(0).getSubThoroughfare());
                    city.setText(addresses.get(0).getLocality());
                    state.setText(addresses.get(0).getAdminArea());
                    cep.setText(addresses.get(0).getPostalCode());
                    num.requestFocus();
                    Toast.makeText(getContext(), "Alguns dados não foram localizados, preencha os campos vazios", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFAAFragmentInteractionListener) {
            mlistener = (OnFAAFragmentInteractionListener) context;
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

    public interface OnFAAFragmentInteractionListener {
        void FAAFragmentInteraction();
        void FAAFragmentInteraction(boolean activity);
    }


}
