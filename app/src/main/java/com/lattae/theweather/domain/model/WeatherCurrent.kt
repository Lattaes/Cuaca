package com.lattae.theweather.domain.model

data class WeatherCurrent(
    val locationName: String? = null,
    val country: String? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val weatherId: Int = 0,
    val datetime: String? = null,
    val sunrise: String? = null,
    val sunset: String? = null,
    val timezone: String? = null,
    val weatherType: String? = null,
    val icon: String? = null,
    val temp: Int = 0,
    val tempMin: Int = 0,
    val tempMax: Int = 0,
    val feelsLike: Int = 0,
    val rainVolume: Double = 0.0,
    val windSpeed: Double = 0.0,
    val visibility: Int = 0,
    val clouds: Int = 0,
    val humidity: Int = 0,
    val airPressure: Int = 0
)