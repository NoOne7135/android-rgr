package com.example.navigationapp.Model

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.roundToInt

class CompassModel(private val sensorManager: SensorManager) {

    private val _compassData = MutableLiveData<Float>()
    val compassData: LiveData<Float> = _compassData

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val gyroscopeReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationValues = FloatArray(3)
    private var emptyFloatList: MutableList<Float> = mutableListOf()

    init {
        checkSensors()
    }
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, accelerometerReading, 0, event.values.size)
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, magnetometerReading, 0, event.values.size)
            } else if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
                System.arraycopy(event.values, 0, gyroscopeReading, 0, event.values.size)
            }
            updateOrientation()
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }
    fun registerListeners() {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        accelerometer?.let { sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        magnetometer?.let { sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        gyroscope?.let { sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    private fun updateOrientation() {
        if (magnetometerReading.isNotEmpty()) {
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
            SensorManager.getOrientation(rotationMatrix, orientationValues)
        } else {
            if (gyroscopeReading.isNotEmpty()) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, gyroscopeReading)
                SensorManager.getOrientation(rotationMatrix, orientationValues)
            } else {

            }
        }

        val azimuth = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
        val rotation = -azimuth.roundToInt().toFloat()

        emptyFloatList.add(0, rotation)
        if (emptyFloatList.size > 10) {
            emptyFloatList.removeAt(emptyFloatList.size - 1)
        }

        val averageRotation = emptyFloatList.average().toFloat()
        _compassData.postValue(averageRotation)
    }

    private fun checkSensors() {
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        var hasMagneticFieldSensor = false
        for (sensor in sensorList) {
            if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                hasMagneticFieldSensor = true
                break
            }
        }

        if (!hasMagneticFieldSensor) {
            setUpGyroscope()
        }
    }

    private fun setUpGyroscope() {
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (gyroscope == null) {

        } else {
            sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregisterListeners() {
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, accelerometerReading, 0, event.values.size)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetometerReading, 0, event.values.size)
            }
            Sensor.TYPE_GYROSCOPE -> {
                System.arraycopy(event.values, 0, gyroscopeReading, 0, event.values.size)
            }
        }
        updateOrientation()
    }
}

