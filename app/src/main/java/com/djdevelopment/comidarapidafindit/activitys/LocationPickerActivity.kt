package com.djdevelopment.comidarapidafindit.activitys

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView

import com.djdevelopment.comidarapidafindit.R
import com.djdevelopment.comidarapidafindit.activitys.MainActivity.Companion.getBitmapFromVectorDrawable
import com.djdevelopment.comidarapidafindit.tools.UtilUI
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import java.io.IOException
import java.util.Locale

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private var googleMap: GoogleMap? = null
    private var mMapFragment: SupportMapFragment? = null
    private var selectedMarker: Marker? = null
    private var txtAddress: TextView? = null
    private var address: String? = null
    private var isTheFirstTime = false
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal var mLastLocation: Location? = null
    internal var locationUser: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker_map)

        try {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.map, mMapFragment)
            fragmentTransaction.commit()
        }
        txtAddress = findViewById(R.id.txtAddress) as TextView

        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
        }

        val btnSelectPosition = findViewById(R.id.btnSelectPosition) as Button
        btnSelectPosition.setOnClickListener {
            val result = Intent()

            result.putExtra("address", txtAddress!!.text)
            result.putExtra("userPosition", selectedMarker!!.position)

            setResult(Activity.RESULT_OK, result)
            finish()
        }

        setUpMap()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_pick_location, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_help_picky_location -> UtilUI.showAlertDialog(this, getString(R.string.help), getString(R.string.selectPositionHelp), R.string.iGotIt, null)
        }
        return true
    }

    public override fun onResume() {
        super.onResume()
    }

    private fun setUpMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        mMapFragment!!.getMapAsync(this)
        // Check if we were successful in obtaining the map.
        if (googleMap != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap!!.isMyLocationEnabled = true
            } else {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        TAG_CODE_PERMISSION_LOCATION)
            }
            googleMap!!.uiSettings.isCompassEnabled = true

            googleMap!!.setOnMapClickListener { clickedLat ->
                try {
                    selectedMarker!!.remove()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                selectedMarker = googleMap!!.addMarker(MarkerOptions()
                        .position(clickedLat)
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(this@LocationPickerActivity, R.drawable.ic_placeholder))))
                if (selectedMarker != null) {
                    selectedMarker!!.position = clickedLat
                    changeMapLocation(clickedLat)
                    setAddressText()
                }
            }

            val location = lastKnownLocation
            //TODO CAMBIAR UBICACION POR DEFECTO PARA PRUEBAS
            locationUser = LatLng(18.4809443, -69.9325192)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(locationUser, 15f))
        }

    }

    private fun changeMapLocation(latLng: LatLng) {

        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

    }

    override fun onMapReady(map: GoogleMap) {
        if (!isTheFirstTime) {
            googleMap = map

            setUpMap()
        }
        isTheFirstTime = true
    }

    private fun setAddressText() {

        val latLng = selectedMarker!!.position
        Thread(Runnable {
            val addresses: List<Address>
            val geocoder = Geocoder(this@LocationPickerActivity, Locale.getDefault())


            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                address = addresses[0].getAddressLine(0) + " " + addresses[0].getAddressLine(1)


            } catch (e: IOException) {
                e.printStackTrace()
            }

            this@LocationPickerActivity.runOnUiThread {
                txtAddress!!.visibility = View.VISIBLE
                if (address != null) {
                    txtAddress!!.text = address
                } else {
                    txtAddress!!.text = getString(R.string.direction_not_found, selectedMarker!!.position.latitude, selectedMarker!!.position.longitude)
                }
            }
        }).start()


    }

    override fun onConnected(bundle: Bundle?) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mGoogleApiClient!!.connect()

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient)
            if (mLastLocation != null) {
                val lat = mLastLocation!!.latitude
                val lon = mLastLocation!!.longitude

                locationUser = LatLng(lat, lon)
            }
        }
    }

    override fun onConnectionSuspended(i: Int) {


    }

    private val lastKnownLocation: Location? get() {
            val mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            return bestLocation
        }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    companion object {

        private val TAG_CODE_PERMISSION_LOCATION = 0
    }
}
