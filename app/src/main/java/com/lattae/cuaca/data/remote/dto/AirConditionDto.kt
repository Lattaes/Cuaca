package com.lattae.cuaca.data.remote.dto

import com.google.gson.annotations.SerializedName


data class AirConditionResponse(
    @SerializedName("list") val listAirCondition: List<AirConditionItem>
)

data class AirConditionItem(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: AirConditionMain
)

data class AirConditionMain(
    @SerializedName("aqi") val aqi: Double
)
