package com.lattae.theweather.data.mapper

import com.lattae.theweather.domain.model.AirPollution
import com.lattae.theweather.domain.model.Weather
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.domain.model.WeatherDaily
import com.lattae.theweather.util.AqiLevel
import com.lattae.theweather.util.toFullDate
import com.lattae.theweather.util.toTimeString
import com.lattae.weatherapp.data.remote.dto.AirPollutionResponse
import com.lattae.weatherapp.data.remote.dto.WeatherCurrentResponse
import com.lattae.weatherapp.data.remote.dto.WeatherDailyResponse
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.abs

fun WeatherCurrentResponse.toWeatherCurrent(): WeatherCurrent {

    return WeatherCurrent(
        locationName = this.name,
        country = this.sys.country,
        lat = this.coord.lat,
        lon = this.coord.lon,
        weatherId = this.weather.firstOrNull()?.id ?: 0,
        datetime = this.dt.toString(),
        sunrise = this.sys.sunrise.toString(),
        sunset = this.sys.sunset.toString(),
        timezone = this.timezone.toString(),
        weatherType = this.weather.firstOrNull()?.main,
        icon = this.weather.firstOrNull()?.icon,
        temp = this.main.temp.toInt(),
        tempMin = this.main.tempMin.toInt(),
        tempMax = this.main.tempMax.toInt(),
        feelsLike = this.main.feelsLike.toInt(),
        rainVolume = this.rain?.oneHour ?: 0.0,
        windSpeed = this.wind.speed,
        visibility = (this.visibility) / 1000,
        clouds = this.clouds.allClouds,
        humidity = this.main.humidity,
        airPressure = this.main.pressure
    )
}

fun AirPollutionResponse.toAirPollution(): AirPollution {
    val aqiValue = this.listAirPollution.firstOrNull()?.main?.aqi ?: 2
    val aqiLevel = AqiLevel.fromScore(aqiValue.toInt())

    return AirPollution(
        aqiLevel = aqiLevel,
        aqiDesc = aqiLevel.desc
    )
}

fun WeatherDailyResponse.toWeatherDaily(): List<WeatherDaily> {
    val timezoneOffset = this.city.timezone
    val todayAtLocation = Instant.now()
        .atZone(ZoneOffset.ofTotalSeconds(timezoneOffset))
        .toLocalDate()
        .toString()

    return this.listWeatherDaily
        .groupBy { it.dtTxt.take(10) }
        .filter { it.key > todayAtLocation }
        .entries
        .take(5)
        .map { (dateString, items) ->
            val noonItem = items.minByOrNull { item ->
                val hour = item.dtTxt.substring(11, 13).toIntOrNull() ?: 12
                abs(hour - 12)
            } ?: items.first()

            WeatherDaily(
                datetime = LocalDate.parse(dateString),
                icon = noonItem.weather.firstOrNull()?.icon,
                tempMin = items.minOf { it.main.tempMin.toInt() },
                tempMax = items.maxOf { it.main.tempMax.toInt() }
            )
        }
}

fun Weather.toWeather(): Weather {
    return Weather(
        weatherCurrent,
        weatherDaily,
        airPollution
    )
}