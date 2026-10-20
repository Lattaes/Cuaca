package com.lattae.cuaca.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey @ColumnInfo("location_id") val locationId: Int,
    @ColumnInfo("location_name") val locationName: String,
    @ColumnInfo("country") val country: String?,
    @ColumnInfo("is_current_location") val isCurrentLocation: Boolean = false,
    @ColumnInfo("is_saved") val isSaved: Boolean = false,
    @ColumnInfo("lat") val lat: Double,
    @ColumnInfo("lon") val lon: Double,
    @Embedded("current_") val weatherCurrent: WeatherCurrentEntity? = null,
    @ColumnInfo("hourly_forecast") val hourlyForecast: String? = null,
    @ColumnInfo("daily_forecast") val dailyForecast: String? = null,
    @Embedded("air_") val airCondition: AirConditionEntity? = null,
    @ColumnInfo("updated_at") val updatedAt: Long = System.currentTimeMillis()
)

data class WeatherCurrentEntity(
    @ColumnInfo("weather_id") val weatherId: Int = 0,
    @ColumnInfo("datetime") val datetime: String? = null,
    @ColumnInfo("sunrise") val sunrise: String? = null,
    @ColumnInfo("sunset") val sunset: String? = null,
    @ColumnInfo("sun_progress") val sunProgress: Float = 0f,
    @ColumnInfo("is_night_mode") val isNightMode: Boolean = false,
    @ColumnInfo("timezone") val timezone: String? = null,
    @ColumnInfo("weather_type") val weatherType: String? = null,
    @ColumnInfo("icon") val icon: String? = null,
    @ColumnInfo("temp") val temp: Int = 0,
    @ColumnInfo("temp_min") val tempMin: Int = 0,
    @ColumnInfo("temp_max") val tempMax: Int = 0,
    @ColumnInfo("feels_like") val feelsLike: Int = 0,
    @ColumnInfo("rain_volume") val rainVolume: Double = 0.0,
    @ColumnInfo("wind_speed") val windSpeed: Double = 0.0,
    @ColumnInfo("visibility") val visibility: Int = 0,
    @ColumnInfo("clouds") val clouds: Int = 0,
    @ColumnInfo("humidity") val humidity: Int = 0,
    @ColumnInfo("air_pressure") val airPressure: Int = 0
)

data class AirConditionEntity(
    @ColumnInfo("aqi_level") val aqiLevel: Int = 2,
    @ColumnInfo("aqi_desc") val aqiDesc: String? = null
)