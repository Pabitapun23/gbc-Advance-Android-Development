package com.example.locationandmapdemo

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import com.example.locationandmapdemo.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import java.lang.Exception
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    private var TAG:String = "MY_APP_LOCATION"

    // Device location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val MY_COORDINATES = mutableListOf<Point>(
        Point.fromLngLat(-74.0060, 40.7128),        // new york city
        Point.fromLngLat(-73.5674,45.5019),         // montreal
        Point.fromLngLat(-72.5778, 44.5588),
        Point.fromLngLat(-75.6972,45.4215)
    )

    // permissions array
    private val APP_PERMISSIONS_LIST = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // showing the permissions dialog box & its result
    private val multiplePermissionsResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
            resultsList ->
        Log.d(TAG, resultsList.toString())

        var allPermissionsGrantedTracker = true


        for (item in resultsList.entries) {
            if (item.key in APP_PERMISSIONS_LIST && item.value == false) {
                allPermissionsGrantedTracker = false
            }
        }

        if (allPermissionsGrantedTracker == true) {
            var snackbar = Snackbar.make(binding.root, "All permissions granted", Snackbar.LENGTH_LONG)
            snackbar.show()


            // TODO: Get the user's location from the device (GPS, Wifi, etc)
            getDeviceLocation()


        } else {
            var snackbar = Snackbar.make(binding.root, "Some permissions NOT granted", Snackbar.LENGTH_LONG)
            snackbar.show()
            // TODO: Output a rationale for why we need permissions
            // TODO: Disable the get current location button so they can't accidently click on
            handlePermissionDenied()
        }

    }


    private fun getDeviceLocation() {
        // helper function to get device location
        // Before running fusedLocationClient.lastLocation, CHECK that the user gave you permission for FINE_LOCATION and COARSE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Usually, an app will ask again for permission (ask a second time)
            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
            return
        }

        // if YES, then go get the location, everything is fine ;)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location === null) {
                    Log.d(TAG, "Location is null")
                    return@addOnSuccessListener
                }
                // Output the location
                val message = "The device is located at: ${location.latitude}, ${location.longitude}"
                Log.d(TAG, message)
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()

                // adds annotation to the user's current device location
                addAnnotationToMap(location.latitude, location.longitude)

                Log.d(TAG, "adding annnotation :  ${addAnnotationToMap(location.latitude, location.longitude)}")
            }

    }


    private fun handlePermissionDenied() {
        // output the rationale
        // disable the get device location button
        binding.tvResults.setText("Sorry, you need to give us permissions before we can get your location. Check your settings menu and update your location permissions for this app.")
        // disable the button
        binding.btnGetDeviceLocation.isEnabled = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // instantiate the fusedLocationProvider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // getting coordinates from the address
        binding.btnGetCoordinates.setOnClickListener {
            Log.d(TAG, "Getting coordinates...")

            // forward geocoding: (street address --> latitude/longitude)
            // 1. Create an instance of the built in Geocoder class
            // Locale - sets the language and features of this
            val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())

            // - get the address from the user interface
            val addressFromUI = binding.etStreetAddress.text.toString()

            // try to get the coordinate
            try {
//                val searchResults:MutableList<Address>?  = geocoder.getFromLocationName("165 Kendal Avenue", 1)
                val searchResults:MutableList<Address>?  = geocoder.getFromLocationName(addressFromUI, 1)

                if (searchResults == null) {
                    // Log.e --> outputs the message as an ERROR (red)
                    // Log.d --> outputs the message as a DEBUG message
                    Log.e(TAG, "searchResults variable is null")
                    return@setOnClickListener
                }

                // if not null, then we were able to get some results (and it is possible for the results to be empty)
                if (searchResults.size == 0) {
                    binding.tvResults.setText("Search results are empty.")
                } else {
                    // 3. Get the coordinate
                    val foundLocation:Address = searchResults.get(0)
                    // 4. output to screen
                    var message = "Coordinates are: ${foundLocation.latitude}, ${foundLocation.longitude}"
                    binding.tvResults.setText(message)

                    // 5. output it to the map
                    addAnnotationToMap(foundLocation.latitude, foundLocation.longitude)

                    Log.d(TAG, message)
                }

            } catch (ex: Exception) {
                Log.e(TAG, "Error encountered while getting coordinate location.")
                Log.e(TAG, ex.toString())
            }

        }

        // getting address from the coordinates
        binding.btnGetStreetAddress.setOnClickListener {
            // 1. get the geocoder
            val geocoder:Geocoder = Geocoder(applicationContext, Locale.getDefault())

            // 2. get the lat/lng from the UI
            val latFromUI = binding.etLat.text.toString()
            val lngFromUI = binding.etLng.text.toString()

            // in real life you should use .toDoubleOrNull() and handle the null case
            val latAsDouble = latFromUI.toDouble()
            val lngAsDouble = lngFromUI.toDouble()

            // 3. try to find a matching street address
            try {
                val searchResults:MutableList<Address>? = geocoder.getFromLocation(latAsDouble, lngAsDouble, 1)
                if (searchResults == null) {
                    Log.e(TAG, "getting Street Address: searchResults is NULL ")
                    return@setOnClickListener
                }

                if (searchResults.size == 0) {
                    Log.d(TAG, "Search results <= 0")
                } else {
                    // 3. get the result
                    val matchingAddress: Address = searchResults.get(0)
                    // 4. output the properties of this address object
                    Log.d(TAG, "Search results found")
                    Log.d(TAG, "performForwardGeocoding: Postal Code " + matchingAddress.postalCode)
                    Log.d(
                        TAG,
                        "performForwardGeocoding: Country Code " + matchingAddress.countryCode
                    )
                    Log.d(
                        TAG,
                        "performForwardGeocoding: Country Name " + matchingAddress.countryName
                    )
                    Log.d(TAG, "performForwardGeocoding: Locality " + matchingAddress.locality)
                    Log.d(
                        TAG,
                        "performForwardGeocoding: getThoroughfare " + matchingAddress.thoroughfare
                    )
                    Log.d(
                        TAG,
                        "performForwardGeocoding: getSubThoroughfare " + matchingAddress.subThoroughfare
                    )

                    // output this information to the UI
                    val output = "${matchingAddress.subThoroughfare}," +
                            " ${matchingAddress.thoroughfare}," +
                            " ${matchingAddress.locality}," +
                            " ${matchingAddress.adminArea}," +
                            " ${matchingAddress.countryCode}," +
                            " ${matchingAddress.countryName}"
                    binding.tvResults.setText(output)
                    Log.d(TAG, output)
                }

            } catch(ex:Exception) {
                Log.e(TAG, "Error encountered while getting coordinate location.")
                Log.e(TAG, ex.toString())
            }

        }

        // getting user's current location
        binding.btnGetDeviceLocation.setOnClickListener {
            // Check for permissions & do resulting actions
            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
        }

        // For maps
        binding.btnShowMarker.setOnClickListener {
            // OPTIONAL: delete any existing markers before showing your new marker
            // binding.mapView.annotations.cleanup()

            // add a new annotation to the map
            addAnnotationToMap(43.6532, -79.3832)
        }

        binding.btnShowManyMarkers.setOnClickListener {
            addManyAnnotations(MY_COORDINATES, R.drawable.camping)
        }


        binding.btnDeleteAllMarkers.setOnClickListener {
            binding.mapView.annotations.cleanup()
        }
    }


    // helper functions to show annotations

    private fun addManyAnnotations(coordinatesList:MutableList<Point>, @DrawableRes drawableImageResourceId: Int = R.drawable.red_marker) {

        val icon = MapboxUtils.bitmapFromDrawableRes(applicationContext, drawableImageResourceId)

        if (icon == null) {
            Log.d(TAG, "ERROR: Unable to convert provided image into the correct format.")
            return
        }

        val annotationApi = binding.mapView?.annotations
        val pointAnnotationManager =
            annotationApi?.createPointAnnotationManager(
                AnnotationConfig(
                    annotationSourceOptions = AnnotationSourceOptions(maxZoom = 16)
                )
            )

        // loop through our list of coordinates & add them to the map
        val pointAnnotationOptionsList: MutableList<PointAnnotationOptions> = ArrayList()

        for (currCoordinate in coordinatesList) {
            pointAnnotationOptionsList.add(
                PointAnnotationOptions()
                    .withPoint(currCoordinate)
                    .withIconImage(icon)
            )
        }

        pointAnnotationManager?.create(pointAnnotationOptionsList)

    }

    private fun addAnnotationToMap(lat:Double, lng:Double, @DrawableRes drawableImageResourceId: Int = R.drawable.red_marker) {
        Log.d(TAG, "Attempting to add annotation to map")

        // get the image you want to use as a map marker
        // & resize it to fit the proportion of the map as the user zooms in and zoom out
        // val icon = MapboxUtils.bitmapFromDrawableRes(applicationContext, R.drawable.pikachu)
        val icon = MapboxUtils.bitmapFromDrawableRes(applicationContext, drawableImageResourceId)

        // error handling code: sometimes, the person may provide an image that cannot be
        // properly converted to a map marker
        if (icon == null) {
            Log.d(TAG, "ERROR: Unable to convert provided image into the correct format.")
            return
        }


        // code sets up the map so you can add markers
        val annotationApi = binding.mapView?.annotations
        val pointAnnotationManager =
            annotationApi?.createPointAnnotationManager(
                AnnotationConfig(
                    annotationSourceOptions = AnnotationSourceOptions(maxZoom = 16)
                )
            )

        // Create a marker & configure the options for that marker
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(lng, lat))
            .withIconImage(icon)

        // Add the resulting pointAnnotation to the map.
        pointAnnotationManager?.create(pointAnnotationOptions)


    }

}