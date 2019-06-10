package com.example.confapp.gmaps

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import bolts.Task
import com.example.confapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.jar.Manifest

class GMapsActivity :AppCompatActivity(), OnMapReadyCallback{
    val FINE_LOCATION : String = android.Manifest.permission.ACCESS_FINE_LOCATION
    val COARSE_LOCATION : String = android.Manifest.permission.ACCESS_COARSE_LOCATION
    val LOCATION_PERMISSION_REQUEST_CODE: Int = 1234
    val DEFAULT_ZOOM : Float = 17f
    val RITEH_LOCATION = LatLng(45.337694, 14.425776)

    private lateinit var mMap: GoogleMap

    private lateinit var marker: Marker
    private lateinit var usrLocation: LatLng

    private lateinit var mInfo: ImageView
    private lateinit var mTarget: ImageView
    private lateinit var mPerson: ImageView

    var mLocationPermissionGranted: Boolean = false
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gmaps)

        mInfo = findViewById(R.id.place_info)
        mTarget = findViewById(R.id.target_marker)
        mPerson = findViewById(R.id.person_marker)

        getLocationPermission()

    }


/*
    fun geoLocate(){
        val search: String = "Tehnički fakultet"
        val geocoder = Geocoder(this)
        var list: List<Address>? = null
        try {
            list = geocoder.getFromLocationName(search, 1)
        }catch (e: IOException){
            Toast.makeText(this, "IOException: " + e.message, Toast.LENGTH_LONG).show()
        }

        if (list!!.isNotEmpty()){
            var address: Address = list[0]

            Toast.makeText(this, "result: " + address.toString(), Toast.LENGTH_LONG).show()
        }
    }
*/
    fun initMap(){

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(p0: GoogleMap?) {
        Toast.makeText(this, "Map is ready!", Toast.LENGTH_LONG).show()
        mMap = p0!!

        if (mLocationPermissionGranted){
            getDeviceLocation(false)

            val permission = ContextCompat.checkSelfPermission(this,
                FINE_LOCATION)

            if (permission == PackageManager.PERMISSION_GRANTED) {
                mMap?.isMyLocationEnabled = true
            }
        }

        val snippet: String = "Vukovarska ul. 58, 51000, Rijeka\n" +
                "Phone Number: 051 651 444\n" +
                "Website: riteh.uniri.hr\n" +
                "Rating: 4.4"
        val options = MarkerOptions().position(RITEH_LOCATION).title("Tehnički fakultet u Rijeci").snippet(snippet)
        marker = mMap.addMarker(options)
        moveCamera(RITEH_LOCATION, DEFAULT_ZOOM)

        mTarget.setOnClickListener {
            moveCamera(RITEH_LOCATION, DEFAULT_ZOOM)
        }

        mPerson.setOnClickListener {
            getDeviceLocation(true)
        }

        mInfo.setOnClickListener {
            if (marker.isInfoWindowShown){
                marker.hideInfoWindow()
            }else{
                marker.showInfoWindow()
            }
        }
        //geoLocate()

    }

    private fun getLocationPermission(){
        val permissions = Array<String>(2){android.Manifest.permission.ACCESS_FINE_LOCATION; android.Manifest.permission.ACCESS_COARSE_LOCATION}

        if(ContextCompat.checkSelfPermission(this.applicationContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.applicationContext, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true
                initMap()
            }else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mLocationPermissionGranted = false

        when(requestCode){
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if(grantResults.size > 0){
                    for (res in grantResults){
                        if(res != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false
                            return
                        }
                    }
                    mLocationPermissionGranted = true
                    //initialize map
                    initMap()
                }
            }
        }
    }
// mozda ako stignem
    fun getDeviceLocation(move: Boolean){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            if (mLocationPermissionGranted){
                var location: com.google.android.gms.tasks.Task<Location> = mFusedLocationProviderClient.lastLocation
                location.addOnCompleteListener {
                    if (it.isSuccessful){
                        //Toast.makeText(this, "found your location ;) ;)", Toast.LENGTH_LONG).show()
                        var userLocation : Location = it.result

                        if (move){
                            moveCamera(LatLng(userLocation.latitude, userLocation.longitude), DEFAULT_ZOOM)
                        }

                    }else{
                        Toast.makeText(this, "Failed to find you :(", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }catch (e : SecurityException){
            Toast.makeText(this, "Maps Error: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun moveCamera(latLng: LatLng, zoom: Float){
        //Toast.makeText(this, "moving to location", Toast.LENGTH_LONG).show()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }
}