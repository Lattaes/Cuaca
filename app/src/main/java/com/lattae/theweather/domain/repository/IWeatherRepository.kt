package com.lattae.theweather.domain.repository

import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.AirPollution
import com.lattae.theweather.domain.model.Location
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.domain.model.WeatherDaily
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Resource<WeatherCurrent>
    suspend fun getDailyForecastWeather(lat: Double, lon: Double): Resource<List<WeatherDaily>>
    suspend fun getAirPollution(lat: Double, lon: Double): Resource<AirPollution>
    suspend fun getSearchLocation(name: String): Resource<List<Location>>
    suspend fun getSavedLocations(): Flow<List<Location>>
    suspend fun deleteLocation(lat: Double, lon: Double)
    suspend fun saveLocation(location: Location)
}