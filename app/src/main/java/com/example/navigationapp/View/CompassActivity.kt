package com.example.navigationapp.View

import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.navigationapp.Model.CompassModel
import com.example.navigationapp.ModelView.CompassViewModel
import com.example.navigationapp.ModelView.CompassViewModelFactory
import com.example.navigationapp.R
import com.example.navigationapp.databinding.ActivityCompassBinding

class CompassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompassBinding
    private lateinit var compassViewModel: CompassViewModel
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val compassModel = CompassModel(sensorManager)
        val factory = CompassViewModelFactory(compassModel)
        compassViewModel = ViewModelProvider(this, factory)[CompassViewModel::class.java]
        compassViewModel.compassData.observe(
            this,
        ) { rotation ->
            binding.compassImageView.rotation = rotation
        }

        binding.button.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        }
    }

    override fun onResume() {
        super.onResume()
        compassViewModel.registerListeners()
    }

    override fun onPause() {
        super.onPause()
        compassViewModel.unregisterListeners()
    }
}


