package com.lattae.cuaca.data.repository

import androidx.room.withTransaction
import com.lattae.cuaca.common.Resource
import com.lattae.cuaca.common.safeApiCall
import com.lattae.cuaca.data.local.LocationDao
import com.lattae.cuaca.data.local.WeatherDatabase
import com.lattae.cuaca.data.mapper.toAirCondition
import com.lattae.cuaca.data.mapper.toLocation
import com.lattae.cuaca.data.mapper.toLocationEntity
import com.lattae.cuaca.data.mapper.toWeather
import com.lattae.cuaca.data.mapper.toWeatherCurrent
import com.lattae.cuaca.data.mapper.toWeatherDaily
import com.lattae.cuaca.data.mapper.toWeatherHourly
import com.lattae.cuaca.data.remote.api.WeatherApiService
import com.lattae.cuaca.domain.model.Location
import com.lattae.cuaca.domain.model.Weather
import com.lattae.cuaca.domain.model.WeatherCurrent
import com.lattae.cuaca.domain.repository.IWeatherRepository
import com.lattae.cuaca.util.generateId
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class WeatherRepository @Inject constructor(
    private val db: WeatherDatabase,
    private val locationDao: LocationDao,
    private val apiService: WeatherApiService
) : IWeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Resource<WeatherCurrent> =
        safeApiCall {
            apiService.getCurrentWeather(lat, lon).toWeatherCurrent()
        }

    override suspend fun getWeatherDetail(lat: Double, lon: Double): Resource<Weather> =
        safeApiCall {
            coroutineScope {
                val currentWeather = async { apiService.getCurrentWeather(lat, lon).toWeatherCurrent() }
                val forecast = async { apiService.getWeatherDailyForecast(lat, lon)}
                val airCondition = async { apiService.getAirCondition(lat, lon).toAirCondition() }

                val forecastResponse = forecast.await()

                Weather(
                    currentWeather.await(),
                    forecastResponse.toWeatherHourly(),
                    forecastResponse.toWeatherDaily(),
                    airCondition.await()
                )
            }
        }

    override suspend fun getSearchLocation(name: String): Resource<List<Location>> = safeApiCall {
        apiService.getLocationName(name).toLocation()
    }

    override suspend fun getSavedLocations(): Flow<List<Location>>? {
        return locationDao.getAllLocations()?.map { it.toLocation() }
    }

    override suspend fun getCachedWeatherByCoord(lat: Double, lon: Double): Location? {
        val locationId = generateId(lat, lon)
        val location = (locationDao.getLocationById(locationId)?.toLocation() ?: return null)
        return location
    }

    override suspend fun getLocationById(locationId: Int): Location? {
        val location = locationDao.getLocationById(locationId)?.toLocation() ?: return null

        return location
    }

    override suspend fun getCachedWeather(
        lat: Double,
        lon: Double,
        isCurrentLocation: Boolean
    ): Weather? {
        return runCatching {
            val cachedEntity = if (isCurrentLocation) {
                locationDao.getCurrentLocationCache()
            } else {
                val locationId = generateId(lat, lon)
                locationDao.getLocationById(locationId)
            }

            cachedEntity?.toWeather()
        }.getOrNull()
    }

    override suspend fun deleteLocation(locationId: Int) {
        locationDao.deleteLocation(locationId)
    }

    override suspend fun saveLocation(weather: Weather, location: Location) {
        weather.toLocationEntity(location).let { entity ->
            locationDao.insertLocation(entity)
        }
    }

    override suspend fun updateCurrentLocationCached(weather: Weather, location: Location) {
        db.withTransaction {
            locationDao.deletePreviousUnsavedCurrentLocation()
            locationDao.unmarkSavedCurrentLocation()

            weather.toLocationEntity(location).let { entity ->
                locationDao.insertLocation(entity)
            }
        }
    }
}