package com.example.navigationapp.ModelView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.navigationapp.Model.LocationModel

class LocationViewModelFactory(private val locationModel: LocationModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(locationModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
