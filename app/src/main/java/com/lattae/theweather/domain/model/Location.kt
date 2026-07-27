package com.lattae.theweather.domain.model

data class Location (
    val locationName: String? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val country: String? = null
)