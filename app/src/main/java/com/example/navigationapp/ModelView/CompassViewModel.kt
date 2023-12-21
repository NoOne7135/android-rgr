package com.example.navigationapp.ModelView

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.navigationapp.Model.CompassModel

class CompassViewModel(private val compassModel: CompassModel) : ViewModel() {

    private val _compassData = compassModel.compassData
    val compassData: LiveData<Float> = _compassData

    fun registerListeners() {
        compassModel.registerListeners()
    }

    fun unregisterListeners() {
        compassModel.unregisterListeners()
    }

}
