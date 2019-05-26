package com.freejob.freejob.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.geofire.GeoFire;
import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Fragments.FastJobFragments.WorkerFastJobFragment;
import com.freejob.freejob.Fragments.SignInFragments.FullSignInFragment;
import com.freejob.freejob.Fragments.SignInFragments.WorksFragment;
import com.freejob.freejob.Globals.SharedPreferences;
import com.freejob.freejob.Items.FastJob;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.freejob.freejob.Transforms.CircleTransform;
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


public class WorkerActivity extends AppCompatActivity implements  WorksFragment.OnWFInteractionListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, WorkerFastJobFragment.OnWFJFragmentInteractionListener, FullSignInFragment.OnFSIFragmentInteractionListener {

    FrameLayout layout;
    DrawerLayout mapLayout;
    LinearLayout sidemenu;
    private GoogleApiClient apiClient;
    private Location lastLocation;
    private MaterialButton loc, open_sidebar;

    //Java Elements
    private boolean gps = false, net = false;
    private User user;

    public WorkerFastJobFragment fj;
    DatabaseReference reference;
    GeoFire geoFire;
    StorageReference storageRef;


    ImageButton change;
    GoogleMap map;
    Marker mCurrent;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        layout = findViewById(R.id.AW_fragment_content);
        SupportMapFragment mapFragment = (SupportMapFragment) Objects.requireNonNull(WorkerActivity.this).getSupportFragmentManager()
                .findFragmentById(R.id.AW_map);
        mapFragment.getMapAsync(this);
        setUpLocation();
        initFirebase();

    }

    private void initFirebase() {
        storageRef = new CommonMethods().getStorage(WorkerActivity.this);
        reference = new CommonMethods().getRef(WorkerActivity.this);
        reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                user.setUri(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        initVariables();
                         if (user.getCPF().equals("nulo")) {
                            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            getSupportFragmentManager().beginTransaction().add(R.id.AW_fragment_content, new FullSignInFragment()).commit();
                            showView(layout);
                            Toast.makeText(WorkerActivity.this, "Para trabalhar com o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                        } else if(user.getWork_type().equals("nulo")){
                            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            getSupportFragmentManager().beginTransaction().add(R.id.AW_fragment_content, new WorksFragment()).commit();
                            showView(layout);
                            Toast.makeText(WorkerActivity.this, "Para trabalhar com o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                        }
                       if(CheckforLocation().latitude != 0) {HandleNewFastJob();}
                        else {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Servidor lotado, aguarde um momento e tente novamente",
                                       Snackbar.LENGTH_INDEFINITE);

                        snackbar.show();
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        snackbar.setAction("TENTAR NOVAMENTE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                
                            }
                        });
                        }

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initVariables() {
        mapLayout = findViewById(R.id.AW_map_layout);
        initList();
        loc = findViewById(R.id.AW_loca);
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startLocationUpdates();
                HandleNewFastJob();

             //   loc.startAnimation(AnimationUtils.loadAnimation(WorkerActivity.this, R.anim.rotation360));
            }
        });


        open_sidebar = findViewById(R.id.AW_open_sidebar);
        open_sidebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapLayout.openDrawer(findViewById(R.id.MS_LinearLayout), true);

            }
        });

    }

    private void CreateFastJobViews(FastJob fastJob) {
        Gson gson = new Gson();
        Bundle bundle = new Bundle();
        bundle.putString("work_type", fastJob.getType());
        bundle.putInt("fj_type", 1);
        bundle.putString("fastJob", gson.toJson(fastJob));
        fj = new WorkerFastJobFragment();
        fj.setArguments(bundle);

        LinearLayout mainLayout = findViewById(R.id.AW_mainLayout);
        TransitionManager.beginDelayedTransition(mainLayout, new ChangeBounds());
        CardView cardView = findViewById(R.id.AW_CV_Map);
        CoordinatorLayout relativeLayout = findViewById(R.id.AW_CL_Map);
        FrameLayout fj_layout = findViewById(R.id.AW_FJ_layout);

        LinearLayout.LayoutParams card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        card_lp.weight = Float.parseFloat("0.9");
        card_lp.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));

        cardView.setLayoutParams(card_lp);
        cardView.setRadius(10);
        cardView.setCardElevation(10);

        MaterialCardView.LayoutParams relative_lp = new MaterialCardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relative_lp.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));

        relativeLayout.setLayoutParams(relative_lp);

        card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        card_lp.weight = Float.parseFloat("1.1");

        fj_layout.setLayoutParams(card_lp);
        getSupportFragmentManager().beginTransaction().replace(R.id.AW_FJ_layout, fj).commit();

    }

    private void DestroyFastJobViews() {

        LinearLayout mainLayout = findViewById(R.id.AW_mainLayout);
        TransitionManager.beginDelayedTransition(mainLayout, new ChangeBounds());
        CardView cardView = findViewById(R.id.AW_CV_Map);
        CoordinatorLayout relativeLayout = findViewById(R.id.AW_CL_Map);
        FrameLayout fj_layout = findViewById(R.id.AW_FJ_layout);

        LinearLayout.LayoutParams card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        fj_layout.setLayoutParams(card_lp);
        card_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        cardView.setLayoutParams(card_lp);
        fj_layout.removeAllViewsInLayout();
        CardView.LayoutParams relative_lp = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(relative_lp);

        RelativeLayout rl = findViewById(R.id.AW_RL_Map);
        rl.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));

    }

    private void initList() {

        AutofitTextView userName = findViewById(R.id.MS_UserName);
        AutofitTextView userPhone = findViewById(R.id.MS_UserPhone);
        AutofitTextView workType = findViewById(R.id.MS_work_type);
        AutofitTextView rating = findViewById(R.id.MS_rating);

        final ImageView profilePhoto = findViewById(R.id.MS_profilePhoto);

        userName.setText(user.getName() + " " + user.getMiddlename());
        workType.setText(user.getWork_type());
        rating.setText(user.getRating() + "☆");
        userPhone.setText(user.getPhone());

        Picasso.get()
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .transform(new CircleTransform())
                .into(profilePhoto);

        NavigationView navigationView = findViewById(R.id.MS_LinearLayout);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    //case R.id.MM_config: openFrag(new ConfigFragment()); break;
                    //case R.id.MM_contacts: openFrag(new ScheduleFragment()); break;
                    case R.id.MM_exit: new CommonMethods().LogOut(WorkerActivity.this); startActivity(new Intent(WorkerActivity.this, MainActivity.class)); WorkerActivity.this.finish(); break;
                    case R.id.MM_home: layout.setLayoutParams(new LinearLayout.LayoutParams(0,0)); break;
                    case R.id.MM_apagar:
                        reference.child("Users").child(user.getUuid()).child("active_fastJob").setValue("nulo"); reference.child("Users").child(user.getUuid()).child("waitingFastJob").setValue("nulo"); startActivity(new Intent(WorkerActivity.this, WorkerActivity.class)); WorkerActivity.this.finish();
                    //case R.id.MM_schedule: openFrag(new ScheduleFragment()); break;
                    //case R.id.MM_services: openFrag(new ScheduleFragment()); break;
                }
                return true;
            }

        });

        change = findViewById(R.id.MS_sidebar_change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user.getWork_type().equals("nulo") && !user.getCPF().equals("nulo")) {
                    reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("type").setValue("T").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(WorkerActivity.this, ClientActivity.class));
                            WorkerActivity.this.finish();
                        }
                    });

                } else if (user.getCPF().equals("nulo")) {
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    getSupportFragmentManager().beginTransaction().add(R.id.AW_fragment_content, new FullSignInFragment()).commit();
                    showView(layout);
//                    mapLayout.closeDrawer(sidemenu, true);
                    Toast.makeText(WorkerActivity.this, "Para trabalhar com o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                } else {
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    getSupportFragmentManager().beginTransaction().add(R.id.AW_fragment_content, new WorksFragment()).commit();
                    showView(layout);
//                    mapLayout.closeDrawer(sidemenu, true);
                    Toast.makeText(WorkerActivity.this, "Para trabalhar com o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public LatLng CheckforLocation() {
        if (ActivityCompat.checkSelfPermission(WorkerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WorkerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WorkerActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 7000);
        } else {
            LocationManager lm = (LocationManager) WorkerActivity.this.getSystemService(Context.LOCATION_SERVICE);
            try {
                gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WorkerActivity.this);
                dialog.setMessage("Para continuar, ative os serviços de localização do seu dispositivo");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        WorkerActivity.this.startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Não ativar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    WorkerActivity.this.finish();
                    }
                });
                dialog.show();
                dialog.setCancelable(false);
            }
            if (!net) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(WorkerActivity.this);
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
        return new LatLng(0, 0);
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(WorkerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WorkerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WorkerActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 7001);
        } else {
            if (checkPlayServices()) {
                apiClient = new GoogleApiClient.Builder(WorkerActivity.this)
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

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(WorkerActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, WorkerActivity.this, PLAY_SERVICE_RES_REQUEST).show();
            } else {
                Toast.makeText(WorkerActivity.this, "O seu dispositivo não é compatível", Toast.LENGTH_SHORT).show();
                WorkerActivity.this.finish();
            }
            return false;
        }
        return true;
    }

    private LatLng LocateLocation() {
        if (map != null) {
            final LatLng[] address = {null};
            if (ActivityCompat.checkSelfPermission(WorkerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WorkerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WorkerActivity.this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 7000);
            } else {
                LocationManager lm = (LocationManager) WorkerActivity.this.getSystemService(Context.LOCATION_SERVICE);
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
                    AlertDialog.Builder dialog = new AlertDialog.Builder(WorkerActivity.this);
                    dialog.setMessage("Para continuar, ative os serviços de localização do seu dispositivo");
                    dialog.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            WorkerActivity.this.startActivity(myIntent);
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
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(WorkerActivity.this);
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
                            if (mCurrent != null) mCurrent.remove();
                            mCurrent = map.addMarker(new MarkerOptions()
                                    .position(address[0])
                                    .title(user.getName() + " " + user.getMiddlename())
                                    .icon(bitmapDescriptorFromVector(WorkerActivity.this, R.drawable.ic_you)));

                            //Move Camera
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(address[0], 25.0f));

                            SharedPreferences.setLatLng(WorkerActivity.this, address[0]);
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
        return new LatLng(0, 0);
    }

    private LatLng displayLocation() {

        final LatLng[] address = {new LatLng(0,0)};
        if(map != null) {

            if (net && gps) {

                if (ActivityCompat.checkSelfPermission(WorkerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WorkerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                user = dataSnapshot.getValue(User.class);
                                user.setLat(String.valueOf(lat[0]));
                                user.setLng(String.valueOf(lng[0]));
                                reference.child("Users").child(user.getUuid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (mCurrent != null) mCurrent.remove();
                                        mCurrent = map.addMarker(new MarkerOptions()

                                                .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                                                .title(user.getName() + " " + user.getMiddlename())
                                                .icon(bitmapDescriptorFromVector(WorkerActivity.this, R.drawable.ic_you)));
                                        //Move Camera
                                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat[0], lng[0]), 15.0f));
                                        address[0] = new LatLng(lat[0], lng[0]);
                                    }
                                });


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }else
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-19.9181213, -43.9599618), 1.0f));

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
                        apiClient = new GoogleApiClient.Builder(Objects.requireNonNull(WorkerActivity.this))
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

    private void showView(final View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_right);
        view.startAnimation(animation);
    }

    @Override
    public void WFInteraction() {
        startActivity(new Intent(WorkerActivity.this, ClientActivity.class));
        WorkerActivity.this.finish();
    }

    private int dpToPx(int dp) {
        float density = WorkerActivity.this.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    private void openFrag(Fragment fragment) {
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        getSupportFragmentManager().beginTransaction().add(R.id.AC_fragment_content, fragment).commit();
        showView(layout);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                getApplicationContext(), R.raw.map_style);
        googleMap.setMapStyle(style);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-19.9181213, -43.9599618), 7.0f));
    }

     private void ShowWorkers() {
        reference.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                User worker = dataSnapshot.getValue(User.class);

                assert worker != null;
                if(!worker.getUuid().equals(user.getUuid())) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(worker.getLat()), Double.parseDouble(worker.getLng())))
                            .title(worker.getName())
                            .icon(bitmapDescriptorFromVector(WorkerActivity.this, R.drawable.ic_worker)));
                }else user = worker;

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User worker = dataSnapshot.getValue(User.class);

                assert worker != null;

                if(!worker.getUuid().equals(user.getUuid())) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(worker.getLat()), Double.parseDouble(worker.getLng())))
                            .title(worker.getName())
                            .icon(bitmapDescriptorFromVector(WorkerActivity.this, R.drawable.ic_worker)));
                }else user = worker;
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

    private void HandleNewFastJob() {
        reference.child("FastJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final FastJob fj = dataSnapshot.getValue(FastJob.class);
                boolean canBe = true;
                for(String fj_uid: user.getRefused_fastJobs()){
                    if(fj_uid.equals(fj.getUid())) canBe = false;
                }
                for(String fj_uid: user.getAccepted_fastJobs()){
                    if(fj_uid.equals(fj.getUid())) canBe = false;
                }
                if (canBe &&  fj.getWorker().equals("nulo") && !fj.getClient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && fj.getType().equals(user.getWork_type())) {
                    CreateFastJobViews(fj);
                }
                else if(user.getActive_fastJob().equals(fj.getUid())){
                    reference.child("Users").child(fj.getClient()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            WFJFragmentInteraction(dataSnapshot.getValue(User.class),fj);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    public void WFJFragmentInteraction(User client, final FastJob fastJob) {
        DestroyFastJobViews();
        CreateBottomSheet(client, fastJob);
    }

    @Override
    public void CanceledFastJob(FastJob fastJob) {
        DestroyFastJobViews();
    }



    private void CreateBottomSheet(final User client, final FastJob fastJob){
        final LinearLayout workerLayout = findViewById(R.id.FJ_userLayout);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(workerLayout);

        workerLayout.setVisibility(View.VISIBLE);

        final AutofitTextView name = findViewById(R.id.FJ_user);
        name.setText(client.getName()+" "+ client.getMiddlename());

        final AutofitTextView type = findViewById(R.id.FJ_type);
        type.setText("Precisa de "+user.getWork_type());

        ImageView worker_profile = findViewById(R.id.FJ_profile);
        Picasso.get()
                .load(client.getUri())
                .transform(new CircleTransform())
                .into(worker_profile);

        MaterialButton btn_init = findViewById(R.id.FJ_start_job);
        btn_init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fastJob.setWorkerWaiting(true);
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
                if(dataSnapshot.getValue(FastJob.class).getUid().equals(fastJob.getUid())){
                    final FastJob fastJob = dataSnapshot.getValue(FastJob.class);
                    final ProgressDialog dialog = new ProgressDialog(WorkerActivity.this);
                    if(fastJob.isWorkerWaiting()){
                        if(fastJob.isClientWaiting()) {
                            final BottomSheetDialog bsdialog = new BottomSheetDialog(WorkerActivity.this);
                            bsdialog.setContentView(R.layout.in_progress_bsd);
                            bsdialog.setCanceledOnTouchOutside(false);
                            bsdialog.setCancelable(false);
                            bsdialog.show();
                            Chronometer c = bsdialog.findViewById(R.id.IPB_Chronometer);
                            c.setBase(SystemClock.elapsedRealtime());
                            c.start();

                            MaterialButton finishJob = bsdialog.findViewById(R.id.IPB_FinishJob);
                            finishJob.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reference.child("FastJobs").child(fastJob.getUid()).child("wFinished").setValue(true);
                                    bsdialog.setContentView(R.layout.avaliation_bsd);
                                    TextView text = bsdialog.findViewById(R.id.ABS_text);
                                    text.setText("Avalie o cliente:");
                                    MaterialButton send = bsdialog.findViewById(R.id.ABS_send);
                                    send.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AppCompatRatingBar rating = bsdialog.findViewById(R.id.ABS_rating);
                                            client.setRating(String.valueOf(rating.getRating()* Float.parseFloat(client.getRating())/2));
                                            reference.child("Users").child(client.getUuid()).child("rating").setValue(client.getRating());
                                            bsdialog.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(WorkerActivity.this, "Serviço finalizado com sucesso, obrigado por usar o FreeJob", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                            if(dialog.isShowing())dialog.dismiss();
                            workerLayout.setVisibility(View.GONE);

                        }else{

                            dialog.setCancelable(false);
                            dialog.setTitle("Aguardando Cliente..");
                            dialog.setMessage("Por favor, peça ao cliente para marcar INICIAR TRABALHO em seu dispositivo");
                            dialog.show();
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setCanceledOnTouchOutside(false);
                          /*  reference.child("FastJobs").child(fastJob.getUid()).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if(dataSnapshot.getValue(FastJob.class).isClientWaiting()) dialog.dismiss();
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    if(dataSnapshot.getValue(FastJob.class).isClientWaiting()) dialog.dismiss();
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
                            }); */
                        }
                    }
                    if(fastJob.isClientWaiting()){
                        AlertDialog.Builder adialog = new AlertDialog.Builder(WorkerActivity.this);
                        adialog.setMessage("O Cliente pediu para INICIAR TRABALHO. Está pronto para iniciar agora?");
                        adialog.setTitle("Cliente Esperando");
                        adialog.setCancelable(false);
                        adialog.setNegativeButton("Aguarde um momento", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        adialog.setPositiveButton("OK, Vamos Iniciar!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fastJob.setWorkerWaiting(true);
                                reference.child("FastJobs").child(fastJob.getUid()).setValue(fastJob);
                            }
                        });
                        adialog.create().show();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue(FastJob.class).getUid().equals(fastJob.getUid())){
                    final FastJob fastJob = dataSnapshot.getValue(FastJob.class);

                    if(fastJob.isWorkerWaiting()){
                        if(fastJob.isClientWaiting()) {
                            BottomSheetDialog dialog = new BottomSheetDialog(WorkerActivity.this);
                            dialog.setContentView(R.layout.in_progress_bsd);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            dialog.show();
                        }else{
                           final ProgressDialog dialog = new ProgressDialog(WorkerActivity.this);
                            dialog.setCancelable(false);
                            dialog.setTitle("Aguardando Cliente..");
                            dialog.setMessage("Por favor, peça ao cliente para marcar INICIAR TRABALHO em seu dispositivo");
                            dialog.show();
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setCanceledOnTouchOutside(false);
                        /*    reference.child("FastJobs").child(fastJob.getUid()).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if(dataSnapshot.getValue(FastJob.class).isClientWaiting()) dialog.dismiss();
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    if(dataSnapshot.getValue(FastJob.class).isClientWaiting()) dialog.dismiss();
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
                            }); */
                        }
                    }
                    if(fastJob.isClientWaiting() && !fastJob.isWorkerWaiting()){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(WorkerActivity.this);
                        dialog.setMessage("O Cliente pediu para INICIAR TRABALHO. Está pronto para iniciar agora?");
                        dialog.setTitle("Cliente Esperando");
                        dialog.setCancelable(false);
                        dialog.setNegativeButton("Aguarde um momento", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.setPositiveButton("OK, Vamos Iniciar!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fastJob.setWorkerWaiting(true);
                                reference.child("FastJobs").child(fastJob.getUid()).setValue(fastJob);
                            }
                        });
                        dialog.create().show();
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
    public void FSIFragmentInteraction() {
        reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                if (!user.getWork_type().equals("nulo") && !user.getCPF().equals("nulo")) {

                    Toast.makeText(WorkerActivity.this, "Atualização realizada com sucesso", Toast.LENGTH_LONG).show();

                } else if (user.getCPF().equals("nulo")) {
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    getSupportFragmentManager().beginTransaction().add(R.id.AW_fragment_content, new FullSignInFragment()).commit();
                    showView(layout);
//                    mapLayout.closeDrawer(sidemenu, true);
                    Toast.makeText(WorkerActivity.this, "Para trabalhar com o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                } else {
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    getSupportFragmentManager().beginTransaction().add(R.id.AW_fragment_content, new WorksFragment()).commit();
                    showView(layout);
//                    mapLayout.closeDrawer(sidemenu, true);
                    Toast.makeText(WorkerActivity.this, "Para trabalhar com o FreeJob, insira os dados acima.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}