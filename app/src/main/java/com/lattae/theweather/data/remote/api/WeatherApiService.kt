package com.lattae.theweather.data.remote.api

import com.lattae.weatherapp.data.remote.dto.AirPollutionResponse
import com.lattae.theweather.data.remote.dto.LocationResponse
import com.lattae.weatherapp.data.remote.dto.WeatherCurrentResponse
import com.lattae.weatherapp.data.remote.dto.WeatherDailyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en"
    ): WeatherCurrentResponse

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByLocationName(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en"
    ): WeatherCurrentResponse

    @GET("data/2.5/forecast")
    suspend fun getWeatherDailyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en"
    ): WeatherDailyResponse

    @GET("data/2.5/forecast")
    suspend fun getWeatherDailyForecastByLocationName(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en"
    ): WeatherDailyResponse

    @GET("data/2.5/air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AirPollutionResponse

    @GET("geo/1.0/direct")
    suspend fun getLocationName(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("limit") limit: Int = 15
    ): List<LocationResponse>
}