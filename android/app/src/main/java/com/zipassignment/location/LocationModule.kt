package com.zipassignment.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.*
import kotlinx.coroutines.*
import android.location.LocationManager
import android.provider.Settings
import android.content.Intent
import android.content.Context

class LocationModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    private val viewModel = LocationViewModel(reactApplicationContext)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var permissionAskedOnce = false

    override fun getName(): String = "LocationModule"

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            reactApplicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        val activity = reactApplicationContext.currentActivity ?: return
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1001
        )
    }

    private fun isLocationEnabled(): Boolean {
        val manager =
            reactApplicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun openLocationSettings() {
        val activity = reactApplicationContext.currentActivity ?: return
        activity.startActivity(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        )
    }

    @ReactMethod
    fun getLocationAndPlaces(promise: Promise) {

        if (!hasLocationPermission()) {

            if (!permissionAskedOnce) {
                permissionAskedOnce = true
                requestLocationPermission()
                promise.resolve(null)
                return
            }

            scope.launch {
                try {
                    val result = viewModel.getLocationAndPlaces()
                    promise.resolve(convertToWritableMap(result))
                } catch (e: Exception) {
                    promise.reject("LOCATION_ERROR", e.message, e)
                }
            }
            return
        }

        if (!isLocationEnabled()) {
            openLocationSettings()
            promise.resolve(null)
            return
        }

        scope.launch {
            try {
                val result = viewModel.getLocationAndPlaces()
                promise.resolve(convertToWritableMap(result))
            } catch (e: Exception) {
                promise.reject("LOCATION_ERROR", e.message, e)
            }
        }
    }

    private fun convertToWritableMap(
        result: com.zipassignment.location.models.LocationResult
    ): WritableMap {
        return Arguments.createMap().apply {
            putMap("location", Arguments.createMap().apply {
                putDouble("latitude", result.location.latitude)
                putDouble("longitude", result.location.longitude)
            })

            val placesArray = Arguments.createArray()
            result.places.forEach { place ->
                placesArray.pushMap(Arguments.createMap().apply {
                    putString("id", place.id)
                    putString("name", place.name)
                    putDouble("latitude", place.latitude)
                    putDouble("longitude", place.longitude)
                    putDouble("distance", place.distance)
                })
            }
            putArray("places", placesArray)
        }
    }

    override fun onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy()
        scope.cancel()
    }
}