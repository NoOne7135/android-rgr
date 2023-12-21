package com.example.navigationapp.Model

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng

class LocationModel(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    private val _locationData = MutableLiveData<LatLng>()
    val locationData: LiveData<LatLng> = _locationData

    fun updateLocation() {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    _locationData.postValue(userLocation)
                }
            }
        }
    }

    companion object {
        const val LOCATION_UPDATE_INTERVAL = 10000L
        const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}


