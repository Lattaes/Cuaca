package com.lattae.cuaca.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherDailyResponse(
    @SerializedName("list") val listWeatherDaily: List<WeatherDaily>,
    @SerializedName("city") val city: City
)

data class WeatherDaily(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("clouds") val clouds: Clouds,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("rain") val rain: Rain,
    @SerializedName("dt_txt") val dtTxt: String
)

data class City(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("timezone") val timezone: Int
)