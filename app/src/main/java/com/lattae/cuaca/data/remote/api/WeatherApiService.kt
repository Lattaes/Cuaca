package com.lattae.cuaca.data.remote.api

import com.lattae.cuaca.data.remote.dto.AirConditionResponse
import com.lattae.cuaca.data.remote.dto.LocationResponse
import com.lattae.cuaca.data.remote.dto.WeatherCurrentResponse
import com.lattae.cuaca.data.remote.dto.WeatherDailyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherCurrentResponse

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByLocationName(
        @Query("q") location: String
    ): WeatherCurrentResponse

    @GET("data/2.5/forecast")
    suspend fun getWeatherDailyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherDailyResponse

    @GET("data/2.5/forecast")
    suspend fun getWeatherDailyForecastByLocationName(
        @Query("q") location: String
    ): WeatherDailyResponse

    @GET("data/2.5/air_pollution")
    suspend fun getAirCondition(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): AirConditionResponse

    @GET("geo/1.0/direct")
    suspend fun getLocationName(
        @Query("q") location: String,
        @Query("limit") limit: Int = 15
    ): List<LocationResponse>
}