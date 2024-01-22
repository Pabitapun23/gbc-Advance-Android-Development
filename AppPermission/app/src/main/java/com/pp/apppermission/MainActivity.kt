package com.pp.apppermission

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.pp.apppermission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // setup your binding
    private lateinit var binding: ActivityMainBinding

    // result launcher
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            permissionGranted:Boolean ->

        // if the user clicked allow, then permissionGranted === true
        // if the user clicked deny, then permissionGranted === false

        if (permissionGranted == true) {
            // show user location
            binding.tvResultsLabel.setText("Your location is: Vancouver, Canada")
        } else {
            val message = "This feature is disabled because you did not provide location permissions.\n" +
                    "Go to Settings > Apps > Permissions to enable location permissions. "
            binding.tvResultsLabel.setText(message)
        }

        // execute this code after the UI disappears
        var snackbar = Snackbar.make(binding.root, "Granted? ${permissionGranted}", Snackbar.LENGTH_LONG)
        snackbar.show()
        // permission = android.Manifest.permission.CAMERA
        // isGranted? = permissionGranted
    }


    private val APP_PERMISSIONS_LIST = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // result launcher for multiple permissions
    // - shows 1 permission box for each of the permissions in teh APP_PERMISSION_LIST variable
    private val multiplePermissionsResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        // results list is going to contain an objects that shows you what the user picked for each permission
            resultsList ->
        Log.d("PERMISSIONS_APP", resultsList.toString())

        // do something:
        // - a. all permissions granted
        // - b. or not?  (some were granted, some were denied --or it could mean that all were denied)

        // loop through each permission and check the status
        var allPermissionsGrantedTracker = true

        for (item in resultsList.entries) {
            if (item.key in APP_PERMISSIONS_LIST && item.value == false) {
                // if any status === false, then update this variable
                allPermissionsGrantedTracker = false
            }
        }

        // do something with the result
        if (allPermissionsGrantedTracker == true) {
            // all permissions were granted
            // they selected ALLOW for every permission
            var snackbar = Snackbar.make(binding.root, "All permissions granted", Snackbar.LENGTH_LONG)
            snackbar.show()
        } else {
            // - a. all permissions were denied
            // - b. some permission were denied
            var snackbar = Snackbar.make(binding.root, "Some permissions NOT granted", Snackbar.LENGTH_LONG)
            snackbar.show()
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ask for permissions
//        multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)

        binding.btnGetOnePermission.setOnClickListener {
            // code to request one permission
            // pass the permission you want into the .launch() function
            activityResultLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.btnGetMultiplePermissions.setOnClickListener {
            // code to request more than 1 permission
            // loop through each permission & check if the permission was granted vs. denied
            // do something as a result

            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
        }


    }




}