package com.djdevelopment.comidarapidafindit.activitys;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.djdevelopment.comidarapidafindit.R;
import com.djdevelopment.comidarapidafindit.data.Restaurants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.multiimageview.MultiImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import br.com.jeancsanchez.photoviewslider.PhotosViewSlider;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    //Permisions
    private static final int TAG_CODE_PERMISSION_LOCATION = 0;
    private static final int RC_SIGN_IN = 5;

    //Map configurantion
    private GoogleMap mMap;


    private GoogleApiClient mGoogleApiClient;

    //Firebase configuration
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("restaurants-suggest");
    Restaurants restaurant;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //BottonSheet view

    /*

        bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        txtBottomSheet = (TextView) findViewById(R.id.txtBottomSheet);
        txtRating = (TextView) findViewById(R.id.txtRating);
        ratingBarBottom = (RatingBar) findViewById(R.id.ratingBarBottom);
        txtCreditCards = (TextView) findViewById(R.id.txtCreditCard);
        linearLayoutPrincipal = (LinearLayout) findViewById(R.id.linearLayoutPrincipal);
        lblTelephone = (TextView) findViewById(R.id.lblTelephone);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        btnMostarMenu =  (Button) findViewById(R.id.btnMostrarMenu);
        mThumbnailPreview = (MultiImageView) findViewById(R.id.imageViewDescription);
        btnRating = (Button) findViewById(R.id.btnRating);
     */
    @BindView(R.id.RelativeLayoutBottomSheet) RelativeLayout relativeLayoutBottomSheet;
    @BindView(R.id.txtRating) TextView txtRating;
    @BindView(R.id.txtCreditCard) TextView txtCreditCards;
    @BindView(R.id.lblTelephone) TextView lblTelephone;
    @BindView(R.id.txtBottomSheet) TextView txtBottomSheet;
    @BindView(R.id.bottomSheet) View bottomSheet;
    @BindView(R.id.ratingBarBottom) RatingBar ratingBarBottom;
    @BindView(R.id.floatingActionButton) FloatingActionButton floatingActionButton;
    @BindView(R.id.btnMostrarMenu) Button btnMostarMenu;
    @BindView(R.id.btnIniciarSesion) Button btnIniciarSesion;
    @BindView(R.id.btnRating) Button btnRating;
    @BindView(R.id.bottomSheet) BottomSheetBehavior bottomSheetBehavior;
    @BindView(R.id.imageViewDescription) MultiImageView mThumbnailPreview;
    PhotosViewSlider photoViewSlider;

    RatingBar ratingBarAddComments;
    EditText txtComments;

    //Location configuration
    LocationManager mLocationManager;

    //Map options
    int markerSelected;
    PolylineOptions polylineOptions;
    LatLng locationUser;
    Polyline polyline;

    ArrayList<Restaurants> restaurants = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();
    ArrayList<String> photosUrl = new ArrayList<>();
    ArrayList<String> commentsUsersImages = new ArrayList<>();

    NavigationView navigationView;

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,  R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        txtBottomSheet = (TextView) findViewById(R.id.txtBottomSheet);
        txtRating = (TextView) findViewById(R.id.txtRating);
        ratingBarBottom = (RatingBar) findViewById(R.id.ratingBarBottom);
        txtCreditCards = (TextView) findViewById(R.id.txtCreditCard);
        lblTelephone = (TextView) findViewById(R.id.lblTelephone);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        btnMostarMenu =  (Button) findViewById(R.id.btnMostrarMenu);
        btnIniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
        mThumbnailPreview = (MultiImageView) findViewById(R.id.imageViewDescription);
        btnRating = (Button) findViewById(R.id.btnRating);
        relativeLayoutBottomSheet = (RelativeLayout) findViewById(R.id.RelativeLayoutBottomSheet);
        photoViewSlider = (PhotosViewSlider) findViewById(R.id.photosViewSlider);

        floatingActionButton.setVisibility(FloatingActionButton.INVISIBLE);

        //Put BottomSheet Hidden when the app begin
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior();

        retrieveDataFromFirebase();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestId()
                .requestIdToken("224098914874-v53bv24ili6ik7i8u6bof8bkqlqal6ar.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                System.out.println(connectionResult.getErrorMessage());
                            }
                        })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        final TextView idUsuario = (TextView) navHeaderView.findViewById(R.id.idUsuario);
        final TextView emailUsuario = (TextView) navHeaderView.findViewById(R.id.Email);
        final ImageView imageViewUsuario = (ImageView) navHeaderView.findViewById(R.id.imageViewNavHeader);
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String name = user.getDisplayName();
                idUsuario.setText(name);
                String email = user.getEmail();
                emailUsuario.setText(email);
                Uri photoUrl = user.getPhotoUrl();
                imageViewUsuario.setImageBitmap(getBitmapFromURL(photoUrl.toString()));
            } else {
                // No user is signed in
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                polyline =  mMap.addPolyline(polylineOptions);
            }
        });

        Button btnCerrarSesion = (Button) navHeaderView.findViewById(R.id.btnIniciarSesion);

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                idUsuario.setText("Nombre Usuario");
                emailUsuario.setText("Email");
                imageViewUsuario.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.burger));
            }
        });

        btnMostarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> namePriceList = new ArrayList<>();

                for(String restMenu : restaurants.get(markerSelected).getMenu()){
                    try {
                        JSONObject jObj = new JSONObject(restMenu);
                        namePriceList.add(jObj.getString("name") +"\nRD$ "+ jObj.getString("price"));
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                }
                new MaterialDialog.Builder(MainActivity.this)
                        .title("Menu")
                        .items(namePriceList)
                        .show();
            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(MainActivity.this)
                        .title("Rating")
                        .customView(R.layout.item_layout_rating,true)
                        .positiveText("Aceptar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                try {
                                    View v = dialog.getCustomView();
                                    txtComments = (EditText) v.findViewById(R.id.txtComments);
                                    ratingBarAddComments = (RatingBar) v.findViewById(R.id.ratingBarAddComments);
                                    float rating = ratingBarAddComments.getRating();
                                    String str = txtComments.getText().toString();

                                    //TODO REMPLAZAR rating2 CON rating
                                    myRef.child(keys.get(markerSelected)).child("rating2").push().setValue("{ \"rating\" : \""+rating+"\", \"comment\" : \""+str+"\"}");
                                }
                                catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        })
                        .show();
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
                try {
                    polyline.remove();
                }
                catch (Exception ex){
                    //TODO ELIIMINAR ESTE CATCH
                    ex.printStackTrace();
                }
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
        locationUser = new LatLng(location.getLatitude(),location.getLongitude());
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

                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(this, SuggestActivity.class);
            startActivity(intent);



        } else if (id == R.id.nav_slideshow) {
            try {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
            catch (Exception ex){
                ex.printStackTrace();
            }

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

    private void retrieveDataFromFirebase(){
        //Firebase data retrieve
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                restaurants.clear();
                index = 0;
                for(final DataSnapshot dataSnapshotchild : dataSnapshot.getChildren()){
                    try {

                        if(dataSnapshotchild.child("validated").getValue().toString().equals("true")) {

                            restaurant = dataSnapshotchild.getValue(Restaurants.class);
                            keys.add(dataSnapshotchild.getKey());
                            restaurants.add(restaurant);
                            String latLong = dataSnapshotchild.child("latLong").getValue().toString();
                            String[] words = latLong.split(",");
                            String latitud = words[0].substring(10);
                            String longitud = words[1].replace(")", "");
                            LatLng latLng = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));

                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(dataSnapshotchild.child("restName").getValue().toString())
                                    .snippet(String.valueOf(index))
                                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(MainActivity.this,R.drawable.ic_placeholder))));

                            index++;
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    //TODO AGREGAR LOS DEMAS CAMPOS DEL RESTAURANTE
                                    markerSelected = Integer.parseInt(marker.getSnippet());

                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    txtBottomSheet.setText(restaurants.get(markerSelected).getRestName());
                                    txtRating.setText(String.valueOf(restaurants.get(markerSelected).getRating()));
                                    txtCreditCards.setText(restaurants.get(markerSelected).getCreditCards());
                                    ratingBarBottom.setRating((float)restaurants.get(markerSelected).getRating());
                                    lblTelephone.setText(restaurants.get(markerSelected).getTelephones());

                                    photoViewSlider.setVisibility(View.INVISIBLE);

                                    try {
                                        mThumbnailPreview.clear();
                                        photosUrl.clear();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        for (String key : restaurants.get(markerSelected).getUrlImage().keySet()) {
                                                            try {
                                                                JSONObject jObj = new JSONObject(restaurants.get(markerSelected).getUrlImage().get(key));
                                                                commentsUsersImages.add(jObj.getString("name"));
                                                                photosUrl.add(jObj.getString("url"));
                                                            } catch (JSONException e) {
                                                                Log.e("MYAPP", "unexpected JSON exception", e);
                                                            }
                                                        }
                                                        photoViewSlider.setVisibility(View.VISIBLE);
                                                        photoViewSlider.initializePhotosUrls(photosUrl);
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                    catch (Exception ex){
                                        mThumbnailPreview.setImageBitmap(null);
                                        ex.printStackTrace();
                                    }

                                    LatLng positionMarker = marker.getPosition();

                                    GoogleDirection.withServerKey("AIzaSyAseOR6uttx9_AMO89pcG2WzaT-Bl6zMWA")
                                            .from(locationUser)
                                            .to(positionMarker)
                                            .transportMode(TransportMode.DRIVING)
                                            .execute(new DirectionCallback() {
                                                @Override
                                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                                    if(direction.isOK()){
                                                        Route route = direction.getRouteList().get(0);
                                                        Leg leg = route.getLegList().get(0);

                                                        ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                                        polylineOptions = DirectionConverter.createPolyline(MainActivity.this, directionPositionList, 5, Color.RED);
                                                    }
                                                }

                                                @Override
                                                public void onDirectionFailure(Throwable t) {

                                                }
                                            });


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

    private void bottomSheetBehavior(){
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_DRAGGING:
                        bottomSheet.setBackgroundColor(getResources().getColor(R.color.colorAzul));
                        relativeLayoutBottomSheet.setBackgroundColor(getResources().getColor(R.color.colorBlanco));
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheet.setBackgroundColor(getResources().getColor(R.color.colorAzul));
                        relativeLayoutBottomSheet.setBackgroundColor(getResources().getColor(R.color.colorBlanco));
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        animateColor(Color.rgb(50,146,248), Color.rgb(255,255,255), 200);
                        relativeLayoutBottomSheet.setBackgroundColor(Color.rgb(255,255,255));
                        mThumbnailPreview.setVisibility(View.VISIBLE);
                        floatingActionButton.setVisibility(FloatingActionButton.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mThumbnailPreview.setVisibility(View.INVISIBLE);
                        photoViewSlider.setVisibility(View.INVISIBLE);
                        floatingActionButton.setVisibility(FloatingActionButton.INVISIBLE);
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
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

    private void animateColor(int colorFrom, int colorTo, int duration){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bottomSheet.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();


    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Sig-in", "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("Sig-in", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}