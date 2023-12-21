package com.example.navigationapp.View

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

import com.example.navigationapp.Model.LocationModel
import com.example.navigationapp.Model.LocationModel.Companion.LOCATION_UPDATE_INTERVAL
import com.example.navigationapp.Model.LocationModel.Companion.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.example.navigationapp.ModelView.LocationViewModel
import com.example.navigationapp.ModelView.LocationViewModelFactory
import com.example.navigationapp.R
import com.example.navigationapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationModel = LocationModel(this, fusedLocationClient)
        val factory = LocationViewModelFactory(locationModel)
        locationViewModel = ViewModelProvider(this, factory).get(LocationViewModel::class.java)

        val mapView = findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync { map ->
            googleMap = map

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val userLocation = LatLng(location.latitude, location.longitude)
                        locationViewModel.updateLocation()
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    }
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }

        locationViewModel.locationData.observe(this) { location ->
            updateMapWithLocation(location)
            updateTextViewWithLocation(location)
        }

        binding.Update.setOnClickListener {
            locationViewModel.updateLocation()
        }

        binding.Back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        }
        startLocationUpdates()
    }

    private fun updateMapWithLocation(location: LatLng) {
        googleMap.clear()
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        googleMap.addMarker(MarkerOptions().position(location))
        googleMap.uiSettings.isScrollGesturesEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true
        googleMap.uiSettings.isRotateGesturesEnabled = true
    }
    private fun startLocationUpdates() {
        val runnable = object : Runnable {
            override fun run() {
                locationViewModel.updateLocation()
                handler.postDelayed(this, LOCATION_UPDATE_INTERVAL)
            }
        }
        handler.postDelayed(runnable, LOCATION_UPDATE_INTERVAL)
    }
    private fun updateTextViewWithLocation(location: LatLng) {
        val textView = findViewById<TextView>(R.id.textView2)
        textView.text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
    }
}
