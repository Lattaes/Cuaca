package com.lattae.cuaca.domain.model

data class WeatherDaily(
    val datetime: String? = null,
    val icon: String? = null,
    val tempMin: Int = 0,
    val tempMax: Int = 0
)