package com.lattae.theweather.domain.model

data class Weather(
    val weatherCurrent: WeatherCurrent? = null,
    val weatherDaily: List<WeatherDaily>? = emptyList(),
    val airPollution: AirPollution? = null
)