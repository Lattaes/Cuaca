package com.lattae.theweather.data.repository

import com.lattae.theweather.BuildConfig
import com.lattae.theweather.common.Resource
import com.lattae.theweather.data.mapper.toAirPollution
import com.lattae.theweather.data.mapper.toLocation
import com.lattae.theweather.data.mapper.toWeatherCurrent
import com.lattae.theweather.data.mapper.toWeatherDaily
import com.lattae.theweather.data.remote.api.WeatherApiService
import com.lattae.theweather.domain.model.AirPollution
import com.lattae.theweather.domain.model.Location
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.domain.model.WeatherDaily
import com.lattae.theweather.domain.repository.IWeatherRepository
import javax.inject.Inject


class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) : IWeatherRepository {

    private val apiKey = BuildConfig.WEATHER_API

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Resource<WeatherCurrent> {
        return try {
            val weatherCurrent = apiService.getCurrentWeather(lat, lon, apiKey)

            Resource.Success(weatherCurrent.toWeatherCurrent())
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun getDailyForecastWeather(lat: Double, lon: Double): Resource<List<WeatherDaily>> {
        return try {
            val weatherDailyForecast = apiService.getWeatherDailyForecast(lat, lon, apiKey)

            Resource.Success(weatherDailyForecast.toWeatherDaily())
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun getAirPollution(lat: Double, lon: Double): Resource<AirPollution> {
        return try {
            val airPollution = apiService.getAirPollution(lat, lon, apiKey)

            Resource.Success(airPollution.toAirPollution())
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun getSearchLocation(name: String): Resource<List<Location>> {
        return try {
            val searchedLocation = apiService.getLocationName(name, apiKey)

            Resource.Success(searchedLocation.toLocation())
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }
}