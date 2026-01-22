package com.zipassignment.location.models

data class Place(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double
)