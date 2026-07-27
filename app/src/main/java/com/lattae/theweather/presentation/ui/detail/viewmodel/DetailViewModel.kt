package com.lattae.theweather.presentation.ui.detail.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lattae.theweather.common.Resource
import com.lattae.theweather.data.mapper.toWeather
import com.lattae.theweather.domain.location.ILocationManager
import com.lattae.theweather.domain.model.Weather
import com.lattae.theweather.domain.usecase.GetAirPollutionUseCase
import com.lattae.theweather.domain.usecase.GetCurrentWeatherUseCase
import com.lattae.theweather.domain.usecase.GetDailyForecastWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class DetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationManager: ILocationManager,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getDailyForecastWeatherUseCase: GetDailyForecastWeatherUseCase,
    private val getAirPollutionUseCase: GetAirPollutionUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailState())
    val uiState: StateFlow<DetailState> = _uiState.asStateFlow()

    fun onPermissionGranted() {
        _uiState.update { it.copy(hasLocationPermission = true) }
        loadDetailWeather()
    }

    fun loadDetailWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(state = Resource.Loading) }

            val location = locationManager.getCurrentLocation() ?: run {
                _uiState.update { it.copy(state = Resource.Failure("Failed to load location")) }
                return@launch
            }

            coroutineScope {
                val currentWeather = async {
                    getCurrentWeatherUseCase(location.latitude, location.longitude)
                }

                val dailyForecastWeather = async {
                    getDailyForecastWeatherUseCase(location.latitude, location.longitude)
                }

                val airPollution = async {
                    getAirPollutionUseCase(location.latitude, location.longitude)
                }

                val currentWeatherResult = currentWeather.await()
                val dailyForecastWeatherResult = dailyForecastWeather.await()
                val airPollutionResult = airPollution.await()

                if (currentWeatherResult is Resource.Success &&
                    dailyForecastWeatherResult is Resource.Success &&
                    airPollutionResult is Resource.Success
                    ) {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val address = getLocationAddress(geocoder, location.latitude, location.longitude)
                        val locationAddress = address.subLocality
                                            ?: address.locality
                                            ?: address.adminArea
                                            ?: address.countryName
                                            ?: (currentWeatherResult.data.locationName)

                        val weather = Weather(
                            currentWeatherResult.data,
                            dailyForecastWeatherResult.data,
                            airPollutionResult.data
                        ).toWeather()
                            .copy(
                                weatherCurrent = currentWeatherResult.data.copy(
                                    locationName = locationAddress
                                )
                            )
                    _uiState.update { it.copy(state = Resource.Success(weather)) }
                } else {
                    val error = (currentWeatherResult as? Resource.Failure)?.error
                        ?: (dailyForecastWeatherResult as? Resource.Failure)?.error
                        ?: (airPollutionResult as? Resource.Failure)?.error

                    _uiState.update { it.copy(state = Resource.Failure(error.toString())) }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun getLocationAddress(geocoder: Geocoder, lat: Double, lon: Double): Address {
        return suspendCancellableCoroutine {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lon, 1) { address ->
                    if (address.isNotEmpty()) {
                        it.resume(address[0])
                    } else {
                        it.resumeWithException(Exception("Address not found"))
                    }
                }
            } else {
                val address = geocoder.getFromLocation(lat, lon, 1)
                if (!address.isNullOrEmpty()) {
                    it.resume(address[0])
                } else {
                    it.resumeWithException(Exception("Address not found"))
                }
            }
        }
    }
}