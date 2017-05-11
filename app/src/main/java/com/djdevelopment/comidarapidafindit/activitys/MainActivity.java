package com.djdevelopment.comidarapidafindit.activitys;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.djdevelopment.comidarapidafindit.R;
import com.djdevelopment.comidarapidafindit.data.Image;
import com.djdevelopment.comidarapidafindit.data.Ratings;
import com.djdevelopment.comidarapidafindit.data.Restaurants;
import com.djdevelopment.comidarapidafindit.tools.DownloadImageTask;
import com.djdevelopment.comidarapidafindit.tools.UtilUI;
import com.djdevelopment.comidarapidafindit.tools.ValuedPlacesAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mzelzoghbi.zgallery.ZGallery;
import com.mzelzoghbi.zgallery.ZGrid;
import com.stfalcon.multiimageview.MultiImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.ButterKnife;

import com.djdevelopment.comidarapidafindit.tools.RatingAdapter;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    //Permisions
    private static final int TAG_CODE_PERMISSION_LOCATION = 0;
    private static final int RC_SIGN_IN = 5;
    private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 10;

    //Map configurantion
    private GoogleMap mMap;

    //Google api client
    private GoogleApiClient mGoogleApiClient;

    //Firebase configuration
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("restaurants-suggest");
    Restaurants restaurant;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    //View render
    @BindView(R.id.RelativeLayoutBottomSheet) RelativeLayout relativeLayoutBottomSheet;
    @BindView(R.id.txtRating) TextView txtRating;
    @BindView(R.id.txtCreditCard) TextView txtCreditCards;
    @BindView(R.id.lblTelephone) TextView lblTelephone;
    @BindView(R.id.txtBottomSheet) TextView txtBottomSheet;
    @BindView(R.id.bottomSheet) View bottomSheet;
    @BindView(R.id.ratingBarBottom) RatingBar ratingBarBottom;
    @BindView(R.id.floatingActionButton) FloatingActionButton floatingActionButton;
    @BindView(R.id.imageViewDescription) MultiImageView mThumbnailPreview;
    @BindView(R.id.lblDelivery) TextView lblDelivery;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.imageViewBottomSheet) ImageView imageViewBottomSheet;
    @BindView(R.id.textViewImageBottomSheet) TextView textViewImageBottomSheet;
    @BindView(R.id.relativeLayoutImageBottomSheet) RelativeLayout relativeLayoutImageBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    RatingBar ratingBarAddComments;
    NavigationView navigationView;
    EditText txtComments;
    ActionBarDrawerToggle toggle;

    //Location configuration
    LocationManager mLocationManager;

    //Map options
    int markerSelected;
    PolylineOptions polylineOptions;
    LatLng locationUser;
    Polyline polyline;

    //Usuario
    TextView idUsuario;
    TextView emailUsuario;
    ImageView imageViewUsuario;

    //Utilidades varias
    float rating = 0;
    ArrayList<String> ratingList = new ArrayList<>();
    ArrayList<Ratings> ratingsList = new ArrayList<>();
    ArrayList<Restaurants> restaurants = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();
    ArrayList<String> photosUrl = new ArrayList<>();
    ArrayList<String> commentsUsersImages = new ArrayList<>();
    UtilUI utilUI = new UtilUI();
    LatLng positionMarker;

    @SuppressWarnings("ALL")
    @OnClick(R.id.btnRating)
    void submitButtonRating() {
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

                            myRef.child(keys.get(markerSelected)).child("rating").push().setValue("{ \"rating\" : \""+rating+"\", \"comment\" : \""+str+"\", \"userName\" : \""+user.getDisplayName()+"\", \"photoURL\" : \""+user.getPhotoUrl()+"\" , \"fecha\" : \""+new Date().getTime()+"\"}");
                        }
                        catch (NullPointerException ex){
                            ex.printStackTrace();
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                })
                .show();

    }

    @OnClick(R.id.btnMostrarMenu)
    void setBtnMostarMenu(){
        ArrayList<String> namePriceList = new ArrayList<>();
        try {
            for (String restMenu : restaurants.get(markerSelected).getMenu()) {
                try {
                    JSONObject jObj = new JSONObject(restMenu);
                    namePriceList.add(jObj.getString("name") + "\nRD$ " + jObj.getString("price"));
                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        if(namePriceList.size() == 0){
            new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.menu_not_available)
                    .show();
        }
        else {
            new MaterialDialog.Builder(MainActivity.this)
                    .title("Menu")
                    .items(namePriceList)
                    .show();
        }
    }

    @OnClick(R.id.btnMostrarRating)
    void setBtnMostrarRating(){
        try {

            MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                    .title("Lista de ratings")
                    .customView(R.layout.layout_show_rating, true)
                    .show();

            if(ratingsList.size() != 0){
                ratingsList.clear();
            }
            for (String restRating : restaurants.get(markerSelected).getRating().values()) {
                try {

                    JSONObject jObj = new JSONObject(restRating);
                    Ratings ratings = new Ratings();
                    ratings.setRating(Float.parseFloat(jObj.getString("rating")));
                    ratings.setComment(jObj.getString("comment"));
                    ratings.setUserName(jObj.getString("userName"));
                    ratings.setPhotoURL(jObj.getString("photoURL"));
                    ratings.setFecha(jObj.getString("fecha"));
                    ratingsList.add(ratings);

                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                }
            }

            Collections.sort(ratingsList, new Comparator<Ratings>() {
                @Override
                public int compare(Ratings ratings1, Ratings ratings2) {
                    return ((new Date(Long.parseLong(ratings1.getFecha())).getTime() >= new Date(Long.parseLong(ratings2.getFecha())).getTime()) ? -1 : 0);
                }
            });
            RecyclerView rvContacts = (RecyclerView) dialog.getView().findViewById(R.id.rvRating);

            RatingAdapter adapter = new RatingAdapter(MainActivity.this, ratingsList, true);
            rvContacts.setAdapter(adapter);
            rvContacts.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @OnClick(R.id.btnShowAllRatings)
    void setBtnShowAllRatings(){
        try {

            MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                    .title("Lista de ratings")
                    .customView(R.layout.layout_show_rating, true)
                    .show();

            if(ratingsList.size() != 0){
                ratingsList.clear();
            }
            for (String restRating : restaurants.get(markerSelected).getRating().values()) {
                try {

                    JSONObject jObj = new JSONObject(restRating);
                    Ratings ratings = new Ratings();
                    ratings.setRating(Float.parseFloat(jObj.getString("rating")));
                    ratings.setComment(jObj.getString("comment"));
                    ratings.setUserName(jObj.getString("userName"));
                    ratings.setPhotoURL(jObj.getString("photoURL"));
                    ratings.setFecha(jObj.getString("fecha"));
                    ratingsList.add(ratings);

                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                }
            }

            Collections.sort(ratingsList, new Comparator<Ratings>() {
                @Override
                public int compare(Ratings ratings1, Ratings ratings2) {
                    return ((new Date(Long.parseLong(ratings1.getFecha())).getTime() >= new Date(Long.parseLong(ratings2.getFecha())).getTime()) ? -1 : 0);
                }
            });

            RecyclerView rvContacts = (RecyclerView) dialog.getView().findViewById(R.id.rvRating);

            RatingAdapter adapter = new RatingAdapter(MainActivity.this, ratingsList, true);
            rvContacts.setAdapter(adapter);
            rvContacts.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @OnClick(R.id.floatingActionButton)
    void setFloatingActionButton(){
        polyline =  mMap.addPolyline(polylineOptions);
    }

    @OnClick(R.id.btnLlamarSitio)
    void setBtnLlamarSitio(){
        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" +restaurants.get(markerSelected).getTelephones()));
        startActivity(intent);
    }

    @OnClick(R.id.bottomSheet)
    void setBottomSheetBehavior(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnClick(R.id.imageViewBottomSheet)
    void setImageViewBottomSheet(){
        ZGallery.with(this, photosUrl)
                .setToolbarColorResId(R.color.colorPrimary) // toolbar color
                .show();
    }

    @OnClick(R.id.imageViewDescription)
    void setImageViewDescription(){
        ZGrid.with(this, photosUrl)
                .setToolbarColorResId(R.color.colorPrimary) // toolbar color
                .setTitle(getString(R.string.app_name)) // toolbar title
                .setSpanCount(3) // colums count
                .setGridImgPlaceHolder(R.color.colorPrimary) // color placeholder for the grid image until it loads
                .show();
    }

    @OnClick(R.id.addNewImage)
    void addNewImageToFirebase(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @OnClick(R.id.btnShareRest)
    void shareRest(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"Visita \""+restaurants.get(markerSelected).getRestName()+"\". Ubicado en:"+ "http://maps.google.com/maps?q=loc:"+String.format("%f,%f", positionMarker.latitude, positionMarker.longitude ) );
        startActivity(Intent.createChooser(intent, "Compartir sitio con:"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,  R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));

        //Put BottomSheet Hidden when the app begin
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        floatingActionButton.setVisibility(View.INVISIBLE);

        bottomSheetBehavior();

        retrieveDataFromFirebase();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_api_user_key))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mAuth = FirebaseAuth.getInstance();


        final View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        final ImageButton btnUsuarioHeader =  (ImageButton) navHeaderView.findViewById(R.id.imageButtonNavHeader);

        btnUsuarioHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnUsuarioHeader.getTag() == null || (Integer)btnUsuarioHeader.getTag() == R.drawable.ic_down_arrow) {
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.activity_main_user);
                    btnUsuarioHeader.setImageResource(R.drawable.ic_up_arrow);
                    btnUsuarioHeader.setTag(R.drawable.ic_up_arrow);
                }
                else{
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.activity_main_drawer);
                    btnUsuarioHeader.setImageResource(R.drawable.ic_down_arrow);
                    btnUsuarioHeader.setTag(R.drawable.ic_down_arrow);
                }
            }
        });
        idUsuario = (TextView) navHeaderView.findViewById(R.id.idUsuario);
        emailUsuario = (TextView) navHeaderView.findViewById(R.id.Email);
        imageViewUsuario = (ImageView) navHeaderView.findViewById(R.id.imageViewNavHeader);

        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                updateUIFromUser();
            } else {
                try {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
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

            Location location = getLastKnownLocation();
            locationUser = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationUser,15));
            mMap.setMyLocationEnabled(true);
        }
        else {

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    TAG_CODE_PERMISSION_LOCATION);
        }
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
            FirebaseAuth.getInstance().signOut();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }

        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            Bitmap urlImage = (Bitmap) data.getExtras().get("data");
            final Uri tempUri = getImageUri(getApplicationContext(), urlImage);

            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://comidarapida-cae88.appspot.com/");
            final String key = keys.get(markerSelected);
            StorageReference spaceRef = storageRef.child("images/"+key +"/"+tempUri.getLastPathSegment());

            UploadTask uploadTask = spaceRef.putFile(tempUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    myRef.child(key).child("urlImage").push().setValue("{\"url\": \"" + taskSnapshot.getDownloadUrl().toString() + "\"}");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
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

        } else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(this, SuggestActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            final MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                    .title("Rating")
                    .customView(R.layout.layout_show_valued_places,false)
                    .positiveText("Aceptar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            try {

                            }
                            catch (NullPointerException ex){
                                ex.printStackTrace();
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    })
                    .show();
            RecyclerView rvValuedPlaces = (RecyclerView) dialog.getView().findViewById(R.id.rvValuedPlaces);

            Collections.sort(this.restaurants, new Comparator<Restaurants>() {
                @Override
                public int compare(Restaurants restaurants1, Restaurants restaurants2) {
                    return (getRatingList(restaurants1) >= getRatingList(restaurants2)) ? -1 : 0;
                }
            });
            ValuedPlacesAdapter adapter = new ValuedPlacesAdapter(restaurants, new ValuedPlacesAdapter.OnItemClickLister() {
                @Override
                public void OnItemClick(Restaurants name, int position) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertStringToLatLng(name.getLatLong()),20));
                    retrieveDataFromFirebase();
                    dialog.dismiss();
                }
            });
            rvValuedPlaces.setAdapter(adapter);
            rvValuedPlaces.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app));
            startActivity(Intent.createChooser(intent, "Share with"));

        } else if (id == R.id.nav_send) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Share with");
            intent.setPackage("com.whatsapp");
            startActivity(intent);

        } else if (id == R.id.change_user){
            mGoogleApiClient.clearDefaultAccountAndReconnect();

            try {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
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

                int index = 0;
                restaurants.clear();
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
                                @SuppressLint("SetTextI18n")
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    //TODO AGREGAR LOS DEMAS CAMPOS DEL RESTAURANTE
                                    markerSelected = Integer.parseInt(marker.getSnippet());
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    txtBottomSheet.setText(restaurants.get(markerSelected).getRestName());
                                    getRatingList(markerSelected);
                                    if(restaurants.get(markerSelected).getDelivery()){
                                        lblDelivery.setText(R.string.available_delivery);
                                    }
                                    else {
                                        lblDelivery.setText(R.string.not_available_delivery);
                                    }

                                    txtRating.setText(String.valueOf(rating));
                                    if(restaurants.get(markerSelected).getCreditCards().equals("")){
                                        txtCreditCards.setText("No se acepta tarjetas de credito");
                                    }
                                    else {
                                        txtCreditCards.setText(restaurants.get(markerSelected).getCreditCards());
                                    }
                                    ratingBarBottom.setRating(rating);
                                    if(restaurants.get(markerSelected).getTelephones().equals("")){
                                        lblTelephone.setText("No existen telefonos registrados");
                                    }
                                    else {
                                        lblTelephone.setText(restaurants.get(markerSelected).getTelephones());
                                    }
                                    loadRatingListToUI();

                                    if(restaurants.get(markerSelected).getUrlImage() == null){
                                        mThumbnailPreview.clear();
                                        imageViewBottomSheet.setImageBitmap(null);
                                        textViewImageBottomSheet.setText(R.string.image_not_storage);
                                    }
                                    else {
                                        try {
                                            mThumbnailPreview.clear();
                                            photosUrl.clear();
                                            textViewImageBottomSheet.setText(restaurants.get(markerSelected).getUrlImage().size()+ " foto/s");
                                            loadImageUI();
                                        } catch (Exception ex) {
                                            mThumbnailPreview.setImageBitmap(null);
                                            ex.printStackTrace();
                                        }
                                    }
                                    positionMarker = marker.getPosition();
                                    /*
                                    Location locationA = new Location("point A");

                                    locationA.setLatitude(locationUser.latitude);
                                    locationA.setLongitude(locationUser.longitude);

                                    Location locationB = new Location("point B");

                                    locationB.setLatitude(positionMarker.latitude);
                                    locationB.setLongitude(positionMarker.longitude);

                                    float distance = locationA.distanceTo(locationB);
                                    System.out.print(distance);
                                    */
                                    try {
                                        GoogleDirection.withServerKey(getString(R.string.google_direction_key))
                                                .from(locationUser)
                                                .to(positionMarker)
                                                .transportMode(TransportMode.DRIVING)
                                                .execute(new DirectionCallback() {
                                                    @Override
                                                    public void onDirectionSuccess(Direction direction, String rawBody) {
                                                        if (direction.isOK()) {
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

                                    }
                                    catch (Exception ex){ex.printStackTrace();}
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

    @SuppressWarnings("ALL")
    private void bottomSheetBehavior(){
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, int newState) {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                switch (newState){
                    case BottomSheetBehavior.STATE_DRAGGING:
                        try {
                            bottomSheet.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            relativeLayoutBottomSheet.setBackgroundColor(getResources().getColor(R.color.colorBlanco));
                            mThumbnailPreview.setVisibility(View.VISIBLE);
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setSubtitle(null);
                            getSupportActionBar().setTitle(R.string.app_name);
                        }
                        catch (NullPointerException ex){
                            ex.printStackTrace();
                        }
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        mThumbnailPreview.setVisibility(View.INVISIBLE);
                        bottomSheet.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        relativeLayoutBottomSheet.setBackgroundColor(getResources().getColor(R.color.colorBlanco));
                        getSupportActionBar().setTitle(restaurants.get(markerSelected).getRestName());
                        getSupportActionBar().setSubtitle(String.valueOf(rating));
                        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white));
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                getSupportActionBar().setSubtitle(null);
                            }
                        });
                        //TODO CHANGE FOR THE RESTAURANT TYPE
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        animateColor(Color.rgb(63,81,181), Color.rgb(255,255,255));
                        relativeLayoutBottomSheet.setBackgroundColor(Color.rgb(255,255,255));
                        toggle.syncState();
                        getSupportActionBar().setTitle(R.string.app_name);
                        getSupportActionBar().setSubtitle(null);
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drawer.openDrawer(Gravity.START);
                            }
                        });
                        if(restaurants.get(markerSelected).getUrlImage() != null){
                            mThumbnailPreview.setVisibility(View.VISIBLE);
                        }
                        floatingActionButton.setVisibility(FloatingActionButton.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        getSupportActionBar().setSubtitle(null);
                        toggle.syncState();
                        getSupportActionBar().setTitle(R.string.app_name);toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drawer.openDrawer(Gravity.START);
                            }
                        });

                        mThumbnailPreview.setVisibility(View.INVISIBLE);
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

    private void animateColor(int colorFrom, int colorTo){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(200); // milliseconds
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

                        if (task.isSuccessful()) {
                            updateUIFromUser();
                        }
                        else {
                            Log.w("Sig-in", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getRatingList(int markerSelected){
        try {
            rating = 0;
            if(ratingList.size() != 0){
                ratingList.clear();
            }
            for (String restRating : restaurants.get(markerSelected).getRating().values()) {
                try {
                    JSONObject jObj = new JSONObject(restRating);
                    ratingList.add(jObj.getString("rating"));
                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            int i;
            for(i = 0; i<ratingList.size();i++){
                rating = Float.parseFloat(ratingList.get(i)) + rating;

            }
            rating = rating / i;

            rating = Math.round(rating * 100.0f) / 100.0f;

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void loadRatingListToUI(){
        try {

            if(ratingsList.size() != 0){
                ratingsList.clear();
            }
            for (String restRating : restaurants.get(markerSelected).getRating().values()) {
                try {

                    JSONObject jObj = new JSONObject(restRating);
                    Ratings ratings = new Ratings();
                    ratings.setRating(Float.parseFloat(jObj.getString("rating")));
                    ratings.setComment(jObj.getString("comment"));
                    ratings.setUserName(jObj.getString("userName"));
                    ratings.setPhotoURL(jObj.getString("photoURL"));
                    ratings.setFecha(jObj.getString("fecha"));
                    ratingsList.add(ratings);

                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                }
            }

            Collections.sort(ratingsList, new Comparator<Ratings>() {
                @Override
                public int compare(Ratings ratings1, Ratings ratings2) {
                    return ((new Date(Long.parseLong(ratings1.getFecha())).getTime() >= new Date(Long.parseLong(ratings2.getFecha())).getTime()) ? -1 : 0);
                }
            });
            RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvRating);

            RatingAdapter adapter = new RatingAdapter(MainActivity.this, ratingsList, false);
            rvContacts.setAdapter(adapter);
            rvContacts.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @SuppressWarnings("ALL")
    private void updateUIFromUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();

        idUsuario.setText(user.getDisplayName());
        emailUsuario.setText(user.getEmail());
        imageViewUsuario.setImageBitmap(utilUI.getBitmapFromURL(user.getPhotoUrl().toString()));
    }

    private void loadImageUI(){
        try {
            for (String key : restaurants.get(markerSelected).getUrlImage().keySet()) {
                try {
                    JSONObject jObj = new JSONObject(restaurants.get(markerSelected).getUrlImage().get(key));
                    photosUrl.add(jObj.getString("url"));
                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                }
            }
            new DownloadImageTask(mThumbnailPreview).execute(photosUrl.get(0));
            new DownloadImageTask(imageViewBottomSheet).execute(photosUrl.get(0));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public LatLng convertStringToLatLng(String latLong){
        String[] words = latLong.split(",");
        String latitud = words[0].substring(10);
        String longitud = words[1].replace(")", "");
        LatLng latLng = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
        return latLng;
    }

    public float getRatingList(Restaurants restaurants){
        try {
            if (ratingList.size() != 0) {
                ratingList.clear();
            }
            for (String restRating : restaurants.getRating().values()) {
                try {
                    JSONObject jObj = new JSONObject(restRating);
                    ratingList.add(jObj.getString("rating"));
                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            int i;
            for (i = 0; i < ratingList.size(); i++) {
                rating = Float.parseFloat(ratingList.get(i)) + rating;

            }
            rating = rating / i;

            return rating = Math.round(rating * 100.0f) / 100.0f;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return 0;
        }
    }

}