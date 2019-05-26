
package com.freejob.freejob.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.freejob.freejob.Adapters.InfoViewAdapter;
import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Fragments.FastJobFragments.ClientFastJobFragment;
import com.freejob.freejob.Fragments.Informations.ConfigFragment;
import com.freejob.freejob.Fragments.Informations.ScheduleFragment;
import com.freejob.freejob.Fragments.SignInFragments.FullSignInFragment;
import com.freejob.freejob.Fragments.SignInFragments.WorksFragment;
import com.freejob.freejob.Globals.SharedPreferences;
import com.freejob.freejob.Items.FastJob;
import com.freejob.freejob.Items.PossibleWorker;
import com.freejob.freejob.Items.Request;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.freejob.freejob.Transforms.CircleTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;


public class ClientActivity extends AppCompatActivity implements  WorksFragment.OnWFInteractionListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ClientFastJobFragment.OnCFJFragmentInteractionListener,
        FullSignInFragment.OnFSIFragmentInteractionListener{

    FrameLayout layout;
    DrawerLayout mapLayout;
    LinearLayout sidemenu;
    ListView menu_List;
    private LocationRequest locationRequest;
    private GoogleApiClient apiClient;
    private Location lastLocation;
    private ImageButton loc;

    //Java Elements
    private boolean gps = false, net = false;
    private User user;

    public ClientFastJobFragment fj;
    DatabaseReference reference;
    GeoFire geoFire;
    StorageReference storageRef;
    List<Request> requests =new ArrayList<>();
    ListView lv_search;
    EditText et_search;
    ArrayAdapter<CharSequence> s_adapter;

    boolean lv =false;
    final LatLng[] address = {new LatLng(0,0)};
    ImageButton open_sidebar, change;
    GoogleMap map;
    Marker mCurrent;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private InfoViewAdapter infoAdapter = new InfoViewAdapter(ClientActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        layout = findViewById(R.id.AC_fragment_content);
        initFirebase();

        SupportMapFragment mapFragment = (SupportMapFragment) Objects.requireNonNull(ClientActivity.this).getSupportFragmentManager()
                .findFragmentById(R.id.CMF_map);
        mapFragment.getMapAsync(this);
        setUpLocation();
        LocateLocation();
    }

    private void initFirebase() {
        //storageRef = new CommonMethods().getStorage(ClientActivity.this);
        reference = new CommonMethods().getRef(ClientActivity.this);
        reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
               user.setUri(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        initVariables();
                        ShowWorkers();
                        HandleRequestedFastJob();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initVariables() {
        mapLayout = findViewById(R.id.AC_map_layout);
        initList();
        loc = findViewById(R.id.AC_loca);
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startLocationUpdates();
                CheckforLocation();
                loc.startAnimation(AnimationUtils.loadAnimation(ClientActivity.this, R.anim.rotation360));
                et_search.clearFocus();
            }
        });
        lv_search = findViewById(R.id.AC_lv_search);
        et_search = findViewById(R.id.AC_et_search);
        s_adapter = ArrayAdapter.createFromResource(ClientActivity.this, R.array.work_type, android.R.layout.simple_list_item_1);
        lv_search.setAdapter(s_adapter);
        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lv) {
                    et_search.setEnabled(true);
                    TransitionManager.beginDelayedTransition((ViewGroup)findViewById(R.id.AC_searchLayout), new ChangeBounds());
                    et_search.setBackgroundResource(R.drawable.ets_rounded_border);
                    lv_search.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    lv = true;
                }
            }
        });
        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    TransitionManager.beginDelayedTransition((ViewGroup)findViewById(R.id.AC_searchLayout), new ChangeBounds());
                    lv_search.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                    et_search.setBackgroundResource(R.drawable.search_rounded_corners);

                    lv = false;
                }
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                s_adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!user.getCPF().equals("nulo")) {
                   // if (CheckforLocation().longitude != 0) {

                        et_search.clearFocus();
                        TransitionManager.beginDelayedTransition((ViewGroup)findViewById(R.id.AC_searchLayout), new ChangeBounds());
                        lv_search.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                        et_search.setBackgroundResource(R.drawable.search_rounded_corners);
                        lv = false;
                        CreateFastJobViews(position);

                   // } else {
                      /*  if(address[0].longitude != 0) {
                            et_search.clearFocus();
                            TransitionManager.beginDelayedTransition((ViewGroup)findViewById(R.id.AC_searchLayout), new ChangeBounds());
                            lv_search.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                            et_search.setBackgroundResource(R.drawable.search_rounded_corners);
                            lv = false;
                            CreateFastJobViews(position);
                       }else
                        Toast.makeText(ClientActivity.this, "Habilite ou aguarde para que o GPS localize sua posição", Toast.LENGTH_LONG).show();
                  //  } */
                }else{
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    getSupportFragmentManager().beginTransaction().add(R.id.AC_fragment_content, new FullSignInFragment()).commit();
                    showView(layout);
                    //mapLayout.closeDrawer(sidemenu, true);
                    Toast.makeText(ClientActivity.this, "Para pedir um FastJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        open_sidebar = findViewById(R.id.AC_open_sidebar);
        open_sidebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapLayout.openDrawer(findViewById(R.id.MS_LinearLayout), true);
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                et_search.clearFocus();
            }
        });

    }

    private void CreateFastJobViews(int position) {
        Gson gson = new Gson();
        String[] works = getResources().getStringArray(R.array.work_type);
        Bundle bundle = new Bundle();
        bundle.putString("work_type", works[position]);
        bundle.putInt("fj_type", 2);
        String json = gson.toJson(CheckforLocation());
        bundle.putString("location", json);
        fj = new ClientFastJobFragment();
        fj.setArguments(bundle);
        et_search.clearFocus();

        LinearLayout mainLayout = findViewById(R.id.AC_mainLayout);
        TransitionManager.beginDelayedTransition(mainLayout, new ChangeBounds());
        CardView cardView = findViewById(R.id.AC_CV_Map);
        CoordinatorLayout relativeLayout = findViewById(R.id.AC_CL_Map);
        FrameLayout fj_layout = findViewById(R.id.AC_FJ_layout);

        LinearLayout.LayoutParams card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        card_lp.weight = Float.parseFloat("1");
        card_lp.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        cardView.setLayoutParams(card_lp);
        cardView.setRadius(10);
        cardView.setCardElevation(10);

        MaterialCardView.LayoutParams relative_lp = new MaterialCardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relative_lp.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        relativeLayout.setLayoutParams(relative_lp);

        card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        card_lp.weight = Float.parseFloat("1");

        fj_layout.setLayoutParams(card_lp);
        getSupportFragmentManager().beginTransaction().replace(R.id.AC_FJ_layout, fj).commit();

    }

    private void CreateFastJobViews() {
        Bundle bundle = new Bundle();
        bundle.putInt("fj_type", 1);
        fj = new ClientFastJobFragment();
        fj.setArguments(bundle);
        et_search.clearFocus();

        LinearLayout mainLayout = findViewById(R.id.AC_mainLayout);
        TransitionManager.beginDelayedTransition(mainLayout, new ChangeBounds());
        CardView cardView = findViewById(R.id.AC_CV_Map);
        CoordinatorLayout relativeLayout = findViewById(R.id.AC_CL_Map);
        FrameLayout fj_layout = findViewById(R.id.AC_FJ_layout);

        LinearLayout.LayoutParams card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        card_lp.weight = Float.parseFloat("1");
        card_lp.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        cardView.setLayoutParams(card_lp);
        cardView.setRadius(10);
        cardView.setCardElevation(10);

        MaterialCardView.LayoutParams relative_lp = new MaterialCardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relative_lp.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        relativeLayout.setLayoutParams(relative_lp);

        card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        card_lp.weight = Float.parseFloat("1");

        fj_layout.setLayoutParams(card_lp);
        getSupportFragmentManager().beginTransaction().replace(R.id.AC_FJ_layout, fj).commit();

    }

    private void DestroyFastJobViews(){
        TransitionManager.beginDelayedTransition((ViewGroup)findViewById(R.id.AC_searchLayout), new ChangeBounds());
        lv_search.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        et_search.setBackgroundResource(R.drawable.search_rounded_corners);

        lv = false;
        LinearLayout mainLayout = findViewById(R.id.AC_mainLayout);
        TransitionManager.beginDelayedTransition(mainLayout, new ChangeBounds());
        CardView cardView = findViewById(R.id.AC_CV_Map);
        CoordinatorLayout relativeLayout = findViewById(R.id.AC_CL_Map);
        FrameLayout fj_layout = findViewById(R.id.AC_FJ_layout);

        loc.requestFocusFromTouch();
        LinearLayout.LayoutParams card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        fj_layout.setLayoutParams(card_lp);
        card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        cardView.setLayoutParams(card_lp);
        fj_layout.removeAllViewsInLayout();
        CardView.LayoutParams relative_lp = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(relative_lp);

        RelativeLayout rl = findViewById(R.id.AC_RL_Map);
        rl.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.AC_searchLayout).setLayoutParams(rlp);
    }

    private void initList() {

        AutofitTextView userName = findViewById(R.id.MS_UserName);
        AutofitTextView userPhone = findViewById(R.id.MS_UserPhone);
        AutofitTextView workType = findViewById(R.id.MS_work_type);
        AutofitTextView rating = findViewById(R.id.MS_rating);

        final ImageView profilePhoto = findViewById(R.id.MS_profilePhoto);

        NavigationView navigationView = findViewById(R.id.MS_LinearLayout);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    //case R.id.MM_config: openFrag(new ConfigFragment()); break;
                   // case R.id.MM_contacts: openFrag(new ScheduleFragment()); break;
                    case R.id.MM_exit: new CommonMethods().LogOut(ClientActivity.this); startActivity(new Intent(ClientActivity.this, MainActivity.class)); ClientActivity.this.finish(); break;
                    case R.id.MM_home: layout.setLayoutParams(new LinearLayout.LayoutParams(0,0)); break;
                    case R.id.MM_apagar:
                        reference.child("FastJobs").child(user.getRequested_fastJob()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                FastJob fj = dataSnapshot.getValue(FastJob.class);

                                reference.child("Users").child(fj.getWorker()).child("active_fastJob").setValue("nulo");
                                reference.child("Users").child(fj.getWorker()).child("waitingFastJob").setValue("nulo");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        reference.child("FastJobs").child(user.getRequested_fastJob()).removeValue(); reference.child("Users").child(user.getUuid()).child("requested_fastJob").setValue("nulo"); startActivity(new Intent(ClientActivity.this, ClientActivity.class)); ClientActivity.this.finish(); break;
                    //case R.id.MM_schedule: openFrag(new ScheduleFragment()); break;
                    //case R.id.MM_services: openFrag(new ScheduleFragment()); break;
                }
                return true;
            }

        });
        userName.setText(user.getName() + " " + user.getMiddlename());
        workType.setText("Cliente");
        rating.setText(user.getRating()+"☆");
        userPhone.setText(user.getPhone());

        Picasso.get()
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .transform(new CircleTransform())
                .into(profilePhoto);

        change = findViewById(R.id.MS_sidebar_change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getWork_type().equals("nulo") && !user.getCPF().equals("nulo")) {
                    reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("type").setValue("T");
                    startActivity(new Intent(ClientActivity.this, WorkerActivity.class));
                    ClientActivity.this.finish();
                }
                else if(user.getCPF().equals("nulo")){

                    openFrag(new FullSignInFragment());
                    Toast.makeText(ClientActivity.this, "Para usar o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                }
                else{
                   openFrag(new WorksFragment());
                    Toast.makeText(ClientActivity.this, "Para trabalhar com o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void openFrag(Fragment fragment) {
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        getSupportFragmentManager().beginTransaction().add(R.id.AC_fragment_content, fragment).commit();
        showView(layout);
    }

    public LatLng CheckforLocation() {
        if (ActivityCompat.checkSelfPermission(ClientActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ClientActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ClientActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 7000);
        } else {
            LocationManager lm = (LocationManager) ClientActivity.this.getSystemService(Context.LOCATION_SERVICE);
            try {
                gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ClientActivity.this);
                dialog.setMessage("Para continuar, ative os serviços de localização do seu dispositivo");
                dialog.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ClientActivity.this.startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Não ativar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
                dialog.setCancelable(false);
            }
            if (!net) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(ClientActivity.this);
                dialog.setMessage("Para continuar, ative os serviços de internet do seu dispositivo");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
            }
            if (net && gps) {
                return displayLocation();

            }
        }
        return new LatLng(0,0);
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(ClientActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ClientActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ClientActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 7001);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                CheckforLocation();
            }
        }

    }

    private void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        apiClient.connect();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ClientActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, ClientActivity.this, PLAY_SERVICE_RES_REQUEST).show();
            } else {
                Toast.makeText(ClientActivity.this, "O seu dispositivo não é compatível", Toast.LENGTH_SHORT).show();
                ClientActivity.this.finish();
            }
            return false;
        }
        return true;
    }

    private LatLng LocateLocation() {

        if(map != null) {

            if (ActivityCompat.checkSelfPermission(ClientActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ClientActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ClientActivity.this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 7000);
            } else {
                LocationManager lm = (LocationManager) ClientActivity.this.getSystemService(Context.LOCATION_SERVICE);
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
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ClientActivity.this);
                    dialog.setMessage("Para continuar, ative os serviços de localização do seu dispositivo");
                    dialog.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            ClientActivity.this.startActivity(myIntent);
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
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(ClientActivity.this);
                    dialog.setMessage("Para continuar, ative os serviços de internet do seu dispositivo");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        }
                    });
                }
                if (net && gps) {
                    android.location.LocationListener locationListener = new android.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            address[0] = new LatLng(location.getLatitude(), location.getLongitude());
                            SharedPreferences.setLatLng(ClientActivity.this, address[0]);
                            Toast.makeText(ClientActivity.this, "Teste", Toast.LENGTH_LONG).show();
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
        return address[0];
    }

    private LatLng displayLocation() {

            final LatLng[] address = {new LatLng(0,0)};
        if(map != null) {

            if (net && gps) {

                if (ActivityCompat.checkSelfPermission(ClientActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ClientActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                final double[] lat = new double[1];
                final double[] lng = new double[1];
                if (apiClient == null) {
                    setUpLocation();
                } else if (LocationServices.FusedLocationApi.getLastLocation(apiClient) != null) {
                    lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);

                    lat[0] = lastLocation.getLatitude();
                    lng[0] = lastLocation.getLongitude();

                    if (lastLocation != null) {
                        if (mCurrent != null) mCurrent.remove();

                        infoAdapter.user = user;
                        mCurrent = map.addMarker(new MarkerOptions()
                                .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                                .title(user.getName() + " " + user.getMiddlename()+" (Você)")
                                .snippet("Cliente")
                                .icon(bitmapDescriptorFromVector(ClientActivity.this, R.drawable.ic_you)));
                        //Move Camera
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat[0], lng[0]), 15.0f));
                        address[0] = new LatLng(lat[0], lng[0]);
                        reference.child("Users").child(user.getUuid()).child("lat").setValue(String.valueOf(lat[0]));
                        reference.child("Users").child(user.getUuid()).child("lng").setValue(String.valueOf(lng[0]));
                        return address[0];
                    }else
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-19.9181213, -43.9599618), 1.0f));
                    return address[0];
                }
            }

        }
            return address[0];

    }
    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        CheckforLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // displayLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        apiClient = new GoogleApiClient.Builder(Objects.requireNonNull(ClientActivity.this))
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();
                        apiClient.connect();
                        LocationRequest locationRequest = new LocationRequest();
                        locationRequest.setInterval(UPDATE_INTERVAL);
                        locationRequest.setFastestInterval(FASTEST_INTERVAL);
                        locationRequest.setSmallestDisplacement(DISPLACEMENT);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        CheckforLocation();
                    }
                }
                break;
        }

    }

    private void showView(final View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_right);
        view.startAnimation(animation);
    }

    @Override
    public void WFInteraction() {
        startActivity(new Intent(ClientActivity.this, WorkerActivity.class));
        ClientActivity.this.finish();
    }

    private int dpToPx(int dp) {
        float density = ClientActivity.this.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                ClientActivity.this, R.raw.map_style);
        googleMap.setMapStyle(style);
        map.setInfoWindowAdapter(infoAdapter);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-19.9181213, -43.9599618), 7.0f));
    }

     private void ShowWorkers() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();    
                ShowWorkers();
                return false;
            }
        });
        reference.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                User worker = dataSnapshot.getValue(User.class);

                assert worker != null;
                if(!worker.getType().equals("C") && !worker.getUuid().equals(user.getUuid())) {
                    infoAdapter.user = worker;
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(worker.getLat()), Double.parseDouble(worker.getLng())))
                            .title(worker.getUuid())
                            .snippet(worker.getWork_type())
                            .icon(bitmapDescriptorFromVector(ClientActivity.this, R.drawable.ic_worker)));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User worker = dataSnapshot.getValue(User.class);

                assert worker != null;
                if(!worker.getType().equals("C") && !worker.getUuid().equals(user.getUuid())) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(worker.getLat()), Double.parseDouble(worker.getLng())))
                            .title(worker.getName())
                            .icon(bitmapDescriptorFromVector(ClientActivity.this, R.drawable.ic_worker)));
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

    @Override
    public void CFJFragmentInteraction() {

        LinearLayout mainLayout = findViewById(R.id.AC_mainLayout);
        TransitionManager.beginDelayedTransition(mainLayout, new ChangeBounds());
        CardView cardView = findViewById(R.id.AC_CV_Map);
        CoordinatorLayout relativeLayout = findViewById(R.id.AC_CL_Map);
        FrameLayout fj_layout = findViewById(R.id.AC_FJ_layout);

        LinearLayout.LayoutParams card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        card_lp.weight = Float.parseFloat("1");
        card_lp.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        cardView.setLayoutParams(card_lp);
        cardView.setRadius(10);
        cardView.setCardElevation(10);

        MaterialCardView.LayoutParams relative_lp = new MaterialCardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relative_lp.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        relativeLayout.setLayoutParams(relative_lp);

        card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        card_lp.weight = Float.parseFloat("1.5");

        fj_layout.setLayoutParams(card_lp);


    }

    private void HandleRequestedFastJob(){
        reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requested_fastJob").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String requested_fastJob = dataSnapshot.getValue(String.class);
                if(!requested_fastJob.equals("nulo")){

                    reference.child("FastJobs").child(requested_fastJob).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final FastJob fj = dataSnapshot.getValue(FastJob.class);
                            if(!fj.getWorker().equals("nulo")) {
                                reference.child("Users").child(fj.getWorker()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        InitFastJob(user, fj);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            else{
                                CreateFastJobViews();
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

    @Override
    public void InitFastJob(final User worker, final FastJob fastJob) {

        DestroyFastJobViews();

       et_search.setEnabled(false);
       et_search.setHint("FastJob desabilitado. Finalize o atual primeiro");
       et_search.setBackgroundColor(Color.GRAY);

        final LinearLayout workerLayout = findViewById(R.id.FJ_userLayout);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(workerLayout);

        workerLayout.setVisibility(View.VISIBLE);

        final AutofitTextView name = findViewById(R.id.FJ_user);
        name.setText(worker.getName()+" "+ worker.getMiddlename());

        final AutofitTextView type = findViewById(R.id.FJ_type);
        type.setText(worker.getWork_type());



        ImageView worker_profile = findViewById(R.id.FJ_profile);
        Picasso.get()
                .load(worker.getUri())
                .transform(new CircleTransform())
                .into(worker_profile);

        MaterialButton btn_init = findViewById(R.id.FJ_start_job);
        btn_init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fastJob.setClientWaiting(true);
                reference.child("FastJobs").child(fastJob.getUid()).setValue(fastJob);
            }
        });

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                TransitionManager.beginDelayedTransition((ViewGroup)findViewById(R.id.FJ_MainCardRL), new ChangeBounds().setDuration(30));
            switch(i){
                case BottomSheetBehavior.STATE_COLLAPSED:
                    MaterialCardView cv = findViewById(R.id.FJ_MainCard);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(dpToPx(5),dpToPx(5),dpToPx(5),dpToPx(5));
                    cv.setLayoutParams(lp);

                    cv.setCardElevation(10);
                    cv.setRadius(10);
                    cv.setCardBackgroundColor(Color.WHITE);

                    name.setTextColor(Color.BLACK);
                    type.setTextColor(Color.DKGRAY);

                break;
                case BottomSheetBehavior.STATE_DRAGGING:
                     cv = findViewById(R.id.FJ_MainCard);
                    lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0,0,0,0);
                    cv.setLayoutParams(lp);

                    cv.setCardElevation(0);
                    cv.setRadius(0);
                    cv.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));

                    name.setTextColor(Color.WHITE);
                    type.setTextColor(Color.WHITE);
            }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        reference.child("FastJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue(FastJob.class).getUid().equals(fastJob.getUid())){
                    final FastJob fastJob = dataSnapshot.getValue(FastJob.class);
                    if(fastJob.isWorkerWaiting()){
                        AlertDialog.Builder b = new AlertDialog.Builder(ClientActivity.this);
                        b.setMessage("O trabalhador está pronto para iniciar o serviço, deseja iniciar agora?");
                        b.setTitle("Permissão para Iniciar");
                        b.setCancelable(false);
                        b.setPositiveButton("Iniciar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fastJob.setClientWaiting(true);
                                reference.child("FastJobs").child(fastJob.getUid()).setValue(fastJob);
                            }
                        });
                        b.setNegativeButton("Aguarde um momento", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        b.show();
                    }

                        if (fastJob.isClientWaiting()) {
                            if (fastJob.isWorkerWaiting()) {
                                Toast.makeText(ClientActivity.this, "O Serviço foi iniciado", Toast.LENGTH_LONG).show();
                                final BottomSheetDialog dialog = new BottomSheetDialog(ClientActivity.this);
                                dialog.setContentView(R.layout.in_progress_bsd);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                dialog.show();
                                Chronometer c = dialog.findViewById(R.id.IPB_Chronometer);
                                c.setBase(SystemClock.elapsedRealtime());
                                c.start();

                                MaterialButton finishJob = dialog.findViewById(R.id.IPB_FinishJob);
                                finishJob.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        reference.child("FastJobs").child(fastJob.getUid()).child("wFinished").setValue(true);
                                        dialog.setContentView(R.layout.avaliation_bsd);
                                        TextView text = dialog.findViewById(R.id.ABS_text);
                                        text.setText("Avalie o serviço:");
                                        MaterialButton send = dialog.findViewById(R.id.ABS_send);
                                        send.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AppCompatRatingBar rating = dialog.findViewById(R.id.ABS_rating);
                                                worker.setRating(String.valueOf(rating.getRating()* Float.parseFloat(worker.getRating())/2));
                                                reference.child("Users").child(worker.getUuid()).child("rating").setValue(worker.getRating());
                                                dialog.dismiss();
                                                Toast.makeText(ClientActivity.this, "Serviço finalizado com sucesso, obrigado por usar o FreeJob", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    }
                                });
                                workerLayout.setVisibility(View.GONE);
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

    @Override
    public void DeleteFastJob() {
        DestroyFastJobViews();
    }

    @Override
    public void RefusedWorker(FastJob fastJob) {
        reference.child("FastJobs").child(fastJob.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FastJob fj = dataSnapshot.getValue(FastJob.class);
                if(fj.getPossibleWorkers().size()>0){
                    CFJFragmentInteraction();
                }
                else {
                    CreateFastJobViews();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void FSIFragmentInteraction() {
        reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                if ( !user.getCPF().equals("nulo")) {
                    Toast.makeText(ClientActivity.this, "Dados inseridos com sucesso. Funcionalidades habilitadas", Toast.LENGTH_LONG).show();
                    layout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                } else if (user.getCPF().equals("nulo")) {
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    getSupportFragmentManager().beginTransaction().add(R.id.AW_fragment_content, new FullSignInFragment()).commit();
                    showView(layout);
//                    mapLayout.closeDrawer(sidemenu, true);
                    Toast.makeText(ClientActivity.this, "Para utilizar o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                } else {
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    getSupportFragmentManager().beginTransaction().add(R.id.AW_fragment_content, new WorksFragment()).commit();
                    showView(layout);
//                    mapLayout.closeDrawer(sidemenu, true);
                    Toast.makeText(ClientActivity.this, "Para utilizar o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}