package com.pp.googlemapdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pp.googlemapdemo.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationHelper: LocationHelper
    private lateinit var currentLocation: LatLng
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.currentLocation = LatLng(43.74853, -79.26374)

        this.locationHelper = LocationHelper.instance
        this.locationHelper.checkPermissions(this)
        if (locationHelper.locationPermissionGranted) {
            // initiate to receive location updates
            this.locationCallback = object : LocationCallback() {
                override fun onLocationResult(location: LocationResult) {
//                    super.onLocationResult(location)

                    for (loc in location.locations) {
                        currentLocation = LatLng(loc.latitude, loc.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f))
                        mMap.addMarker(MarkerOptions().position(currentLocation).title("You're Here"))
                    }
                }
            }

            locationHelper.requestLocationUpdates(this, locationCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        this.locationHelper.stopLocationUpdates(this, locationCallback)
    }

    override fun onResume() {
        super.onResume()
        this.locationHelper.requestLocationUpdates(this, locationCallback)
    }


    /**
     * Manipulates the map once available. 
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        // - max zoom is 20.0f (buildings available)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.currentLocation, 15.0f))
        mMap.addMarker(MarkerOptions().position(this.currentLocation).title("You're Here"))

        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.isTrafficEnabled = true

        val uiSettings = googleMap.uiSettings
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = true

    }
}