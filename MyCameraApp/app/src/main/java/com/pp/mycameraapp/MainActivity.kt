package com.pp.mycameraapp

import android.content.ContentValues
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.pp.mycameraapp.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // setup the screen :
    private val TAG:String = "MY_CAMERA_APP"

    private lateinit var binding: ActivityMainBinding


    // LifecycleCameraController = start, stop, and preview the camera
    lateinit var cameraController: LifecycleCameraController

    // helper function for initializing the cameraController property
    private fun initializeCameraController() {
        // initialize the camera controller variable
        cameraController = LifecycleCameraController(applicationContext)

        // bind to the activity lifecycle
        // This allows the activity's lifecycle to control when the camera is opened, stopped, and closed.
        cameraController.bindToLifecycle(this)

        // OPTIONAL: Set the default camera that should be displayed in the <PreviewView>
        // - devices could have more than 1 camera (front/back)
        // - by default, the cameraController will select the back camera

        // associate the controller with the <PreviewView> in the xml file
        binding.previewView.controller = cameraController

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        // bindings
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ask for run time permission (camera)
        if (hasPermissions() == false) {
            // show the popup box that requests permission from the user
            ActivityCompat.requestPermissions(this, CAMERAX_PERMISSIONS, 0)
        } else {
            // the user already previously granted permissions
            // and so you can move on .... (no further actions required)
        }

        // initialize the camera controller
        initializeCameraController()

        binding.btnFlipCamera.setOnClickListener {
            flipCamera()
        }

        binding.btnTakePhoto.setOnClickListener {
            savePhotoToDeviceMemory()
        }

    }

    private fun flipCamera(){
        // code to choose which camera to use
//        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

//        if  the front camera is currently selected, then change it to be the back camera
        if (cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            // if the front camera is currently selected, then change to be the back camera
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        } else if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
            // if the back camera is currently selected, then change to be the front camera
            cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }

    }

    // helper function to save photo
    private fun savePhotoToDeviceMemory()  {

        // define the file format for your photos
        // In this example, the file format will be the date & time when file take,
        // example: 2021-05-03-15-30-00.jpg
        val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                // define what folder the images are stored in
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }


        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()


        // capture and save the photo
        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Snackbar.make(binding.root, "Saving photo failed, see console for error", Snackbar.LENGTH_LONG).show()
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }


                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    var msg = "Photo capture succeeded: ${output.savedUri}"
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }


    // write code to show the permissions box
    // - return true if user grants permission
    // - return false if user denies permissions
    private fun hasPermissions():Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    companion object {
        // WRITE_EXTERNAL_STORAGE is only needed for Android P and below
        // Android P == Android version 9.0 == API 28
        private val CAMERAX_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA
        )
    }




}