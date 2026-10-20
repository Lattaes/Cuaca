package com.lattae.cuaca.util

import com.lattae.cuaca.R


enum class WeatherType(
    val value: Int
) {
    THUNDERSTORM(value = R.string.thunderstorm,),
    DRIZZLE(value = R.string.drizzle),
    RAIN(value = R.string.rain,),
    SNOW(value = R.string.snow,),
    ATMOSPHERE(value = R.string.atmosphere,),
    CLEAR(value = R.string.clear),
    CLOUDS(value = R.string.clouds);

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