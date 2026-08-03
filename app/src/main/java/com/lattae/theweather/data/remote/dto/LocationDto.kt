package com.lattae.theweather.data.remote.dto

import com.google.gson.annotations.SerializedName


data class LocationResponse(
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String? = null
)
