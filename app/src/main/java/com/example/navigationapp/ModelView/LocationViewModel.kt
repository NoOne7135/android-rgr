package com.example.navigationapp.ModelView

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.navigationapp.Model.LocationModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel(private val locationModel: LocationModel) : ViewModel() {

    private val _locationData = locationModel.locationData
    val locationData: LiveData<LatLng> = _locationData

    fun updateLocation() {
        locationModel.updateLocation()
    }
}
