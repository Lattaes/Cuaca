package com.lattae.theweather.util

import com.lattae.theweather.R


enum class WeatherType(
    val value: Int,
    val notifTitle: Int? = null,
    val notifMessage: Int? = null,
    val isWarningNeeded: Boolean = false
) {
    THUNDERSTORM(
        value = R.string.thunderstorm,
        notifTitle = R.string.thunderstorm_title,
        notifMessage = R.string.thunderstorm_desc,
        isWarningNeeded = true
    ),
    DRIZZLE(
        value = R.string.drizzle
    ),
    RAIN(
        value = R.string.rain,
        notifTitle = R.string.heavy_rain_title,
        notifMessage = R.string.heavy_rain_desc,
        isWarningNeeded = true
    ),
    SNOW(
        value = R.string.snow,
        notifTitle = R.string.snow_title,
        notifMessage = R.string.snow_desc,
        isWarningNeeded = true
    ),
    ATMOSPHERE(
        value = R.string.atmosphere,
        notifTitle = R.string.extreme_atmosphere_title,
        notifMessage = R.string.extreme_atmosphere_desc,
        isWarningNeeded = true
    ),
    CLEAR(
        value = R.string.clear
    ),
    CLOUDS(
        value = R.string.clouds
    );

    fun notifyWeather(id: Int): Boolean {
        return when (this) {
            RAIN -> id in listOf(502, 503, 504, 521, 522)
            SNOW -> id in listOf(602, 621, 622)
            else -> isWarningNeeded
        }
    }

    companion object {
        infix fun from(id: Int): WeatherType {
            return when(id) {
                in 200..232 -> THUNDERSTORM
                in 300..321 -> DRIZZLE
                in 500..531 -> RAIN
                in 600..622 -> SNOW
                in 701..781 -> ATMOSPHERE
                800 -> CLEAR
                in 801..804 -> CLOUDS
                else -> CLEAR
            }
        }
    }
}