package com.example.actofitassignment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.io.File
import java.util.*
import android.R.attr.path
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import android.R.attr.path


lateinit var txtLocation: TextView
var location = ""
lateinit var context: Context

class LocationActivity : AppCompatActivity() {
    //Declaring the needed Variables
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val PERMISSION_ID = 1010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        context = this
        txtLocation = findViewById(R.id.txtLocation)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Log.e("Debug:", checkPermission().toString())
        Log.e("Debug:", isLocationEnabled().toString())
        RequestPermission()
        getLastLocation()

        val path = context.getExternalFilesDir(null)
        val letDirectory = File(path, "Location")
        letDirectory.mkdirs()
        val outputFile = File(letDirectory, "LocationFile.txt")
        try {
            val fos = FileOutputStream(outputFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val runnable: Runnable = object : Runnable {
            override fun run() {
                getLastLocation()
                location = txtLocation.text.toString()
                Log.e("Debug:", "LocationFile.txt: $location")
                outputFile.appendText(location)
                Handler(Looper.getMainLooper()).postDelayed(this, 50000)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 50000);
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        newLocationData()
                    } else {
                        Log.e("Debug:", "Your Location:" + location.longitude)
                        txtLocation.text =
                            "You Current Location is :" + "\n" +
                                    "Long: " + location.longitude +
                                    " , Lat: " + location.latitude + "\n" +
                                    getCityName(
                                        location.latitude, location.longitude
                                    )
                    }
                }
            } else {
                Toast.makeText(this, "Please Turn on Your device Location", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            RequestPermission()
        }
    }

    private fun newLocationData() {
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        Looper.myLooper()?.let {
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest, locationCallback, it
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.e("Debug:", "your last last location: " + lastLocation.longitude.toString())
            txtLocation.text =
                "You Last Location is : Long: " + lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n" + getCityName(
                    lastLocation.latitude,
                    lastLocation.longitude
                )
        }
    }

    private fun getCityName(latitude: Double, longitude: Double): Any? {
        var cityName: String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(latitude, longitude, 3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.e("Debug:", "Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }

    private fun checkPermission(): Boolean {
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if (
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun RequestPermission() {
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}
