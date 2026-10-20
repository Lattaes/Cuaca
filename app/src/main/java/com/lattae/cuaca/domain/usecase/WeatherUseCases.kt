package com.lattae.cuaca.domain.usecase

import com.lattae.cuaca.common.Resource
import com.lattae.cuaca.data.mapper.toLocation
import com.lattae.cuaca.domain.location.ILocationManager
import com.lattae.cuaca.domain.model.Location
import com.lattae.cuaca.domain.model.Weather
import com.lattae.cuaca.domain.repository.IWeatherRepository
import com.lattae.cuaca.util.generateId
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

data class WeatherUseCases(
    val getWeatherDetail: GetWeatherDetailUseCase,
    val getSavedLocations: GetSavedLocationsUseCase,
    val updateSavedLocationWeather: UpdateSavedLocationWeatherUseCase
)


class GetWeatherDetailUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository
) {
    operator fun invoke(
        lat: Double,
        lon: Double,
        isCurrentLocation: Boolean
    ): Flow<Resource<Weather>> = flow {
        val cached = try {
            weatherRepository.getCachedWeather(lat, lon, isCurrentLocation)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }

        if (cached != null) {
            emit(Resource.Success(cached))
        }
        emit(Resource.Loading)

        val location = try {
            weatherRepository.getCachedWeatherByCoord(lat, lon)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }
        val isSavedLocation = location?.isSavedLocation

        when (val resource = weatherRepository.getWeatherDetail(lat, lon)) {
            is Resource.Success -> {
                val weather = resource.data
                val locationToUpdate = weather.weatherCurrent?.toLocation(
                    isCurrentLocation = isCurrentLocation,
                    isSavedLocation = isSavedLocation ?: false
                )

                if (isCurrentLocation) {
                    try {
                        weatherRepository.updateCurrentLocationCached(weather, locationToUpdate ?: Location())
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                    }
                }
                emit(Resource.Success(weather))
            }
            is Resource.Failure -> {
                emit(Resource.Failure(resource.errorMessage))
            }
            is Resource.Loading -> {
                if (cached == null) {
                    emit(Resource.Loading)
                }
            }
        }
    }
}

class GetSavedLocationsUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository
) {
    operator fun invoke(): Flow<Resource<List<Location>>> = flow {
        val savedLocationsFlow = weatherRepository.getSavedLocations()
        if (savedLocationsFlow == null) {
            emit(Resource.Success(emptyList()))
            return@flow
        }

        savedLocationsFlow.collect { locations ->
            if (locations.isEmpty()) {
                emit(Resource.Success(emptyList()))
                return@collect
            }

            emit(Resource.Success(locations))

            try {
                coroutineScope {
                    val weatherList = locations.map { location ->
                        async {
                            try {
                                val resource = weatherRepository.getCurrentWeather(location.lat, location.lon)
                                if (resource is Resource.Success) {
                                    resource.data.toLocation(
                                        id = location.id,
                                        isCurrentLocation = location.isCurrentLocation,
                                        state = location.state
                                    )
                                } else {
                                    location
                                }
                            } catch (e: Exception) {
                                if (e is CancellationException) throw e
                                location
                            }
                        }
                    }.awaitAll()

                    emit(Resource.Success(weatherList))
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                emit(Resource.Success(locations))
            }
        }
    }
}

class UpdateSavedLocationWeatherUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository,
    private val locationManager: ILocationManager
) {
    suspend operator fun invoke() {
        coroutineScope {
            val savedLocations = try {
                weatherRepository.getSavedLocations()?.firstOrNull().orEmpty()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                emptyList()
            }

            val updateSavedLocation = savedLocations.map { location ->
                async {
                    try {
                        val weatherDetail = weatherRepository.getWeatherDetail(location.lat, location.lon)

                        if (weatherDetail is Resource.Success) {
                            weatherRepository.saveLocation(weatherDetail.data, location)
                        }
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                    }
                }
            }

            try {
                val currentLocation = locationManager.getCurrentLocation()
                if (currentLocation != null) {
                    val locationId = generateId(currentLocation.latitude, currentLocation.longitude)
                    val existingCurrentLocation = savedLocations.find { it.id == locationId }

                    val weatherDetail = weatherRepository.getWeatherDetail(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                    if (weatherDetail is Resource.Success) {
                        val weather = weatherDetail.data
                        val current = weather.weatherCurrent

                        if (existingCurrentLocation != null) {
                            val location = existingCurrentLocation.copy(
                                isCurrentLocation = true,
                            )
                            weatherRepository.updateCurrentLocationCached(weather, location)
                        } else {
                            val newLocation = Location(
                                id = current?.locationId ?: locationId,
                                locationName = current?.locationName,
                                isCurrentLocation = true,
                                lat = current?.lat ?: currentLocation.latitude,
                                lon = current?.lon ?: currentLocation.longitude,
                                country = current?.country
                            )
                            weatherRepository.updateCurrentLocationCached(weather, newLocation)
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
            }

            updateSavedLocation.awaitAll()
        }
    }
}