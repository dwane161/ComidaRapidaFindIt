package com.djdevelopment.comidarapidafindit.activitys

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatDrawableManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.djdevelopment.comidarapidafindit.R
import com.djdevelopment.comidarapidafindit.data.Ratings
import com.djdevelopment.comidarapidafindit.data.Restaurants
import com.djdevelopment.comidarapidafindit.tools.DownloadImageTask
import com.djdevelopment.comidarapidafindit.tools.RatingAdapter
import com.djdevelopment.comidarapidafindit.tools.UtilUI.Companion.getBitmapFromURL
import com.djdevelopment.comidarapidafindit.tools.ValuedPlacesAdapter
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.mzelzoghbi.zgallery.ZGallery
import com.mzelzoghbi.zgallery.ZGrid
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 10
    //Map configurantion
    lateinit var mMap: GoogleMap

    //Google api client
    lateinit var mGoogleApiClient: GoogleApiClient

    //Firebase configuration
    internal var database = FirebaseDatabase.getInstance()
    internal var myRef = database.getReference("restaurants-suggest")
    internal var restaurant: Restaurants = Restaurants()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    internal var user: FirebaseUser? = null

    //View render
    lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    lateinit var ratingBarAddComments: RatingBar
    lateinit var navigationView: NavigationView
    lateinit var txtComments: EditText
    lateinit var toggle: ActionBarDrawerToggle

    //Location configuration
    lateinit var mLocationManager: LocationManager

    //Map options
    internal var markerSelected: Int = 0
    lateinit var polylineOptions: PolylineOptions
    lateinit var locationUser: LatLng
    lateinit var polyline: Polyline

    //Usuario
    lateinit var idUsuario: TextView
    lateinit var emailUsuario: TextView
    lateinit var imageViewUsuario: ImageView

    //Utilidades varias
    internal var rating = 0f
    internal var ratingList = ArrayList<String>()
    internal var ratingsList = ArrayList<Ratings>()
    internal var restaurantsNames = ArrayList<String>()
    internal var restaurants = ArrayList<Restaurants>()
    internal var keys = ArrayList<String>()
    internal var photosUrl = ArrayList<String>()
    internal var mCurrentPhotoPath = ""
    lateinit var positionMarker: LatLng
    lateinit var sortedList: List<Restaurants>
    lateinit var urlImage: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet))

        //Put BottomSheet Hidden when the app begin
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        floatingActionButton.visibility = View.INVISIBLE

        btnRating.setOnClickListener({submitButtonRating()})
        btnMostrarRating.setOnClickListener({setBtnMostrarRating()})
        floatingActionButton.setOnClickListener({setFloatingActionButton()})
        bottomSheet.setOnClickListener({setBottomSheetBehavior()})
        imageViewBottomSheet.setOnClickListener({setImageViewBottomSheet()})
        imageViewDescription.setOnClickListener({setImageViewDescription()})
        addNewImage.setOnClickListener({addNewImageToFirebase()})

        bottomSheetBehavior()

        retrieveDataFromFirebase()


        search_view.setOnQueryTextListener(object:  MaterialSearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String): Boolean {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertStringToLatLng(restaurants[restaurantsNames.indexOf(p0)].latLong), 20f))
                loadBottomSheet(restaurantsNames.indexOf(p0), restaurants)
                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {
                return false
            }

        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_api_user_key))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) { }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        mAuth = FirebaseAuth.getInstance()


        val navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main)
        val btnUsuarioHeader = navHeaderView.findViewById(R.id.imageButtonNavHeader) as ImageButton

        btnUsuarioHeader.setOnClickListener {
            if (btnUsuarioHeader.tag == null || btnUsuarioHeader.tag == R.drawable.ic_down_arrow) {
                navigationView.menu.clear()
                navigationView.inflateMenu(R.menu.activity_main_user)
                btnUsuarioHeader.setImageResource(R.drawable.ic_up_arrow)
                btnUsuarioHeader.tag = R.drawable.ic_up_arrow
            } else {
                navigationView.menu.clear()
                navigationView.inflateMenu(R.menu.activity_main_drawer)
                btnUsuarioHeader.setImageResource(R.drawable.ic_down_arrow)
                btnUsuarioHeader.tag = R.drawable.ic_down_arrow
            }
        }
        idUsuario = navHeaderView.findViewById(R.id.idUsuario) as TextView
        emailUsuario = navHeaderView.findViewById(R.id.Email) as TextView
        imageViewUsuario = navHeaderView.findViewById(R.id.imageViewNavHeader) as ImageView

        try {
            user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                updateUIFromUser()
            } else {
                try {
                    val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        //Initialization of the map
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MainActivity, R.raw.mapstyle))
        mMap.setOnMapClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            try {
                polyline.remove()
            } catch (ex: Exception) {
                //TODO ELIIMINAR ESTE CATCH
                ex.printStackTrace()
            }
        }
        //Check permission before getLocation of user
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //val location = lastKnownLocation
            //TODO CHANGE LOCATION FOR THE USER POSITION
            locationUser = LatLng(18.4809443, -69.9325192)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationUser, 15f))
            mMap.isMyLocationEnabled = true
        } else {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    TAG_CODE_PERMISSION_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            TAG_CODE_PERMISSION_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //The permission of location was granted
                        mMap.isMyLocationEnabled = true
                    }

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            FirebaseAuth.getInstance().signOut()
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            try {
                urlImage = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(mCurrentPhotoPath))

                val tempUri = getImageUri(applicationContext, urlImage)

                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://comidarapida-cae88.appspot.com/")
                val key = keys[markerSelected]
                val spaceRef = storageRef.child("images/" + key + "/" + tempUri.lastPathSegment)

                val uploadTask = spaceRef.putFile(tempUri)

                uploadTask.addOnSuccessListener { taskSnapshot -> myRef.child(key).child("urlImage").push().setValue("{\"url\": \"" + taskSnapshot.downloadUrl!!.toString() + "\"}") }.addOnFailureListener { e -> e.printStackTrace() }.addOnProgressListener { }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        val item = menu.findItem(R.id.action_search)
        search_view.setMenuItem(item)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_search) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

            val intent = Intent(this, SuggestActivity::class.java)
            startActivity(intent)

        } else if (id == R.id.nav_slideshow) {
            val dialog = MaterialDialog.Builder(this@MainActivity)
                    .title("Rating")
                    .customView(R.layout.layout_show_valued_places, false)
                    .positiveText("Aceptar")
                    .onPositive { _, _ ->
                        try {

                        } catch (ex: NullPointerException) {
                            ex.printStackTrace()
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                    .show()
            val rvValuedPlaces = dialog.view.findViewById(R.id.rvValuedPlaces) as RecyclerView

            /*
            Collections.sort(this.restaurants, new Comparator<Restaurants>() {
                @Override
                public int compare(Restaurants restaurants1, Restaurants restaurants2) {
                    return (getRatingList(restaurants1) >= getRatingList(restaurants2)) ? -1 : 0;
                }
            });
            */
            sortedList = restaurants.sortedWith(compareBy({ it.rating!!.values.any()})).reversed()

            val adapter = ValuedPlacesAdapter(sortedList, object : ValuedPlacesAdapter.OnItemClickLister {
                override fun OnItemClick(name: Restaurants, position: Int) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertStringToLatLng(name.latLong), 20f))
                    loadBottomSheet(position, sortedList)
                    dialog.dismiss()
                }
            })
            rvValuedPlaces.adapter = adapter
            rvValuedPlaces.layoutManager = LinearLayoutManager(this@MainActivity)
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app))
            startActivity(Intent.createChooser(intent, "Share with"))

        } else if (id == R.id.nav_send) {

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Share with")
            intent.`package` = "com.whatsapp"
            startActivity(intent)

        } else if (id == R.id.change_user) {
            mGoogleApiClient.clearDefaultAccountAndReconnect()

            try {
                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                startActivityForResult(signInIntent, RC_SIGN_IN)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onLocationChanged(location: Location) {
        //remove location callback:
        mLocationManager.removeUpdates(this)
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}

    override fun onProviderEnabled(s: String) {}

    override fun onProviderDisabled(s: String) {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED || bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    private fun retrieveDataFromFirebase() {
        //Firebase data retrieve
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var index = 0
                restaurants.clear()
                for (dataSnapshotchild in dataSnapshot.children) {
                    try {

                        if (dataSnapshotchild.child("validated").value.toString() == "true") {

                            restaurant = dataSnapshotchild.getValue(Restaurants::class.java)
                            keys.add(dataSnapshotchild.key)
                            restaurants.add(restaurant)


                            val latLong = dataSnapshotchild.child("latLong").value.toString()
                            val words = latLong.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            val latitud = words[0].substring(10)
                            val longitud = words[1].replace(")", "")
                            val latLng = LatLng(java.lang.Double.parseDouble(latitud), java.lang.Double.parseDouble(longitud))

                            mMap.addMarker(MarkerOptions()
                                    .position(latLng)
                                    .title(dataSnapshotchild.child("restName").value.toString())
                                    .snippet(index.toString())
                                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(this@MainActivity, R.drawable.ic_placeholder))))

                            index++
                            mMap.setOnMarkerClickListener { marker ->
                                //TODO AGREGAR LOS DEMAS CAMPOS DEL RESTAURANTE
                                markerSelected = Integer.parseInt(marker.snippet)
                                loadBottomSheet(markerSelected, restaurants)
                                true
                            }
                        }
                    } catch (ex: Exception) {
                        MaterialDialog.Builder(this@MainActivity)
                                .title("Error extrayendo informacion")
                                .content("Favor de cominucarse con el equipo de desarrollo, error 1001")
                                .show()
                    }

                }

                restaurantsNames.clear()
                for(rest in restaurants) restaurantsNames.add(rest.restName)
                search_view.setSuggestions(restaurantsNames.toTypedArray())
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun bottomSheetBehavior() {
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val toolbar = findViewById(R.id.toolbar) as Toolbar
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> try {
                        bottomSheet.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        RelativeLayoutBottomSheet.setBackgroundColor(resources.getColor(R.color.colorBlanco))
                        imageViewDescription.visibility = View.VISIBLE
                        setSupportActionBar(toolbar)
                        supportActionBar!!.subtitle = null
                        supportActionBar!!.setTitle(R.string.app_name)
                    } catch (ex: NullPointerException) {
                        ex.printStackTrace()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        imageViewDescription!!.visibility = View.INVISIBLE
                        bottomSheet.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        RelativeLayoutBottomSheet.setBackgroundColor(resources.getColor(R.color.colorBlanco))
                        supportActionBar!!.title = txtBottomSheet.text
                        supportActionBar!!.subtitle = rating.toString()
                        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white)
                        setSupportActionBar(toolbar)
                        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                        toolbar.setNavigationOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            supportActionBar!!.subtitle = null
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        animateColor(Color.rgb(63, 81, 181), Color.rgb(255, 255, 255))
                        RelativeLayoutBottomSheet!!.setBackgroundColor(Color.rgb(255, 255, 255))
                        toggle.syncState()
                        supportActionBar!!.setTitle(R.string.app_name)
                        supportActionBar!!.subtitle = null
                        toolbar.setNavigationOnClickListener { drawer_layout.openDrawer(Gravity.START) }
                        imageViewDescription.visibility = View.VISIBLE
                        floatingActionButton.visibility = FloatingActionButton.VISIBLE
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        supportActionBar!!.subtitle = null
                        toggle.syncState()
                        supportActionBar!!.setTitle(R.string.app_name)
                        toolbar.setNavigationOnClickListener { drawer_layout!!.openDrawer(Gravity.START) }

                        imageViewDescription.visibility = View.INVISIBLE
                        floatingActionButton.visibility = FloatingActionButton.INVISIBLE
                    }
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    private val lastKnownLocation: Location get() {
            mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = mLocationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                var l: Location? = null
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    l = mLocationManager.getLastKnownLocation(provider)
                }
                if (l == null) {
                    continue
                }
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }
            return bestLocation!!
        }

    private fun animateColor(colorFrom: Int, colorTo: Int) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 200 // milliseconds
        colorAnimation.addUpdateListener { animator -> bottomSheet!!.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()


    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    Log.d("Sig-in", "signInWithCredential:onComplete:" + task.isSuccessful)

                    if (task.isSuccessful) {
                        updateUIFromUser()
                    } else {
                        Log.w("Sig-in", "signInWithCredential", task.exception)
                        Toast.makeText(this@MainActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun getRatingList(markerSelected: Int, restaurant: List<Restaurants>) = try {
        rating = 0f
        if (ratingList.size != 0) {
            ratingList.clear()
        }
        for (restRating in restaurant[markerSelected].rating!!.values) {
            try {
                val jObj = JSONObject(restRating)
                ratingList.add(jObj.getString("rating"))
            } catch (e: JSONException) {
                Log.e("MYAPP", "unexpected JSON exception", e)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
        var i: Int = 0
        while (i < ratingList.size) {
            rating += java.lang.Float.parseFloat(ratingList[i])
            i++

        }
        rating /= i

        rating = Math.round(rating * 100.0f) / 100.0f

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    fun loadRatingListToUI(rest: Int, restaurant: List<Restaurants>) {
        try {

            if (ratingsList.size != 0) {
                ratingsList.clear()
            }
            for (restRating in restaurant[rest].rating!!.values) {
                try {

                    val jObj = JSONObject(restRating)
                    val ratings = Ratings()
                    ratings.rating = java.lang.Float.parseFloat(jObj.getString("rating"))
                    ratings.comment = jObj.getString("comment")
                    ratings.userName = jObj.getString("userName")
                    ratings.photoURL = jObj.getString("photoURL")
                    ratings.fecha = jObj.getString("fecha")
                    ratingsList.add(ratings)

                } catch (e: JSONException) {
                    Log.e("MYAPP", "unexpected JSON exception", e)
                }

            }

            Collections.sort(ratingsList) { ratings1, ratings2 -> if (Date(java.lang.Long.parseLong(ratings1.fecha)).time >= Date(java.lang.Long.parseLong(ratings2.fecha)).time) -1 else 0 }
            val rvContacts = findViewById(R.id.rvRating) as RecyclerView

            val adapter = RatingAdapter(ratingsList, false)
            rvContacts.adapter = adapter
            rvContacts.layoutManager = LinearLayoutManager(this@MainActivity)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun updateUIFromUser() {
        user = FirebaseAuth.getInstance().currentUser

        idUsuario.text = user!!.displayName
        emailUsuario.text = user!!.email
        imageViewUsuario.setImageBitmap(getBitmapFromURL(user!!.photoUrl!!.toString()))
    }

    private fun loadImageUI(rest: Int, restaurant: List<Restaurants>) {
        try {
            for (key in restaurant[rest].urlImage!!.keys) {
                try {
                    val jObj = JSONObject(restaurant[rest].urlImage!![key])
                    photosUrl.add(jObj.getString("url"))
                } catch (e: JSONException) {
                    Log.e("MYAPP", "unexpected JSON exception", e)
                }

            }
            DownloadImageTask(imageViewDescription).execute(photosUrl[0])
            DownloadImageTask(imageViewBottomSheet!!).execute(photosUrl[0])
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    fun getImageUri(inContext: Context, inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun convertStringToLatLng(latLong: String): LatLng {
        val words = latLong.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val latitud = words[0].substring(10)
        val longitud = words[1].replace(")", "")
        val latLng = LatLng(java.lang.Double.parseDouble(latitud), java.lang.Double.parseDouble(longitud))
        return latLng
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, // prefix
                ".jpg", // suffix
                storageDir      // directory
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.absolutePath
        return image
    }

    fun loadBottomSheet(rest: Int, restaurant: List<Restaurants>) {

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        txtBottomSheet.text = restaurant[rest].restName
        getRatingList(rest, restaurant)
        if (restaurant[rest].delivery) {
            lblDelivery!!.setText(R.string.available_delivery)
        } else {
            lblDelivery!!.setText(R.string.not_available_delivery)
        }

        txtRating.text = rating.toString()
        if (restaurant[rest].creditCards == "") {
            txtCreditCard.text = getString(R.string.no_credit_cards)
        } else {
            txtCreditCard.text = restaurant[rest].creditCards
        }
        ratingBarBottom.rating = rating
        if (restaurant[rest].telephones == "") {
            lblTelephone.text = getString(R.string.not_telephone_registered)
        } else {
            lblTelephone.text = restaurant[rest].telephones
        }
        loadRatingListToUI(rest,restaurant)

        if (restaurant[rest].urlImage == null) {
            imageViewDescription.clear()
            imageViewBottomSheet.setImageBitmap(null)
            textViewImageBottomSheet.setText(R.string.image_not_storage)
        } else {
            try {
                imageViewDescription.clear()
                photosUrl.clear()
                textViewImageBottomSheet.text = getString(R.string.photos_count, restaurant[rest].urlImage!!.size.toString())
                loadImageUI(rest,restaurant)
            } catch (ex: Exception) {
                imageViewDescription.setImageBitmap(null)
                ex.printStackTrace()
            }

        }
        val latLong = restaurant[rest].latLong
        val words = latLong.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val latitud = words[0].substring(10)
        val longitud = words[1].replace(")", "")
        val latLng = LatLng(java.lang.Double.parseDouble(latitud), java.lang.Double.parseDouble(longitud))

        positionMarker = latLng

        btnMostrarMenu.setOnClickListener({setBtnMostarMenu(rest,restaurant)})
        btnShowAllRatings.setOnClickListener({setBtnShowAllRatings(rest,restaurant)})
        btnShareRest.setOnClickListener({shareRest(rest,restaurant)})
        btnLlamarSitio.setOnClickListener({setBtnLlamarSitio(rest,restaurant)})

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
                    .execute(object : DirectionCallback {
                        override fun onDirectionSuccess(direction: Direction, rawBody: String) {
                            if (direction.isOK) {
                                val route = direction.routeList[0]
                                val leg = route.legList[0]

                                val directionPositionList = leg.directionPoint
                                polylineOptions = DirectionConverter.createPolyline(this@MainActivity, directionPositionList, 5, Color.RED)
                            }
                        }

                        override fun onDirectionFailure(t: Throwable) {

                        }
                    })

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    companion object {

        //Permisions
        private val TAG_CODE_PERMISSION_LOCATION = 0
        private val RC_SIGN_IN = 5

        fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
            var drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = DrawableCompat.wrap(drawable).mutate()
            }

            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                    drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }
    }

    internal fun submitButtonRating()  {
        MaterialDialog.Builder(this@MainActivity)
                .title("Rating")
                .customView(R.layout.item_layout_rating, true)
                .positiveText("Aceptar")
                .onPositive { dialog, which ->
                    try {
                        val v = dialog.customView
                        txtComments = v!!.findViewById(R.id.txtComments) as EditText
                        ratingBarAddComments = v.findViewById(R.id.ratingBarAddComments) as RatingBar
                        val rating = ratingBarAddComments.rating
                        val str = txtComments.text.toString()

                        myRef.child(keys[markerSelected]).child("rating").push().setValue("{ \"rating\" : \"" + rating + "\", \"comment\" : \"" + str + "\", \"userName\" : \"" + user!!.displayName + "\", \"photoURL\" : \"" + user!!.photoUrl + "\" , \"fecha\" : \"" + Date().time + "\"}")
                    } catch (ex: NullPointerException) {
                        ex.printStackTrace()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                .show()
    }

    internal fun setBtnMostarMenu(rest: Int, restaurant: List<Restaurants>) {
        val namePriceList = ArrayList<String>()
        try {
            for (restMenu in restaurant[rest].menu) {
                try {
                    val jObj = JSONObject(restMenu)
                    namePriceList.add(jObj.getString("name") + "\nRD$ " + jObj.getString("price"))
                } catch (e: JSONException) {
                    Log.e("MYAPP", "unexpected JSON exception", e)
                }

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        if (namePriceList.size == 0) {
            MaterialDialog.Builder(this@MainActivity)
                    .title(R.string.menu_not_available)
                    .show()
        } else {
            MaterialDialog.Builder(this@MainActivity)
                    .title("Menu")
                    .items(namePriceList)
                    .show()
        }
    }

    internal fun setBtnMostrarRating() {
        try {

            val dialog = MaterialDialog.Builder(this@MainActivity)
                    .title("Lista de ratings")
                    .customView(R.layout.layout_show_rating, true)
                    .show()

            if (ratingsList.size != 0) {
                ratingsList.clear()
            }
            for (restRating in restaurants[markerSelected].rating!!.values) {
                try {

                    val jObj = JSONObject(restRating)
                    val ratings = Ratings()
                    ratings.rating = java.lang.Float.parseFloat(jObj.getString("rating"))
                    ratings.comment = jObj.getString("comment")
                    ratings.userName = jObj.getString("userName")
                    ratings.photoURL = jObj.getString("photoURL")
                    ratings.fecha = jObj.getString("fecha")
                    ratingsList.add(ratings)

                } catch (e: JSONException) {
                    Log.e("MYAPP", "unexpected JSON exception", e)
                }

            }

            Collections.sort(ratingsList) { ratings1, ratings2 -> if (Date(java.lang.Long.parseLong(ratings1.fecha)).time >= Date(java.lang.Long.parseLong(ratings2.fecha)).time) -1 else 0 }
            val rvContacts = dialog.view.findViewById(R.id.rvRating) as RecyclerView

            val adapter = RatingAdapter(ratingsList, true)
            rvContacts.adapter = adapter
            rvContacts.layoutManager = LinearLayoutManager(this@MainActivity)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    internal fun setBtnShowAllRatings(rest: Int, restaurant: List<Restaurants>) {
        try {

            val dialog = MaterialDialog.Builder(this@MainActivity)
                    .title("Lista de ratings")
                    .customView(R.layout.layout_show_rating, true)
                    .show()

            if (ratingsList.size != 0) {
                ratingsList.clear()
            }
            for (restRating in restaurant[rest].rating!!.values) {
                try {

                    val jObj = JSONObject(restRating)
                    val ratings = Ratings()
                    ratings.rating = java.lang.Float.parseFloat(jObj.getString("rating"))
                    ratings.comment = jObj.getString("comment")
                    ratings.userName = jObj.getString("userName")
                    ratings.photoURL = jObj.getString("photoURL")
                    ratings.fecha = jObj.getString("fecha")
                    ratingsList.add(ratings)

                } catch (e: JSONException) {
                    Log.e("MYAPP", "unexpected JSON exception", e)
                }

            }

            Collections.sort(ratingsList) { ratings1, ratings2 -> if (Date(java.lang.Long.parseLong(ratings1.fecha)).time >= Date(java.lang.Long.parseLong(ratings2.fecha)).time) -1 else 0 }

            val rvContacts = dialog.view.findViewById(R.id.rvRating) as RecyclerView

            val adapter = RatingAdapter(ratingsList, true)
            rvContacts.adapter = adapter
            rvContacts.layoutManager = LinearLayoutManager(this@MainActivity)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    internal fun setFloatingActionButton() {
        polyline = mMap.addPolyline(polylineOptions)
    }

    internal fun setBtnLlamarSitio(rest: Int, restaurant: List<Restaurants>) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + restaurant[rest].telephones))
        startActivity(intent)
    }

    internal fun setBottomSheetBehavior() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    internal fun setImageViewBottomSheet() {
        ZGallery.with(this, photosUrl)
                .setToolbarColorResId(R.color.colorPrimary) // toolbar color
                .show()
    }

    internal fun setImageViewDescription() {
        ZGrid.with(this, photosUrl)
                .setToolbarColorResId(R.color.colorPrimary) // toolbar color
                .setTitle(getString(R.string.app_name)) // toolbar title
                .setSpanCount(3) // colums count
                .setGridImgPlaceHolder(R.color.colorPrimary) // color placeholder for the grid image until it loads
                .show()
    }

    internal fun addNewImageToFirebase() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                ex.printStackTrace()
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    internal fun shareRest(rest: Int,restaurant: List<Restaurants>) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Visita \""
                + restaurant[rest].restName
                + "\". Ubicado en: http://maps.google.com/maps?q=loc:" +
                String.format("%f,%f", positionMarker.latitude, positionMarker.longitude))
        startActivity(Intent.createChooser(intent, "Compartir sitio con:"))
    }
}