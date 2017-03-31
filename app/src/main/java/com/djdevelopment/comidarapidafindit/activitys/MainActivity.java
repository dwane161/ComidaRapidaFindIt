package com.djdevelopment.comidarapidafindit.activitys;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djdevelopment.comidarapidafindit.R;
import com.djdevelopment.comidarapidafindit.data.Restaurants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    //Permisions
    private static final int TAG_CODE_PERMISSION_LOCATION = 0;

    //Map configurantion
    private GoogleMap mMap;

    //Firebase configuration
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("restaurants-suggest");
    Restaurants restaurant;

    //BottonSheet view
    private BottomSheetBehavior bottomSheetBehavior;
    View bottomSheet;
    TextView txtBottomSheet, txtRating, txtCreditCards;
    RatingBar ratingBarBottom;
    LinearLayout linearLayoutPrincipal;
    RelativeLayout relativeLayoutBottomSheet;

    //Location configuration
    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,  R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //BottomSheet initialization
        bottomSheet = findViewById(R.id.bottomSheet);
        relativeLayoutBottomSheet = (RelativeLayout) findViewById(R.id.RelativeLayoutBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        txtBottomSheet = (TextView) findViewById(R.id.txtBottomSheet);
        txtRating = (TextView) findViewById(R.id.txtRating);
        ratingBarBottom = (RatingBar) findViewById(R.id.ratingBarBottom);
        txtCreditCards = (TextView) findViewById(R.id.txtCreditCard);
        linearLayoutPrincipal = (LinearLayout) findViewById(R.id.linearLayoutPrincipal);


        //Put BottomSheet Hidden when the app begin
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING){
                    bottomSheet.setBackgroundColor(Color.rgb(50,146,248));
                    relativeLayoutBottomSheet.setBackgroundColor(Color.rgb(255,255,255));
                }
                else if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheet.setBackgroundColor(Color.rgb(50,146,248));
                    relativeLayoutBottomSheet.setBackgroundColor(Color.rgb(255,255,255));

                }
                else if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    final float[] from = new float[3],
                            to =   new float[3];

                    Color.colorToHSV(Color.parseColor("#3292F8"), from);   // from white
                    Color.colorToHSV(Color.parseColor("#FFFFFF"), to);     // to red

                    ValueAnimator anim = ValueAnimator.ofFloat(0, 3);   // animate from 0 to 1
                    anim.setDuration(400);                              // for 300 ms

                    final float[] hsv  = new float[3];                  // transition color
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
                        @Override public void onAnimationUpdate(ValueAnimator animation) {
                            // Transition along each axis of HSV (hue, saturation, value)
                            hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                            hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                            hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                            bottomSheet.setBackgroundColor(Color.HSVToColor(hsv));
                        }
                    });

                    anim.start();
                    relativeLayoutBottomSheet.setBackgroundColor(Color.rgb(255,255,255));
                }
            }
            /*
                new Thread() {
                        int color = 0;
                        public void run() {
                            for (color = 0; color <= 255; color++) {
                                try {
                                    sleep(1);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            relativeLayoutBottomSheet.setBackgroundColor(Color.argb(255,
                                                    color, color, color));
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
             */
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //Firebase data retrieve
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot dataSnapshotchild : dataSnapshot.getChildren()){
                    try {

                        if(dataSnapshotchild.child("validated").getValue().toString().equals("true")) {

                            restaurant = dataSnapshotchild.getValue(Restaurants.class);

                            String latLong = dataSnapshotchild.child("latLong").getValue().toString();
                            String[] words = latLong.split(",");
                            String latitud = words[0].substring(10);
                            String longitud = words[1].replace(")", "");
                            LatLng latLng = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(dataSnapshotchild.child("restName").getValue().toString())
                                    .snippet(dataSnapshotchild.child("rating").getValue().toString()));

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    //TODO AGREGAR LOS DEMAS CAMPOS DEL RESTAURANTE
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    txtBottomSheet.setText(marker.getTitle());
                                    txtRating.setText(marker.getSnippet());
                                    txtCreditCards.setText(restaurant.getCreditCards());
                                    ratingBarBottom.setNumStars(Integer.parseInt(marker.getSnippet()));
                                    return true;
                                }
                            });
                        }
                    }
                    catch(Exception ex){
                        ex.getMessage();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Initialization of the map
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        //Check permission before getLocation of user
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        else {

            ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                TAG_CODE_PERMISSION_LOCATION);
        }

        //Location of the user
        Location location = getLastKnownLocation();
        LatLng locationUser = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationUser,15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case TAG_CODE_PERMISSION_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                    PackageManager.PERMISSION_GRANTED) {
                        //The permission of location was granted
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(this, SuggestActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

        //remove location callback:
        mLocationManager.removeUpdates(this);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    private Location getLastKnownLocation() {
        //Method for get the last know location of user
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
