package com.lattae.theweather.domain.usecase

import android.util.Log
import com.lattae.theweather.common.Resource
import com.lattae.theweather.domain.model.AirPollution
import com.lattae.theweather.domain.model.Location
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.domain.model.WeatherDaily
import com.lattae.theweather.domain.repository.IWeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<WeatherCurrent> {
        return weatherRepository.getCurrentWeather(lat, lon)
    }
}

class GetDailyForecastWeatherUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<List<WeatherDaily>> {
        return weatherRepository.getDailyForecastWeather(lat, lon)
    }
}

class GetAirPollutionUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<AirPollution> {
        return weatherRepository.getAirPollution(lat, lon)
    }
}

class GetSearchLocationUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository
) {
    suspend operator fun invoke(locationName: String): Resource<List<Location>> {
        return weatherRepository.getSearchLocation(locationName)
    }
}

class GetSavedLocationsUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository
) {
    operator fun invoke(): Flow<Resource<List<WeatherCurrent>>> = flow {
        weatherRepository.getSavedLocations().collect { locations ->
            if (locations.isEmpty()) {
                emit(Resource.Success(emptyList()))
                return@collect
            }
            emit(Resource.Loading)

            val result = try {
                coroutineScope {
                    val weatherList = locations.map { location ->
                        async {
                            val resource = weatherRepository.getCurrentWeather(location.lat, location.lon)
                            (resource as? Resource.Success)?.data
                        }
                    }.awaitAll().filterNotNull()
                    Resource.Success(weatherList)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Resource.Failure(e.message ?: "Unknown error.")
            }
            emit(result)
            Log.d("Saved Location", "$result")
        }
    }
}

class DeleteLocationUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double) {
        weatherRepository.deleteLocation(lat, lon)
    }
}

class SaveLocationUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository
) {
    suspend operator fun invoke(location: Location) {
        weatherRepository.saveLocation(location)
    }
}