package com.lattae.theweather.domain.model

import java.time.LocalDate

data class WeatherCurrent(
    val locationName: String? = null,
    val datetime: LocalDate = LocalDate.now(),
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