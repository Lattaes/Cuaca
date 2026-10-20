package com.lattae.cuaca.domain.model

data class Weather(
    val weatherCurrent: WeatherCurrent? = null,
    val weatherHourly: List<WeatherHourly>? = emptyList(),
    val weatherDaily: List<WeatherDaily>? = emptyList(),
    val airCondition: AirCondition? = null
)