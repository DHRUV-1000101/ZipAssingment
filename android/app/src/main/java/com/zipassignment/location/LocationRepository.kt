package com.zipassignment.location

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.zipassignment.location.models.Place
import com.zipassignment.location.models.UserLocation
import kotlinx.coroutines.tasks.await

class LocationRepository(context: Context) {

    private data class MockPlaceData(
        val name: String,
        val latOffset: Double,
        val lngOffset: Double
    )
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    suspend fun getCurrentLocation(): UserLocation {
        return try {
            val location = getCurrentLocationFromDevice()
            UserLocation(
                latitude = location.latitude,
                longitude = location.longitude
            )
        } catch (_: Exception) {
            // Fallback mock location
            UserLocation(
                latitude = 28.4595,
                longitude = 77.0266
            )
        }
    }

    // get mock nearby places data
    fun getNearbyPlaces(userLocation: UserLocation): List<Place> {
        return generateMockPlaces(userLocation)
    }

    // generates mock nearby places data
    private fun generateMockPlaces(userLocation: UserLocation): List<Place> {
        val mockPlaces = listOf(
            MockPlaceData("Movie Theatre", 0.002, 0.001),
            MockPlaceData("Park", 0.005, -0.003),
            MockPlaceData("Library", -0.003, 0.004),
            MockPlaceData("Pizza Hut", 0.001, -0.002),
            MockPlaceData("Gym", -0.004, -0.001),
            MockPlaceData("Shopping Mall", 0.006, 0.005),
            MockPlaceData("Hospital", -0.002, 0.003),
            MockPlaceData("Metro", 0.003, -0.004),
            MockPlaceData("Restaurant", 0.004, 0.002),
            MockPlaceData("Pharmacy", -0.001, -0.003)
        )

        return mockPlaces.mapIndexed { index, mockPlace ->
            val placeLat = userLocation.latitude + mockPlace.latOffset
            val placeLng = userLocation.longitude + mockPlace.lngOffset
            val distance = calculateDistance(
                userLocation.latitude, userLocation.longitude,
                placeLat, placeLng
            )

            Place(
                id = "place_$index",
                name = mockPlace.name,
                latitude = placeLat,
                longitude = placeLng,
                distance = distance
            )
        }.sortedBy { it.distance } // sorting by distance
    }

   // calculate distance
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
       val result = FloatArray(1)
       Location.distanceBetween(lat1, lon1, lat2, lon2, result)
       return result[0].toDouble()
    }

    //get device location
    private suspend fun getCurrentLocationFromDevice(): Location {
        return fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .await()
    }
}