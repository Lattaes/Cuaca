package com.lattae.cuaca.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String,
)

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int
)

data class Wind(
    @SerializedName("speed") val speed: Double
)

data class Rain(
    @SerializedName("1h") val oneHour: Double?
)

data class Clouds(
    @SerializedName("all") val allClouds: Int
)