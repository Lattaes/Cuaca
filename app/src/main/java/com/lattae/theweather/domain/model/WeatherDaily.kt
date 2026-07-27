package com.lattae.theweather.domain.model

import java.time.LocalDate

data class WeatherDaily(
    val datetime: LocalDate = LocalDate.now(),
    val icon: String? = null,
    val tempMin: Int = 0,
    val tempMax: Int = 0
)