package com.lattae.theweather.util


enum class WeatherType(val value: String) {
    THUNDERSTORM("Thunderstorm"),
    DRIZZLE("Drizzle"),
    RAIN("Rain"),
    SNOW("Snow"),
    ATMOSPHERE("Atmosphere"),
    CLEAR("Clear"),
    CLOUDS("Clouds");

    companion object {
        infix fun from(value: String): WeatherType {
            return WeatherType.entries.firstOrNull{ it.value == value } !!
        }
    }
}