package com.lattae.cuaca.data.mapper

import com.lattae.cuaca.data.local.AirConditionEntity
import com.lattae.cuaca.data.local.LocationEntity
import com.lattae.cuaca.data.local.WeatherCurrentEntity
import com.lattae.cuaca.domain.model.AirCondition
import com.lattae.cuaca.domain.model.Weather
import com.lattae.cuaca.domain.model.WeatherCurrent
import com.lattae.cuaca.domain.model.WeatherDaily
import com.lattae.cuaca.util.AqiLevel
import com.lattae.cuaca.data.remote.dto.AirConditionResponse
import com.lattae.cuaca.data.remote.dto.WeatherCurrentResponse
import com.lattae.cuaca.data.remote.dto.WeatherDailyResponse
import com.lattae.cuaca.domain.model.Location
import com.lattae.cuaca.domain.model.WeatherHourly
import com.lattae.cuaca.util.fullDateFormatter
import com.lattae.cuaca.util.generateId
import com.lattae.cuaca.util.local
import com.lattae.cuaca.util.toDayName
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.abs

fun WeatherCurrentResponse.toWeatherCurrent(): WeatherCurrent {
    val locationId = generateId(this.coord.lat, this.coord.lon)
    val fullDate = toFullDate(this.dt, this.timezone)
    val sunrise = toTimeString(this.sys.sunrise, this.timezone)
    val sunset = toTimeString(this.sys.sunset, this.timezone)
    val (sunProgress, isNightMode) = calculateSunProgress(
        this.sys.sunrise,
        this.sys.sunset
    )
    val visibility = (this.visibility) / 1000

    return WeatherCurrent(
        locationName = this.name,
        country = this.sys.country,
        lat = this.coord.lat,
        lon = this.coord.lon,
        weatherId = this.weather.firstOrNull()?.id ?: 0,
        fullDate = fullDate,
        timezone = this.timezone.toString(),
        sunrise = sunrise,
        sunset = sunset,
        sunProgress = sunProgress,
        isNightMode = isNightMode,
        locationId = locationId,
        weatherType = this.weather.firstOrNull()?.main,
        icon = this.weather.firstOrNull()?.icon,
        temp = this.main.temp.toInt(),
        tempMin = this.main.tempMin.toInt(),
        tempMax = this.main.tempMax.toInt(),
        feelsLike = this.main.feelsLike.toInt(),
        rainVolume = this.rain?.oneHour ?: 0.0,
        windSpeed = this.wind.speed,
        visibility = visibility,
        clouds = this.clouds.allClouds,
        humidity = this.main.humidity,
        airPressure = this.main.pressure
    )
}

fun AirConditionResponse.toAirCondition(): AirCondition {
    val aqiValue = this.listAirCondition.firstOrNull()?.main?.aqi ?: 2
    val aqiLevel = AqiLevel.fromScore(aqiValue.toInt())

    return AirCondition(
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
                datetime = LocalDate.parse(dateString).toDayName(),
                icon = noonItem.weather.firstOrNull()?.icon,
                tempMin = items.minOf { it.main.tempMin.toInt() },
                tempMax = items.maxOf { it.main.tempMax.toInt() }
            )
        }
}

fun WeatherDailyResponse.toWeatherHourly(): List<WeatherHourly> {
    val dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val hourFormat = DateTimeFormatter.ofPattern("HH:mm")
    val timezoneOffset = ZoneOffset.ofTotalSeconds(this.city.timezone)
    val todayAtLocation = Instant.now()
        .atOffset(timezoneOffset)
        .toLocalDate()

    return this.listWeatherDaily
        .map { item ->
            val localDate = LocalDateTime
                .parse(item.dtTxt, dtFormat)
                .atOffset(ZoneOffset.UTC)
                .withOffsetSameInstant(timezoneOffset)
                .toLocalDateTime()

            localDate to item
        }
        .filter { (localDate, _) -> localDate.toLocalDate() >= todayAtLocation}
        .take(12)
        .map { (localDate, item) ->
            WeatherHourly(
                datetime = localDate.format(hourFormat),
                icon = item.weather.firstOrNull()?.icon,
                temp = item.main.temp.toInt()
            )
        }
}

