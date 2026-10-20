package com.lattae.cuaca.domain.model

import com.lattae.cuaca.util.AqiLevel

data class AirCondition(
    val aqiLevel: AqiLevel = AqiLevel.GOOD,
    val aqiDesc: String? = null
)