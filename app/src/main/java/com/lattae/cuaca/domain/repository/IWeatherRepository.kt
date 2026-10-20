package com.lattae.cuaca.domain.repository

import com.lattae.cuaca.common.Resource
import com.lattae.cuaca.domain.model.Location
import com.lattae.cuaca.domain.model.Weather
import com.lattae.cuaca.domain.model.WeatherCurrent
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Resource<WeatherCurrent>
    suspend fun getWeatherDetail(lat: Double, lon: Double): Resource<Weather>
    suspend fun getSearchLocation(name: String): Resource<List<Location>>
    suspend fun getSavedLocations(): Flow<List<Location>>?
    suspend fun getLocationById(locationId: Int): Location?
    suspend fun getCachedWeatherByCoord(lat: Double, lon: Double): Location?
    suspend fun getCachedWeather(lat: Double, lon: Double, isCurrentLocation: Boolean): Weather?
    suspend fun deleteLocation(locationId: Int)
    suspend fun saveLocation(weather: Weather, location: Location)
    suspend fun updateCurrentLocationCached(weather: Weather, location: Location)
}