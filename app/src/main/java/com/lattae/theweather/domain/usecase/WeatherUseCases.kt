package com.lattae.theweather.domain.usecase

import com.lattae.theweather.common.Resource
import com.lattae.theweather.data.repository.WeatherRepository
import com.lattae.theweather.domain.model.AirPollution
import com.lattae.theweather.domain.model.Location
import com.lattae.theweather.domain.model.WeatherCurrent
import com.lattae.theweather.domain.model.WeatherDaily
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<WeatherCurrent> {
        return weatherRepository.getCurrentWeather(lat, lon)
    }
}

class GetDailyForecastWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<List<WeatherDaily>> {
        return weatherRepository.getDailyForecastWeather(lat, lon)
    }
}

class GetAirPollutionUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<AirPollution> {
        return weatherRepository.getAirPollution(lat, lon)
    }
}

class GetSearchLocationUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(locationName: String): Resource<List<Location>> {
        return weatherRepository.getSearchLocation(locationName)
    }
}

class GetSavedLocationsUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(): Flow<Resource<List<Pair<Location, WeatherCurrent>>>> = flow {
        weatherRepository.getSavedLocations().collect { locations ->
            if (locations.isEmpty()) {
                emit(Resource.Success(emptyList()))
                return@collect
            }
            emit(Resource.Loading)
            try {
                coroutineScope {
                    val currentWeather = locations.map { location ->
                        async {
                            val resource = weatherRepository.getCurrentWeather(location.lat, location.lon)
                            if (resource is Resource.Success) {
                                location to resource.data
                            } else null
                        }
                    }.awaitAll().filterNotNull()
                    emit(Resource.Success(currentWeather))
                }
            } catch (e: Exception) {
                emit(Resource.Failure(e.message ?: "Unknown error."))
            }
        }
    }
}

class DeleteLocationUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double) {
        weatherRepository.deleteLocation(lat, lon)
    }
}

class SaveLocationUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(location: Location) {
        weatherRepository.saveLocation(location)
    }
}