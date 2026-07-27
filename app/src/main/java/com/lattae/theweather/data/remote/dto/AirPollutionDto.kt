package com.lattae.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName


data class AirPollutionResponse(
    @SerializedName("list") val listAirPollution: List<AirPollutionItem>
)

data class AirPollutionItem(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: AirPollutionMain
)

data class AirPollutionMain(
    @SerializedName("aqi") val aqi: Double
)
