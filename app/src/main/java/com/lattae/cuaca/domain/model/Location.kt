package com.lattae.cuaca.domain.model

data class Location (
    val id: Int = 0,
    val locationName: String? = null,
    val isCurrentLocation: Boolean = false,
    val isSavedLocation: Boolean = false,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val icon: String? = null,
    val tempMin: Int = 0,
    val tempMax: Int = 0,
    val country: String? = null,
    val state: String? = null
)
