package com.lattae.theweather.data.local

import androidx.room.Entity

@Entity(tableName = "locations", primaryKeys = ["lat", "lon"])
data class LocationEntity(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null
)