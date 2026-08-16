package com.lattae.theweather.presentation.ui.detail.viewmodel

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lattae.theweather.common.Resource
import com.lattae.theweather.data.mapper.toWeather
import com.lattae.theweather.domain.location.ILocationManager
import com.lattae.theweather.domain.model.Location
import com.lattae.theweather.domain.model.Weather
import com.lattae.theweather.domain.usecase.GetAirPollutionUseCase
import com.lattae.theweather.domain.usecase.SaveLocationUseCase
import com.lattae.theweather.domain.usecase.GetCurrentWeatherUseCase
import com.lattae.theweather.domain.usecase.GetDailyForecastWeatherUseCase
import com.lattae.theweather.domain.usecase.GetSavedLocationsUseCase
import com.lattae.theweather.util.toFullDate
import com.lattae.theweather.util.toTimeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.abs

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val geocoder: Geocoder,
    private val locationManager: ILocationManager,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getDailyForecastWeatherUseCase: GetDailyForecastWeatherUseCase,
    private val getAirPollutionUseCase: GetAirPollutionUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val getSavedLocationsUseCase: GetSavedLocationsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailState())
    val uiState: StateFlow<DetailState> = _uiState.asStateFlow()

    fun onPermissionGranted() {
        _uiState.update { it.copy(hasLocationPermission = true) }
        loadDetailWeather()
    }

    fun loadDetailWeather(
        latitude: Double? = null,
        longitude: Double? = null,
        isCurrentLoc: Boolean? = null) {
        viewModelScope.launch {
            val isCurrentLocation = isCurrentLoc ?: (latitude == null && longitude == null)

            _uiState.update { it.copy(
                detailState = Resource.Loading,
                isCurrentLocation = isCurrentLocation
            ) }

            val (lat, lon) = if (latitude != null && longitude != null) {
                latitude to longitude
            } else {
                val location = locationManager.getCurrentLocation() ?: run {
                    _uiState.update { it.copy(detailState = Resource.Failure("Failed to load location")) }
                    return@launch
                }
                location.latitude to location.longitude
            }

            coroutineScope {
                val savedLocation = async {
                    val result = getSavedLocationsUseCase()
                        .filter { it !is Resource.Loading }
                        .firstOrNull()

                    if (result is Resource.Success) {
                        result.data.any { weather ->
                            val threshold = 0.02
                            abs(weather.lat - lat) < threshold &&
                            abs(weather.lon - lon) < threshold
                        }
                    } else false
                }

                val currentWeather = async {
                    getCurrentWeatherUseCase(lat, lon)
                }
                val dailyForecastWeather = async {
                    getDailyForecastWeatherUseCase(lat, lon)
                }
                val airPollution = async {
                    getAirPollutionUseCase(lat, lon)
                }

                val isSavedLocation = savedLocation.await()
                val currentWeatherResult = currentWeather.await()
                val dailyForecastWeatherResult = dailyForecastWeather.await()
                val airPollutionResult = airPollution.await()

                if (currentWeatherResult is Resource.Success &&
                    dailyForecastWeatherResult is Resource.Success &&
                    airPollutionResult is Resource.Success
                ) {
                    val address = withContext(Dispatchers.IO) {
                        try {
                            getLocationAddress(lat, lon)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    val locationAddress = if (_uiState.value.isCurrentLocation) {
                        address?.let {
                            it.subLocality ?: it.locality ?: it.adminArea ?: it.countryName
                        } ?: currentWeatherResult.data.locationName
                    } else {
                        currentWeatherResult.data.locationName
                    }

                    val timezone = currentWeatherResult.data.timezone
                    val fullDate = toFullDate(currentWeatherResult.data.datetime, timezone)

                    val sunrise = toTimeString(currentWeatherResult.data.sunrise, timezone)
                    val sunset = toTimeString(currentWeatherResult.data.sunset, timezone)

                    val weather = Weather(
                        currentWeatherResult.data,
                        dailyForecastWeatherResult.data,
                        airPollutionResult.data
                    ).toWeather()
                        .copy(
                            weatherCurrent = currentWeatherResult.data.copy(
                                locationName = locationAddress,
                                datetime = fullDate,
                                sunrise = sunrise,
                                sunset = sunset
                            )
                        )

                    _uiState.update {
                        val updatedCurrentWeather = if (isCurrentLocation) {
                            Resource.Success(
                                currentWeatherResult.data.copy(
                                    locationName = locationAddress,
                                )
                            )
                        } else {
                            it.currentLocationWeather
                        }

                        val (sunProgress, isNightMode) = calculateSunProgress(
                            currentWeatherResult.data.sunrise ?: "",
                            currentWeatherResult.data.sunset ?: ""
                        )

                        it.copy(
                            detailState = Resource.Success(weather),
                            currentLocationWeather = updatedCurrentWeather,
                            isCurrentLocation = isCurrentLocation,
                            isSavedLocation = isSavedLocation,
                            sunProgress = sunProgress,
                            isNightMode = isNightMode
                        )
                    }

                    Log.d("Current location:", "$currentWeatherResult")
                } else {
                    val error = (currentWeatherResult as? Resource.Failure)?.error
                        ?: (dailyForecastWeatherResult as? Resource.Failure)?.error
                        ?: (airPollutionResult as? Resource.Failure)?.error

                    _uiState.update {
                        it.copy(
                            detailState = Resource.Failure(error.toString())
                        )
                    }
                }
            }
        }
    }

    fun saveLocation() {
        viewModelScope.launch {
            val state = _uiState.value.detailState
            if (state is Resource.Success) {
                val weather = state.data
                val location = Location(
                    locationName = weather.weatherCurrent?.locationName,
                    lat = weather.weatherCurrent?.lat ?: 0.0,
                    lon = weather.weatherCurrent?.lon ?: 0.0,
                    country = weather.weatherCurrent?.country,
                )
                saveLocationUseCase(location)
                _uiState.update { it.copy(isSavedLocation = true) }
            }
        }
    }

    private fun calculateSunProgress(
        sunrise: String,
        sunset: String
    ): Pair<Float, Boolean> {
        val sunriseEpoch = sunrise.toFloatOrNull() ?: return Pair(0f, false)
        val sunsetEpoch = sunset.toFloatOrNull() ?: return Pair(0f, false)
        val currentEpoch = (System.currentTimeMillis()/1000).toFloat()

        return when {
            currentEpoch in sunriseEpoch..sunsetEpoch -> {
                val total = (sunsetEpoch - sunriseEpoch)
                val elapsed = (currentEpoch - sunriseEpoch)
                val progress = if (total > 0f) (elapsed/total).coerceIn(0f, 1f) else 0f
                Pair(progress, false)
            }
            currentEpoch > sunsetEpoch -> {
                val nextSunriseEpoch = (sunriseEpoch + 86400)
                val total = (nextSunriseEpoch - sunsetEpoch)
                val elapsed = (currentEpoch - sunsetEpoch)
                val progress = if (total > 0f) (elapsed/total).coerceIn(0f, 1f) else 0f
                Pair(progress, true)
            }
            else -> {
                val prevSunsetEpoch = (sunsetEpoch - 86400)
                val total = (sunriseEpoch - prevSunsetEpoch)
                val elapsed = (currentEpoch - prevSunsetEpoch)
                val progress = if (total > 0) (elapsed/total).coerceIn(0f, 1f) else 0f
                Pair(progress, true)
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun getLocationAddress(lat: Double, lon: Double): Address? {
        return suspendCancellableCoroutine { continuation ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lon, 1) { address ->
                        if (address.isNotEmpty()) {
                            continuation.resume(address[0])
                        } else {
                            continuation.resume(null)
                        }
                    }
                } else {
                    val address = geocoder.getFromLocation(lat, lon, 1)
                    if (!address.isNullOrEmpty()) {
                        continuation.resume(address[0])
                    } else {
                        continuation.resume(null)
                    }
                }
            } catch (e: Exception) {
                continuation.resume(null)
            }
        }
    }
}