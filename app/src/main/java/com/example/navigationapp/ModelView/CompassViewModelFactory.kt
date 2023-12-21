package com.example.navigationapp.ModelView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.navigationapp.Model.CompassModel

class CompassViewModelFactory(private val compassModel: CompassModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompassViewModel::class.java)) {
            return CompassViewModel(compassModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
