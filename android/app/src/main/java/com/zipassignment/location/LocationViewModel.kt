package com.zipassignment.location

import android.content.Context
import com.zipassignment.location.models.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationViewModel(context: Context) {
    
    private val repository = LocationRepository(context)

    suspend fun getLocationAndPlaces(): LocationResult = withContext(Dispatchers.IO) {
        try {
            val userLocation = repository.getCurrentLocation()

            val nearbyPlaces = repository.getNearbyPlaces(userLocation)

            // combined data for UI
            LocationResult(
                location = userLocation,
                places = nearbyPlaces
            )
        } catch (e: Exception) {
            throw LocationException("Failed to fetch location data: ${e.message}", e)
        }
    }

    class LocationException(message: String, cause: Throwable? = null) : 
        Exception(message, cause)
}