fun Weather.toLocationEntity(
    location: Location
) : LocationEntity {
    val weatherCurrent = this.weatherCurrent.let { current ->
        WeatherCurrentEntity(
            weatherId = current?.weatherId ?: 0,
            datetime = current?.fullDate,
            sunrise = current?.sunrise,
            sunset = current?.sunset,
            sunProgress = current?.sunProgress ?: 0f,
            isNightMode = current?.isNightMode ?: false,
            timezone = current?.timezone,
            weatherType = current?.weatherType,
            icon = current?.icon,
            temp = current?.temp ?: 0,
            tempMin = current?.tempMin ?: location.tempMin,
            tempMax = current?.tempMax ?: location.tempMax,
            feelsLike = current?.feelsLike ?: 0,
            rainVolume = current?.rainVolume ?: 0.0,
            windSpeed = current?.windSpeed ?: 0.0,
            visibility = current?.visibility ?: 0,
            clouds = current?.clouds ?: 0,
            humidity = current?.humidity ?: 0,
            airPressure = current?.airPressure ?:0
        )
    }

    val hourlyForecastRaw = this.weatherHourly?.joinToString("|") { hourly ->
        "${hourly.datetime},${hourly.icon},${hourly.temp}"
    }

    val dailyForecastRaw = this.weatherDaily?.joinToString("|") { daily ->
        "${daily.datetime},${daily.icon},${daily.tempMin},${daily.tempMax}"
    }

    val airCondition = this.airCondition?.let { air ->
        AirConditionEntity(
            aqiLevel = this.airCondition.aqiLevel.score ?: 0,
            aqiDesc = this.airCondition.aqiDesc
        )
    }

    return LocationEntity(
        locationId = this.weatherCurrent?.locationId ?: location.id,
        locationName = this.weatherCurrent?.locationName ?: "",
        country = this.weatherCurrent?.country,
        isCurrentLocation = location.isCurrentLocation,
        isSaved = location.isSavedLocation,
        lat = this.weatherCurrent?.lat ?: location.lat,
        lon = this.weatherCurrent?.lon ?: location.lon,
        weatherCurrent = weatherCurrent,
        hourlyForecast = hourlyForecastRaw,
        dailyForecast = dailyForecastRaw,
        airCondition = airCondition
    )
}

@JvmName("toWeatherFromEntity")
fun LocationEntity.toWeather(): Weather {
    val current = weatherCurrent?.let { currentEntity ->
        WeatherCurrent(
            locationId = this.locationId,
            locationName = this.locationName,
            lat = this.lat,
            lon = this.lon,
            weatherId = currentEntity.weatherId,
            fullDate = currentEntity.datetime,
            sunrise = currentEntity.sunrise,
            sunset = currentEntity.sunset,
            sunProgress = currentEntity.sunProgress,
            isNightMode = currentEntity.isNightMode,
            weatherType = currentEntity.weatherType,
            icon = currentEntity.icon,
            temp = currentEntity.temp,
            tempMin = currentEntity.tempMin,
            tempMax = currentEntity.tempMax,
            feelsLike = currentEntity.feelsLike,
            rainVolume = currentEntity.rainVolume,
            windSpeed = currentEntity.windSpeed,
            visibility = currentEntity.visibility,
            clouds = currentEntity.clouds,
            humidity = currentEntity.humidity,
            airPressure = currentEntity.airPressure
        )
    }

    val hourlyForecast = hourlyForecast?.split("|")?.mapNotNull { item ->
        val parts = item.split(",")
        if (parts.size == 3) {
            WeatherHourly(
                datetime = parts[0],
                icon = parts[1],
                temp = parts[2].toIntOrNull() ?: 0
            )
        } else null
    } ?: emptyList()

    val dailyForecast = dailyForecast?.split("|")?.mapNotNull { item ->
        val parts = item.split(",")
        if (parts.size == 4) {
            WeatherDaily(
                datetime = parts[0],
                icon = parts[1],
                tempMin = parts[2].toIntOrNull() ?: 0,
                tempMax = parts[3].toIntOrNull() ?: 0
            )
        } else null
    } ?: emptyList()

    val airCondition = airCondition?.let { air ->
        AirCondition(
            aqiLevel = AqiLevel.fromScore(air.aqiLevel),
            aqiDesc = air.aqiDesc
        )
    }

    return Weather(
        current,
        hourlyForecast,
        dailyForecast,
        airCondition
    )
}

private fun toFullDate(dateTime: Long, timezone: Int): String =
    Instant.ofEpochSecond(dateTime)
        .atZone(ZoneOffset.ofTotalSeconds(timezone))
        .format(fullDateFormatter)


private fun toTimeString(time: Long, timezone: Int): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", local)

    return Instant.ofEpochSecond(time)
        .atZone(ZoneOffset.ofTotalSeconds(timezone))
        .format(formatter)
}

private fun calculateSunProgress(
    sunrise: Long,
    sunset: Long
): Pair<Float, Boolean> {
    val sunriseEpoch = sunrise.toFloat()
    val sunsetEpoch = sunset.toFloat()
    val currentEpoch = (System.currentTimeMillis()/1000).toFloat()

    return when {
        currentEpoch in sunriseEpoch..sunsetEpoch -> {
            val total = (sunsetEpoch - sunriseEpoch)
            val elapsed = (currentEpoch - sunriseEpoch)
            val progress = if (total > 0f) (elapsed/total).coerceIn(0f, 1f) else 0f
            Pair(progress, false)
        }
        currentEpoch > sunsetEpoch -> {
            val nextSunriseEpoch = (sunriseEpoch + 86400)
            val total = (nextSunriseEpoch - sunsetEpoch)
            val elapsed = (currentEpoch - sunsetEpoch)
            val progress = if (total > 0f) (elapsed/total).coerceIn(0f, 1f) else 0f
            Pair(progress, true)
        }
        else -> {
            val prevSunsetEpoch = (sunsetEpoch - 86400)
            val total = (sunriseEpoch - prevSunsetEpoch)
            val elapsed = (currentEpoch - prevSunsetEpoch)
            val progress = if (total > 0) (elapsed/total).coerceIn(0f, 1f) else 0f
            Pair(progress, true)
        }
    }
}