package com.zipassignment.location.models

data class UserLocation(
    val latitude: Double,
    val longitude: Double
)

data class LocationResult(
    val location: UserLocation,
    val places: List<Place>
)