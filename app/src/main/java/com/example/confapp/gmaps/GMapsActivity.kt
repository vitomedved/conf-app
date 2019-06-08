package com.example.confapp.gmaps

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.confapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import java.util.jar.Manifest

class GMapsActivity :AppCompatActivity(), OnMapReadyCallback{
    val FINE_LOCATION : String = android.Manifest.permission.ACCESS_FINE_LOCATION
    val COARSE_LOCATION : String = android.Manifest.permission.ACCESS_COARSE_LOCATION
    val LOCATION_PERMISSION_REQUEST_CODE: Int = 1234

    private lateinit var mMap: GoogleMap

    var mLocationPermissionGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gmaps)

        getLocationPermission()

    }

    fun initMap(){
        var mapFragment : SupportMapFragment? = null
        mapFragment = fragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }


    override fun onMapReady(p0: GoogleMap?) {
        Toast.makeText(this, "Map is ready!", Toast.LENGTH_LONG).show()
        mMap = p0!!
    }

    private fun getLocationPermission(){
        val permissions = Array<String>(2){android.Manifest.permission.ACCESS_FINE_LOCATION; android.Manifest.permission.ACCESS_COARSE_LOCATION}

        if(ContextCompat.checkSelfPermission(this.applicationContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.applicationContext, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true
            }else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
            }
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
}