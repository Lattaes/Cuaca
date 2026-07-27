package com.lattae.theweather.domain.model

import com.lattae.theweather.util.AqiLevel

data class AirPollution(
    val aqiLevel: AqiLevel = AqiLevel.GOOD,
    val aqiDesc: String? = null
)